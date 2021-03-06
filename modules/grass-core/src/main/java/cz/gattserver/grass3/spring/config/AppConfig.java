package cz.gattserver.grass3.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ BaseConfig.class, DatabaseConfig.class, SecurityConfig.class, WebSecurityConfig.class, RestConfig.class })
public class AppConfig {

}
