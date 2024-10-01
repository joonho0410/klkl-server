package taco.klkl.global.config.security;

import static taco.klkl.domain.member.domain.Role.*;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import taco.klkl.domain.oauth.service.CustomOAuth2UserService;
import taco.klkl.domain.oauth.service.OAuth2SuccessHandler;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomOAuth2UserService oAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final CustomAccessDeniedHandler accessDeniedHandler;
	private final CustomAuthenticationEntryPoint authenticationEntryPoint;
	private final TokenAuthenticationFilter tokenAuthenticationFilter;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			// disable csrf
			.csrf(AbstractHttpConfigurer::disable)

			// configure cors
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))

			// disable default authentication
			.httpBasic(AbstractHttpConfigurer::disable)

			// disable default login form
			.formLogin(AbstractHttpConfigurer::disable)

			// disable default logout
			.logout(AbstractHttpConfigurer::disable)

			// disable X-Frame-Options (enable h2-console)
			.headers((headers) ->
				headers.contentTypeOptions(contentTypeOptionsConfig ->
					headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)))

			// disable session
			.sessionManagement(sessionManagement ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// request authentication & authorization
			.authorizeHttpRequests(authorizeRequests ->
				authorizeRequests
					.requestMatchers("/", "/login/**").permitAll()
					.requestMatchers(
						"/swagger-ui/**",
						"/swagger-ui.html",
						"/api-docs/**",
						"/v3/api-docs/**"
					).permitAll()
					.requestMatchers("/error", "/favicon.ico").permitAll()
					.requestMatchers("/h2-console/**").permitAll()
					.requestMatchers(HttpMethod.POST).hasAnyRole(USER.name(), ADMIN.name())
					.requestMatchers(HttpMethod.PUT).hasAnyRole(USER.name(), ADMIN.name())
					.requestMatchers(HttpMethod.DELETE).hasAnyRole(USER.name(), ADMIN.name())
					.requestMatchers(
						"/v1/members/me/**",
						"/v1/products/following/**",
						"/v1/notifications/**"
					).hasRole(USER.name())
					.requestMatchers(
						RegexRequestMatcher.regexMatcher("/v1/products/\\d+/likes(/.*)?"))
					.hasRole(USER.name())
					.requestMatchers(
						"/v1/login/**",
						"/v1/oauth/**",
						"/v1/members/**",
						"/v1/products/**",
						"/v1/regions/**",
						"/v1/countries/**",
						"/v1/cities/**",
						"/v1/currencies/**",
						"/v1/categories/**",
						"/v1/subcategories/**",
						"/v1/search/**"
					).permitAll()
					.anyRequest().authenticated()
			)

			// oauth2
			.oauth2Login(oauth2 ->
				oauth2
					.userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
					.successHandler(oAuth2SuccessHandler)
					.authorizationEndpoint(authorization -> authorization
						.baseUri("/v1/oauth2/authorization"))
					.redirectionEndpoint(redirection -> redirection
						.baseUri("/v1/login/oauth2/code/*"))
			)

			// auth exception handling
			.exceptionHandling(exception ->
				exception
					.accessDeniedHandler(accessDeniedHandler)
					.authenticationEntryPoint(authenticationEntryPoint)
			)

			// jwt exception handling
			.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(7200L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
