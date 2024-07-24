package fr.chevallier31.my_batch.job;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
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
public class StepZero {
    
    
    @Bean(name = "step0")
    public Step step0(JobRepository jobRepository,
    DataSourceTransactionManager transactionManager,
    @Qualifier("savePostingId") StepExecutionListener savePostingId,
    @Qualifier("createPostingInfo")Tasklet tasklet) {
        return new StepBuilder("0- Get file info", jobRepository)
        .tasklet(tasklet, transactionManager)
        .listener(savePostingId)
        .build();
    }
    
    @Bean(name = "createPostingInfo")
    public Tasklet tasklet(FileSystemResource inputFile,
    PostingManager postingManager) {
        return new Tasklet(){
            @Override
            public RepeatStatus execute(StepContribution sc, ChunkContext cc) {
                int postingId = postingManager.createPostingInfo(inputFile.getFile());
                // Store the posting ID in context so it can be linked to the soon inserted points data  
                sc.getStepExecution().getExecutionContext().put("postingId", postingId);
                return RepeatStatus.FINISHED;
            }
        };
    }
    
    // Store the posting ID in context so it can be linked to the soon inserted points data  
    @Bean(name = "savePostingId")
    public StepExecutionListener savePostingId() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"postingId"});
        return listener;
    }
}
