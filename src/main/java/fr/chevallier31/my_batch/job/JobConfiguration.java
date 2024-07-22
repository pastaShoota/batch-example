package fr.chevallier31.my_batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.chevallier31.my_batch.JobCompletionNotificationListener;

@Configuration
public class JobConfiguration {
    
    @Bean
    public Job importUserJob(JobRepository jobRepository, 
    @Qualifier("step0")Step step0, 
    @Qualifier("step1")Step step1, 
    @Qualifier("step3")Step step3, 
    JobCompletionNotificationListener listener) {
        return new JobBuilder("importUserJob", jobRepository)
                .listener(listener)
                .start(step0)
                .next(step1)
                .next(step3)
                .build();
    }

}
