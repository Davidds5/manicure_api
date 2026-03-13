package br.com.davidds5.manicure_api.config;


import com.github.dozermapper.core.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DozerMapperConfig {

    @Bean
    public Mapper dozerMapper(){
        return new DozerBeanConfig();
    }
}
