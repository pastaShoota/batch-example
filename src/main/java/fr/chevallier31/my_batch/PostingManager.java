package fr.chevallier31.my_batch;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostingManager {

    private static final Logger logger = LoggerFactory.getLogger(PostingManager.class);

    private JdbcTemplate jdbcTemplate;
    private java.sql.Timestamp startTime;
    private String filename;

    public PostingManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createPostingInfo(File inputFile){
        filename = inputFile.getName();
        startTime = new java.sql.Timestamp(System.currentTimeMillis());
        
        logger.info("inserting info for file " + filename);
        jdbcTemplate.update("""
            INSERT INTO POSTINGS (filename, start_processed, status)
            VALUES (? , ?, 'STARTED')
            """, filename, startTime);
    }

    public void finalizePosting() {
        logger.info("finalizing posting " + filename);
        
        java.sql.Timestamp endTime = new java.sql.Timestamp(System.currentTimeMillis());
        
        jdbcTemplate.update("""
            UPDATE POSTINGS SET end_processed = ?, STATUS = 'DONE'
            WHERE filename = ? AND start_processed = ?
            """, endTime, filename, startTime);

        logger.info("truncating POINTS_TRANSIENT");
        // TODO
    }
}
