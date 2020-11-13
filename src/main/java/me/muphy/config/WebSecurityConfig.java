package me.muphy.config;

import me.muphy.handler.AuthLimitHandler;
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
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .withUser("azi").password(new BCryptPasswordEncoder().encode("...")).roles("USER").and()
                .withUser("ruphy").password(new BCryptPasswordEncoder().encode("...")).roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/payload/**", "/memorandum/**").permitAll()
                .anyRequest().permitAll();
//                .anyRequest().fullyAuthenticated();
        http.formLogin().defaultSuccessUrl("/")
                .successHandler(new LoginSuccessHandler())
                .failureHandler(new LoginFailureHandler()).permitAll();
        http.exceptionHandling().accessDeniedHandler(new AuthLimitHandler());
        http.logout().permitAll();
        http.headers().frameOptions().sameOrigin();
        http.csrf().disable();
    }
}
