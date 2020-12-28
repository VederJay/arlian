package org.arlian.site.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        String[] unsecuredUrls = {
                "/", "index", "/index",
                "/privacy",
                "/usage",
                "/adminLTE/**",
                "/img/**", "/favicon/**", "/css/**"
        };

        httpSecurity
                .authorizeRequests()
                    .antMatchers(unsecuredUrls).permitAll()
                    .anyRequest().authenticated()
                    .and()
                .oauth2Login();
    }

}
