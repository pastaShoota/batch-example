package fr.chevallier31.my_batch;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class PostingManager {

    private static final Logger logger = LoggerFactory.getLogger(PostingManager.class);

    private JdbcTemplate jdbcTemplate;
    private KeyHolder keyHolder;
    private java.sql.Timestamp startTime;
    private String filename;

    public PostingManager(JdbcTemplate jdbcTemplate, KeyHolder keyHolder) {
        this.jdbcTemplate = jdbcTemplate;
        this.keyHolder = keyHolder;
    }

    public Integer createPostingInfo(File inputFile){
        filename = inputFile.getName();
        startTime = new java.sql.Timestamp(System.currentTimeMillis());
        final String sql = """
            INSERT INTO POSTINGS (filename, start_processed, status)
            VALUES (? , ?, 'STARTED')
        """;
        
        logger.info("inserting info for file " + filename);
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException{
                return connection.prepareStatement(sql, new String[]{"id"});
            }
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public void finalizePosting() {
        logger.info("finalizing posting " + filename);
        
        java.sql.Timestamp endTime = new java.sql.Timestamp(System.currentTimeMillis());
        
        jdbcTemplate.update("""
            UPDATE POSTINGS SET end_processed = ?, STATUS = 'DONE'
            WHERE filename = ? AND start_processed = ?
            """, endTime, filename, startTime);

        logger.info("truncating POINTS_TRANSIENT");
        jdbcTemplate.execute("TRUNCATE TABLE POINTS_TRANSIENT");
    }
}
