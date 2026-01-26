//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.interview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((auth)
                        -> ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl) ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl) auth.requestMatchers(new String[]{"/", "/oauth2/**", "/login/**", "/error", "/api/auth/**", "/cache/**","/api/interview-rooms/**"})).permitAll()
                        .anyRequest()).authenticated())
                .oauth2Login((oauth2) -> oauth2.defaultSuccessUrl("/home", true))
                .logout((logout) -> logout.logoutSuccessUrl("/"));
        return (SecurityFilterChain) http.build();
    }
}
