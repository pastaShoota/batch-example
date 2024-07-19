package fr.chevallier31.my_batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private JdbcTemplate jdbcTemplate;

    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            logger.info("Job finished! time to verify the results");

            try {

                jdbcTemplate.query(
                    """
                    SELECT activity_date, fidelity_number, first_name, last_name,
                    fidelity_code, points FROM POINTS
                    """,
                    new DataClassRowMapper<Points>(Points.class)
                    ).forEach((points) -> {
                        logger.info("found <{{}}> in the database", points);
                    });
            } catch(Exception e) {
                logger.error("jdbctemplate query failed", e);
            }
        }
    }
    
}
