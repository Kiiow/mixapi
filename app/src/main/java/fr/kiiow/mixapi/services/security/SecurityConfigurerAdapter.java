package fr.kiiow.mixapi.services.security;

import fr.kiiow.mixapi.models.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

// https://www.javainuse.com/boot3/sec/5
@Configuration
@EnableWebSecurity
public class SecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private Config config;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults());
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> {
                    if(config.isSecurityEnabled()) {
                        requests.requestMatchers("/auth/**", "/scrapper/**").authenticated();
                    }
                    requests.requestMatchers("/**").permitAll();
                }
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
        ;
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(PasswordEncoder.passwordEncoder());
        return authenticationProvider;
    }
}
