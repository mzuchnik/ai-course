package pl.klastbit.lexpage.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.klastbit.lexpage.infrastructure.adapters.persistence.mapper.ContactMessageMapper;

/**
 * Configuration for mapper beans.
 * Mappers are stateless and can be shared as Spring beans.
 */
@Configuration
public class MapperConfiguration {

    @Bean
    public ContactMessageMapper contactMessageMapper() {
        return new ContactMessageMapper();
    }
}
