package sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import sample.model.User;
import sample.service.SpringDataJpaUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
@Profile("integrationtest")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().csrfTokenRepository(csrfTokenRepository());
		http.authorizeRequests().antMatchers("/**").permitAll();
	}

	private CsrfTokenRepository csrfTokenRepository() 
    { 
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository(); 
        repository.setSessionAttributeName("_csrf");
        return repository; 
    }

	
}