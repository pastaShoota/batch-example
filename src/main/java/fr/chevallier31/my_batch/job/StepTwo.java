package fr.chevallier31.my_batch.job;

import java.util.LinkedHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import fr.chevallier31.my_batch.Points;

@Configuration
public class StepTwo {
    @Value("${chunk.size:100}")
    private int chunkSize;

    private static final Logger logger = LoggerFactory.getLogger(StepTwo.class);

    @Bean
    public JdbcPagingItemReader<Points> pointsReader(DataSource dataSource) {
        LinkedHashMap<String,Order> sortKeys = new LinkedHashMap<String, Order>();
        sortKeys.put("activityDate", Order.ASCENDING);
        sortKeys.put("fidelityNumber", Order.ASCENDING);
        sortKeys.put("firstName", Order.ASCENDING);
        sortKeys.put("lastName", Order.ASCENDING);
        sortKeys.put("fidelityCode", Order.ASCENDING);

        return new JdbcPagingItemReaderBuilder<Points>()
        .dataSource(dataSource)
        .fetchSize(chunkSize)
        .pageSize(chunkSize)
        .name("pointsReader")
        .selectClause("""
            activity_date AS activityDate, fidelity_number AS fidelityNumber, 
            first_name AS firstName, last_name AS lastName, 
            fidelity_code AS fidelityCode, SUM(points) AS points
            """)
        .fromClause("POINTS_TRANSIENT")
        .sortKeys(sortKeys)
        .groupClause(String.join(",",sortKeys.keySet()))
        .rowMapper(new DataClassRowMapper<>(Points.class))
        .build();
    }

    @Bean(name = "step2")
    @JobScope
    public Step step2(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
    JdbcPagingItemReader<Points> reader,
    @Value("#{jobParameters['ignore.duplicates']}") Boolean ignoreDuplicates,
    DataSource dataSource
    ) {
        SimpleStepBuilder<Points,Points> step = new StepBuilder("2- Group by holder", jobRepository)
        .<Points,Points>chunk(chunkSize, transactionManager)
        .reader(reader)
        .writer(new StepTwoWriter(dataSource));
        if (ignoreDuplicates != null && ignoreDuplicates) {
            logger.info("Beware: duplicate rows will be silently skipped");
            step = step.faultTolerant()
                .skip(DuplicateKeyException.class)
                .skipLimit(2*chunkSize); // ignore up to a certain point
        }
        return step.build();
    }


    private static class StepTwoWriter extends JdbcBatchItemWriter<Points> implements StepExecutionListener {

        public StepTwoWriter(DataSource dataSource) {
            super();
            super.setDataSource(dataSource);
            super.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
            super.usingNamedParameters = true;
        }

        @Override
        public void beforeStep(StepExecution stepExecution) {
            int postingId = stepExecution.getJobExecution().getExecutionContext().getInt("postingId");
            super.setSql(
                String.format(
                    """
                    INSERT INTO POINTS (
                    activity_date,fidelity_number,first_name,last_name,fidelity_code,points,status, posting_id
                ) VALUES (
                    :activityDate,:fidelityNumber,:firstName,:lastName,:fidelityCode,:points,'SENT', %d
                )
                    """
                , postingId)
            );
        }
    }
}
