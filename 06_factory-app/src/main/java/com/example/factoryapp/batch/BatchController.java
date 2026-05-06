package com.example.factoryapp.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job dailySummaryJob;

    public BatchController(JobLauncher jobLauncher, Job dailySummaryJob) {
        this.jobLauncher = jobLauncher;
        this.dailySummaryJob = dailySummaryJob;
    }

    @PostMapping("/api/batch/run")
    public ResponseEntity<?> run() throws Exception {
        JobParameters params = new JobParametersBuilder()
            .addLong("run.id", System.currentTimeMillis())
            .toJobParameters();

        jobLauncher.run(dailySummaryJob, params);
        return ResponseEntity.ok(Map.of("message", "バッチ実行完了。output/ に CSV を出力しました。"));
    }
}
