package fr.chevallier31.my_batch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class FileConfiguration {
    
    @Value("${posting.filepath}")
    private String postingFilePath;

    @Bean(name = "postingFile")
    public FileSystemResource postingFile(){
        return new FileSystemResource(postingFilePath);
    }

}
