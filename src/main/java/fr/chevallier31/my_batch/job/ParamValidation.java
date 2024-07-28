package fr.chevallier31.my_batch.job;

import java.nio.file.Paths;
import java.util.List;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParamValidation {
    private static final String[] requiredParams = new String[]{"posting.filepath"};
    private static final String[] optionalParams = new String[]{"ignore.duplicates"};
    
    @Bean
    public JobParametersValidator validator() {
        final CompositeJobParametersValidator validator = new CompositeJobParametersValidator();

        validator.setValidators(List.of(
            presenceValidator(),
            fileExistsValidator()
        ));
        return validator;
    }

    private DefaultJobParametersValidator presenceValidator() {
        return new DefaultJobParametersValidator(requiredParams, optionalParams);
    }

    private JobParametersValidator fileExistsValidator() {
        return (JobParameters params) -> {
            String postingFilepath = params.getString("posting.filepath");
            if (!Paths.get(postingFilepath).toFile().exists()) {
                throw new JobParametersInvalidException("posting.filepath "+postingFilepath+" missing on file system");
            }
        };
    }
}
