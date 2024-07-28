package fr.chevallier31.my_batch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.ResultSet;
import java.sql.Timestamp;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@SpringBatchTest
class MyBatchApplicationTests {
	private static Logger logger = LoggerFactory.getLogger(MyBatchApplicationTests.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	void contextLoads() throws Exception { // @see input data in the test/resources
		jobLauncherTestUtils.launchJob(testParameters());
		logResults();
		verifyResults();
	}

	private JobParameters testParameters() {
		return new JobParametersBuilder()
		.addString("posting.filepath", "src/test/resources/input.csv")
		.toJobParameters();
	}

	private void verifyResults() {
		Object pointsCount = jdbcTemplate.query("SELECT COUNT(*) FROM POINTS",
				(ResultSet rs) -> {
					rs.first();
					return rs.getInt(1);
				});
		assertEquals(2, pointsCount);

		int summedPoints = 1330 + 1320;
		Object actualSummedPoints = jdbcTemplate.query(
				"SELECT SUM(points) points FROM POINTS WHERE last_name = 'Macopine'",
				(ResultSet rs) -> {
					rs.first();
					return rs.getInt("points");
				});
		assertEquals(summedPoints, actualSummedPoints);

		Object countWithJoin = jdbcTemplate.query(
				"SELECT COUNT(*) points FROM POINTS pnt JOIN POSTINGS pst ON pst.id = pnt.posting_id",
				(ResultSet rs) -> {
					rs.first();
					return rs.getInt(1);
				});
		assertEquals(2, countWithJoin);

	}

	private void logResults() {
		try {
			jdbcTemplate.query(
					"""
							SELECT activity_date, fidelity_number, first_name, last_name,
							fidelity_code, points FROM POINTS
							""",
					new DataClassRowMapper<Points>(Points.class)).forEach((points) -> {
						logger.info("firstName: " + points.getFirstName() + "EOF");
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
		} catch (Exception e) {
			logger.error("problem while querying test results");
		}
	}

}
