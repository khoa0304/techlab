//package lab.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.web.savedrequest.NullRequestCache;
//
//@Configuration
//@EnableWebSecurity
////@EnableGlobalMethodSecurity(prePostEnabled = true)  
//public class WebSecurity extends WebSecurityConfigurerAdapter {
//
//	public static final String USER_NAME = "khoa";
//	public static final String PASSWORD = "welcome123";
//
////	@Bean
////    public HttpSessionStrategy httpSessionStrategy() {
////        return new HeaderHttpSessionStrategy();
////    }
//
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.csrf().ignoringAntMatchers("/**");
//
//		http.requestCache().requestCache(new NullRequestCache());
//		super.configure(http);
//	}
//
//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication().withUser(USER_NAME).password("{noop}" + PASSWORD).roles("SYSTEM");
//	}
//
//	@Autowired
//	public void configureAuth(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication().withUser(USER_NAME).password("{noop}" + PASSWORD).roles("SYSTEM").and()
//				.withUser("admin").password("{noop}+PASSWORD").roles("ADMIN");
//	}
//}
