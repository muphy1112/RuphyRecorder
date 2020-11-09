package me.muphy.config;

import me.muphy.handler.LoginFailureHandler;
import me.muphy.handler.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .withUser("azi").password(new BCryptPasswordEncoder().encode("...")).roles("user").and()
                .withUser("ruphy").password(new BCryptPasswordEncoder().encode("...")).roles("admin");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/payload/**").permitAll()
                .anyRequest().fullyAuthenticated();
        http.formLogin().defaultSuccessUrl("/")
                .successHandler(new LoginSuccessHandler())
                .failureHandler(new LoginFailureHandler()).permitAll();
        http.logout().permitAll();
        http.headers().frameOptions().sameOrigin();
        http.csrf().disable();
    }
}
