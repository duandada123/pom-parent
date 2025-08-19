package com.sinogale.security.base;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    public static final String HEADER_TOKEN_NAME = "token";

    public static final String USER_AGENT = "user-agent";

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authorization = request.getHeader("token");
        String userAgent = request.getHeader("user-agent");
        List<AntPathRequestMatcher> list = Lists.newArrayList();
        list.add(new AntPathRequestMatcher("/auth/**"));
        list.add(new AntPathRequestMatcher("/public/**"));
        boolean match = false;
        for (AntPathRequestMatcher ant : list) {
            if (ant.matches(request))
                match = true;
        }
        if (!Strings.isNullOrEmpty(authorization) && !match) {
            JwtAuthToken token = new JwtAuthToken(authorization);
            token.setUserAgent(userAgent);
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        chain.doFilter(request, response);
    }
}
