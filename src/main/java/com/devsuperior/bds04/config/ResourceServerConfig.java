package com.devsuperior.bds04.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private JwtTokenStore tokenStore;
	
	@Autowired
	private Environment env;
	
	private static final String[] PUBLIC = { "/oauth/token", "/h2-console/**" };
	private static final String[] PUBLIC_GET = { "/cities/**", "/events/**" };
	private static final String[] CLIENT_OR_ADMIN = { "/events/**" };
	private static final String[] OPERATOR_OR_ADMIN = { "/products/**", "/categories/**" };
	
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		//H2
		if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().disable();
		}
		
		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll()
		.antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll()
		.antMatchers(HttpMethod.GET, PUBLIC_GET).permitAll()
		.antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN")
		.antMatchers(HttpMethod.POST, CLIENT_OR_ADMIN).hasAnyRole("CLIENT", "ADMIN")
		.anyRequest().hasAnyRole("ADMIN");
	}	
	
}
