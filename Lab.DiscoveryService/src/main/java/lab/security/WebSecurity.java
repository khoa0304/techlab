package lab.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)  
public class WebSecurity extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().ignoringAntMatchers("/**");
		
		http
         .requestCache()
         .requestCache(new NullRequestCache());
		 super.configure(http);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("khoa").password("{noop}welcome123").roles("SYSTEM");
	}

	@Autowired
	public void configureAuth(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().
		withUser("khoa").password("{noop}welcome123").roles("SYSTEM")
	    .and().
        withUser("admin").password("{noop}password").roles("ADMIN");
;
	}

	@Bean
	public UserDetailsService userDetailsService() {

		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		UserDetails user = User.withUsername("khoa").password(encoder.encode("khoa")).roles("SYSTEM").build();
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(user);
		return manager;

	}
}
