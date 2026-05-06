package com.example.factoryapp.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class BatchConfig {

    private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);

    @Bean
    @StepScope
    public JdbcCursorItemReader<DailySummaryItem> productionReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<DailySummaryItem>()
            .name("productionReader")
            .dataSource(dataSource)
            .sql("""
                SELECT
                    TO_CHAR(result_date, 'YYYY-MM-DD') AS result_date,
                    product_code,
                    SUM(production_qty)  AS total_qty,
                    SUM(defect_qty)      AS total_defects,
                    ROUND(SUM(defect_qty) * 100.0 / SUM(production_qty), 2) AS defect_rate
                FROM production_results
                GROUP BY result_date, product_code
                ORDER BY result_date, product_code
                """)
            .rowMapper((rs, rowNum) -> new DailySummaryItem(
                rs.getString("result_date"),
                rs.getString("product_code"),
                rs.getInt("total_qty"),
                rs.getInt("total_defects"),
                rs.getDouble("defect_rate")
            ))
            .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<DailySummaryItem> csvWriter() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String outputPath = "output/daily_summary_" + timestamp + ".csv";

        return new FlatFileItemWriterBuilder<DailySummaryItem>()
            .name("csvWriter")
            .resource(new FileSystemResource(outputPath))
            .delimited()
            .delimiter(",")
            .names("resultDate", "productCode", "totalQty", "totalDefects", "defectRate")
            .headerCallback(writer -> writer.write("日付,製品コード,総生産数,不良数,不良率(%)"))
            .build();
    }

    @Bean
    public Step dailySummaryStep(JobRepository jobRepository,
                                  PlatformTransactionManager txManager,
                                  JdbcCursorItemReader<DailySummaryItem> productionReader,
                                  FlatFileItemWriter<DailySummaryItem> csvWriter) {
        return new StepBuilder("dailySummaryStep", jobRepository)
            .<DailySummaryItem, DailySummaryItem>chunk(10, txManager)
            .reader(productionReader)
            .writer(csvWriter)
            .listener(new StepExecutionListener() {
                @Override
                public void beforeStep(StepExecution stepExecution) {
                    log.info("=== CSV バッチ開始 ===");
                }
                @Override
                public ExitStatus afterStep(StepExecution stepExecution) {
                    log.info("=== CSV バッチ終了: {} 件処理 ===", stepExecution.getWriteCount());
                    return ExitStatus.COMPLETED;
                }
            })
            .build();
    }

    @Bean
    public Job dailySummaryJob(JobRepository jobRepository, Step dailySummaryStep) {
        return new JobBuilder("dailySummaryJob", jobRepository)
            .start(dailySummaryStep)
            .build();
    }
}
