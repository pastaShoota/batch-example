package fr.chevallier31.my_batch.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import fr.chevallier31.my_batch.Points;
import fr.chevallier31.my_batch.conversion.ConversionConfig.PointsMapper;

@Configuration
public class StepOne {

    @Bean
    public FlatFileItemReader<Points> transientReader(PointsMapper pointsMapper, FileSystemResource postingFile) {
        return new FlatFileItemReaderBuilder<Points>()
                .name("points reader")
                .resource(postingFile)
                .linesToSkip(1) // skip header
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

    @Bean(name = "PointsTransientWriter")
    public JdbcBatchItemWriter<Points> transientWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Points>()
                .sql("""
                            INSERT INTO POINTS_TRANSIENT (
                                activity_date,fidelity_number,first_name,last_name,fidelity_code,points
                            ) VALUES (
                                :activityDate,:fidelityNumber,:firstName,:lastName,:fidelityCode,:points
                            )
                        """)
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean(name = "step1")
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
            FlatFileItemReader<Points> reader, @Qualifier("PointsTransientWriter")JdbcBatchItemWriter<Points> writer) {
        return new StepBuilder("1- Acquire posting data", jobRepository)
                .<Points, Points>chunk(10, transactionManager)
                .reader(reader)
                .writer(writer)
                .faultTolerant()
                .skipLimit(1) // skip parse exception on footer record
                .skip(FlatFileParseException.class)
                .build();
    }
}
