package sample.config;

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
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled =true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private SpringDataJpaUserDetailsService userDetailsService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().csrfTokenRepository(csrfTokenRepository());
		//http.csrf().disable();
		http.authorizeRequests().antMatchers("/").permitAll()
		.anyRequest().authenticated()
		.and().formLogin().loginPage("/login").permitAll().and().logout().permitAll();

		// secure login
		//http.requiresChannel().anyRequest().requiresInsecure();
		//http.requiresChannel().antMatchers("/login").requiresSecure();	

		
//		  http.httpBasic().and().authorizeRequests()
//	        .antMatchers("/publications","/publications/**").hasRole("PUBLISHER")
//	        .antMatchers("/pubsubscriptions","/pubsubscriptions/**").hasRole("VIEWER");
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {		
		auth.userDetailsService(userDetailsService).passwordEncoder(User.PASSWORD_ENCODER);	
	}
	
	
	private CsrfTokenRepository csrfTokenRepository() 
    { 
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository(); 
        repository.setSessionAttributeName("_csrf");
        return repository; 
    }

	
}