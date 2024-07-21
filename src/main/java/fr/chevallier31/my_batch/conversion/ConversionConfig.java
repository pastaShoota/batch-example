package fr.chevallier31.my_batch.conversion;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

import fr.chevallier31.my_batch.Points;

@Configuration
public class ConversionConfig {
    @Bean
    public ConversionService conversionService() {
        DefaultConversionService cs = new DefaultConversionService();
        DefaultConversionService.addDefaultConverters(cs);
        cs.addConverter(localDateConverter());
        return cs;
    }

    @Bean
    public Converter<String,LocalDate> localDateConverter() {
        return new Converter<String,LocalDate>(){
            @Override
            public LocalDate convert(String text) {
                return LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
        };
    }

    @Bean
    public PointsMapper pointsMapper() {
        BeanWrapperFieldSetMapper<Points> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setConversionService(conversionService());
        mapper.setTargetType(Points.class);
        return new PointsMapper(){
            @Override
            public Points mapFieldSet(FieldSet fs) throws org.springframework.validation.BindException{
                return mapper.mapFieldSet(fs);
            }
        };
    }

    public interface PointsMapper extends FieldSetMapper<Points>{};
} 