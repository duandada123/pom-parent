package com.sinogale.security.auto;


import com.sinogale.security.base.CustomCorsFilter;
import com.sinogale.security.base.JwtAuthenticationEntryPoint;
import com.sinogale.security.base.JwtAuthenticationProvider;
import com.sinogale.security.base.JwtAuthenticationTokenFilter;
import com.sinogale.security.config.LoginUrlProperties;
import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ChannelSecurityConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    private LoginUrlProperties loginUrlProperties;

    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(this.jwtAuthenticationProvider);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().antMatchers(new String[] { "/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**" }).antMatchers(HttpMethod.GET, new String[] { "/", "/*.html", "/favicon.ico", "/**/*.html", "/**/*.css", "/**/*.js" });
    }

    protected void configure(HttpSecurity httpSecurity) throws Exception {
        ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) ((HttpSecurity)((ChannelSecurityConfigurer.RequiresChannelUrl)httpSecurity.requiresChannel()
                .anyRequest()).requiresInsecure()
                .and())
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(this.unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers().frameOptions().disable()
                .and()
                .headers().addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))

                .and()
                .authorizeRequests()
                .requestMatchers(new RequestMatcher[] { CorsUtils::isPreFlightRequest })).permitAll()
                .antMatchers(HttpMethod.OPTIONS)).permitAll()
                .antMatchers(HttpMethod.GET,
                        new String[] { "/", "/*.html", "/favicon.ico", "/**/*.html", "/**/*.css", "/**/*.js" })).permitAll()
                .antMatchers(new String[] { "/trace/users/**" })).permitAll()
                .antMatchers(new String[] { "/swagger-ui.html" })).permitAll()
                .antMatchers(new String[] { "/swagger-resources/**" })).permitAll()
                .antMatchers(new String[] { "/images/**" })).permitAll()
                .antMatchers(new String[] { "/webjars/**" })).permitAll()
                .antMatchers(new String[] { "/v2/api-docs" })).permitAll()
                .antMatchers(new String[] { "/configuration/ui" })).permitAll()
                .antMatchers(new String[] { "/configuration/security" })).permitAll()
                .antMatchers(new String[] { "/auth/**" })).permitAll()
                .antMatchers(new String[] { "/public/**" })).permitAll()
                .antMatchers((String[])this.loginUrlProperties.getUnAuthUrls()
                        .toArray((Object[])new String[this.loginUrlProperties.getUnAuthUrls().size()]))).permitAll()
                .antMatchers(new String[] { "/admin/**" })).hasRole("ADMIN")
                .anyRequest()).authenticated();
        httpSecurity
                .addFilterBefore(new CustomCorsFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        httpSecurity.headers().cacheControl();
    }
}