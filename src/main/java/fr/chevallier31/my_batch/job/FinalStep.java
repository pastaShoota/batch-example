package fr.chevallier31.my_batch.job;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import fr.chevallier31.my_batch.PostingManager;

@Configuration
public class FinalStep {

    @Bean(name = "Cleanup")
    public Tasklet tasklet(FileSystemResource inputFile,
            PostingManager postingManager) {
        return (c, cc) -> {
            postingManager.finalizePosting();
            return RepeatStatus.FINISHED;
        };
    }

    @Bean(name = "step3")
    public Step step3(JobRepository jobRepository,
    DataSourceTransactionManager transactionManager,
    @Qualifier("Cleanup")Tasklet tasklet) {
        return new StepBuilder("3- Cleanup", jobRepository)
            .tasklet(tasklet, transactionManager)
            .build();
    }
}
