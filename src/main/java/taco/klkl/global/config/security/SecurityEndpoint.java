package taco.klkl.global.config.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Getter
@RequiredArgsConstructor
public enum SecurityEndpoint {
	PUBLIC(new RequestMatcher[]{
		new AntPathRequestMatcher("/"),
		new AntPathRequestMatcher("/login/**"),
		new AntPathRequestMatcher("/error"),
		new AntPathRequestMatcher("/favicon.ico"),

		// swagger
		new AntPathRequestMatcher("/swagger-ui/**"),
		new AntPathRequestMatcher("/swagger-ui.html"),
		new AntPathRequestMatcher("/api-docs/**"),
		new AntPathRequestMatcher("/v3/api-docs/**"),

		// h2 console
		new AntPathRequestMatcher("/h2-console/**"),

		// klkl api
		new AntPathRequestMatcher("/v1/login/**"),
		new AntPathRequestMatcher("/v1/oauth2/**"),
		new AntPathRequestMatcher("/v1/members/**"),
		new AntPathRequestMatcher("/v1/products/**"),
		new AntPathRequestMatcher("/v1/regions/**"),
		new AntPathRequestMatcher("/v1/countries/**"),
		new AntPathRequestMatcher("/v1/cities/**"),
		new AntPathRequestMatcher("/v1/currencies/**"),
		new AntPathRequestMatcher("/v1/categories/**"),
		new AntPathRequestMatcher("/v1/subcategories/**"),
		new AntPathRequestMatcher("/v1/search/**"),
	}),
	USER_ROLE(new RequestMatcher[]{
		new AntPathRequestMatcher("/v1/me/**"),
		new AntPathRequestMatcher("/v1/notifications/**"),
	}),
	;

	private final RequestMatcher[] matchers;
}
