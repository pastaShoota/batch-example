package fr.chevallier31.my_batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import fr.chevallier31.my_batch.conversion.ConversionConfig.PointsMapper;
import fr.chevallier31.my_batch.domain.PointsRecord;

@Configuration
public class BatchConfiguration {

    @Bean
    public FlatFileItemReader<Points> reader(PointsMapper pointsMapper) {
        return new FlatFileItemReaderBuilder<Points>()
                .name("points reader")
                .resource(new ClassPathResource("input.csv"))
                .linesToSkip(1)
                .fixedLength()
                .columns(new Range(65, 72), new Range(10, 16),
                        new Range(25, 44), new Range(45, 64),
                        new Range(87, 94), new Range(73, 84),
                        new Range(95, 119))
                .names("activityDate", "fidelityNumber",
                        "firstName", "lastName", "fidelityCode", "points", "filler")
                .fieldSetMapper(pointsMapper)
                .build();
    }

    @Bean
    public PointsItemProcessor processor() {
        return new PointsItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Points> writer(DataSource dataSource) {
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

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("importUserJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
            FlatFileItemReader<Points> reader, PointsItemProcessor processor, JdbcBatchItemWriter<Points> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Points, Points>chunk(3, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipLimit(1)
                .skip(FlatFileParseException.class)
                .build();
    }
}
