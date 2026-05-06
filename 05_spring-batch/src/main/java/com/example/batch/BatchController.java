package com.example.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

// バッチを手動で起動するエンドポイント
// POST /batch/run → ジョブが動いて CSV が出力される
@RestController
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job dailySummaryJob;

    public BatchController(JobLauncher jobLauncher, Job dailySummaryJob) {
        this.jobLauncher = jobLauncher;
        this.dailySummaryJob = dailySummaryJob;
    }

    @PostMapping("/batch/run")
    public ResponseEntity<String> run() throws Exception {
        // 毎回ユニークなパラメータで実行（同じJobを再実行するために必要）
        JobParameters params = new JobParametersBuilder()
            .addLong("run.id", System.currentTimeMillis())
            .toJobParameters();

        jobLauncher.run(dailySummaryJob, params);
        return ResponseEntity.ok("バッチ実行完了。output/ フォルダに CSV が出力されました。");
    }
}
