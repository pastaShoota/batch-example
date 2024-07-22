package fr.chevallier31.my_batch.job;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import fr.chevallier31.my_batch.Points;

@Configuration
public class StepTwo {
    private static final int CHUNK_SIZE = 100;

    @Bean
    public JdbcPagingItemReader<Points> pointsReader(DataSource dataSource) {
        Map<String,Order> sortKeys = new HashMap<String, Order>();
        sortKeys.put("activity_date", Order.ASCENDING);
        sortKeys.put("fidelity_number", Order.ASCENDING);
        sortKeys.put("first_name", Order.ASCENDING);
        sortKeys.put("last_name", Order.ASCENDING);
        sortKeys.put("fidelity_code", Order.ASCENDING);

        return new JdbcPagingItemReaderBuilder<Points>()
        .dataSource(dataSource)
        .fetchSize(CHUNK_SIZE)
        .pageSize(CHUNK_SIZE)
        .name("pointsReader")
        .selectClause("""
            activity_date AS activityDate, fidelity_number AS fidelityNumber, 
            first_name AS firstName, last_name AS lastName, 
            fidelity_code AS fidelityCode, SUM(points) AS points
            """)
        .fromClause("POINTS_TRANSIENT")
        .sortKeys(sortKeys)
        .groupClause("""
            activity_date, fidelity_number, first_name, last_name, fidelity_code
            """)
        .rowMapper(new DataClassRowMapper<>(Points.class))
        .build();
    }

    @Bean(name = "PointsWriter")
    public JdbcBatchItemWriter<Points> pointsWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Points>()
            .sql("""
                INSERT INTO POINTS (
                    activity_date,fidelity_number,first_name,last_name,fidelity_code,points
                ) VALUES (
                    :activityDate,:fidelityNumber,:firstName,:lastName,:fidelityCode,:points
                )
                """)
            .dataSource(dataSource)
            .beanMapped()
            .build();
    }

    @Bean(name = "step2")
    public Step step2(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
    JdbcPagingItemReader<Points> reader, @Qualifier("PointsWriter")JdbcBatchItemWriter<Points> writer
    ) {
        return new StepBuilder("2- Group by holder", jobRepository)
            .<Points,Points>chunk(CHUNK_SIZE, transactionManager)
            .reader(reader)
            .writer(writer)
            .build();
    }
}
