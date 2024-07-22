package fr.chevallier31.my_batch;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
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
                    fidelity_code, points FROM POINTS_TRANSIENT
                    """,
                    new DataClassRowMapper<Points>(Points.class)
                    ).forEach((points) -> {
                        logger.info("found <{{}}> in the database", points);
                    });

                jdbcTemplate.query("SELECT filename, end_processed, status FROM POSTINGS", (rs) -> {
                    while (rs.next()) {
                        String filename = rs.getString(1);
                        Timestamp date = rs.getTimestamp(2);
                        String status = rs.getString(3);
                        logger.info("Posting inserted: " + filename + " " + status + " at " + date);
                    }
                    return "";
                });
            } catch(Exception e) {
                logger.error("jdbctemplate query failed", e);
            }
        }
    }
    
}
