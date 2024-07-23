package fr.chevallier31.my_batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            logger.info("Job complete !");
        } else {
            logger.warn("Job aborted ! " + jobExecution.getFailureExceptions().stream()
                .map(Throwable::getMessage)
                .reduce("", (acc, newVal) -> String.join(", ", acc, newVal))
            );
            // TODO notify SSN
        }
    }
}
