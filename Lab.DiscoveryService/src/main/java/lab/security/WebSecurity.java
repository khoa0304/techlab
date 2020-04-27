package lab.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
//@Order(1)
public class WebSecurity extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// http.csrf().disable().authorizeRequests().anyRequest().authenticated().and().httpBasic();

		http.csrf().ignoringAntMatchers("/eureka/**");
		super.configure(http);

//		http.sessionManagement()
//        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//        .and().requestMatchers().antMatchers("/eureka/**")
//        .and().authorizeRequests().antMatchers("/eureka/**")
//        .hasRole("SYSTEM").anyRequest().denyAll().and()
//        .httpBasic().and().csrf().disable();
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
