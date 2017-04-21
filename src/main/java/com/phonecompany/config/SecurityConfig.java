package com.phonecompany.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;
    private ShaPasswordEncoder shaPasswordEncoder;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService,
                          ShaPasswordEncoder shaPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.shaPasswordEncoder = shaPasswordEncoder;
    }

    @Autowired
    public void registerGlobalAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(shaPasswordEncoder);
    }

    @Autowired
    private RESTAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private RESTAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private RESTAuthenticationSuccessHandler authenticationSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/view/**").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/api/users").permitAll()
                .antMatchers("/api/user/reset").permitAll()
                .antMatchers("/api/confirmRegistration").permitAll()
                .antMatchers("/api/roles").hasRole("ADMIN")
                .antMatchers("/csr").hasRole("CSR")
                .anyRequest().authenticated();

        http.csrf().disable();
        http
                .formLogin().loginPage("/login").permitAll()
                .and()
                .logout().logoutSuccessUrl("/#/index").permitAll();
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        http.formLogin().successHandler(authenticationSuccessHandler);
        http.formLogin().failureHandler(authenticationFailureHandler);
    }
}