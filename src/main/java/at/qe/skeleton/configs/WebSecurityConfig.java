package at.qe.skeleton.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

/**
 * Spring configuration for web security.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Configuration
@EnableWebSecurity()
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.headers().frameOptions().disable(); // needed for H2 console

        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login.xhtml");

        String location_manager = "LOCATION_MANAGER";
        String user = "USER";
        String admin = "ADMIN";

        http.authorizeRequests()
                //Permit access to the H2 console
                .antMatchers("/h2-console/**").permitAll()
                //Permit access for all to error pages
                .antMatchers("/error/**")
                .permitAll()
                // Only access with admin role
                .antMatchers("/admin/**")
                .hasAnyAuthority(admin)
                //Permit access only for some roles
                .antMatchers("/secured/**")
                .hasAnyAuthority(admin, location_manager, user)
                .antMatchers("/events/**")
                .hasAnyAuthority(admin, location_manager, user)
                //Access location for ADMIN and LOCATION_MANAGER
                .antMatchers("/location/**")
                .hasAnyAuthority(admin, location_manager)
                 //Access location for all
                .antMatchers("/profile/**")
                .hasAnyAuthority(admin, location_manager, user)
                // Allow only certain roles to use websockets (only logged in users)
                .and()
                .formLogin()
                .loginPage("/login.xhtml")
                .failureUrl("/login.xhtml?error")
                .permitAll()
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/secured/welcome.xhtml");

            http.exceptionHandling().accessDeniedPage("/error/access_denied.xhtml");
            http.sessionManagement().invalidSessionUrl("/error/invalid_session.xhtml");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //Configure roles and passwords via datasource
        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("select username, password, enabled from user where username=?")
                .authoritiesByUsernameQuery("select user_username, roles from user_user_role where user_username=?")
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {

      return new BCryptPasswordEncoder();
    }

}
