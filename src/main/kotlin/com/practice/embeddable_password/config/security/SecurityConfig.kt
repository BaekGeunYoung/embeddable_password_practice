package com.practice.embeddable_password.config.security

import com.practice.embeddable_password.config.security.jwt.JwtConfig
import com.practice.embeddable_password.config.security.userdetails.CustomUserDetailsService
import com.practice.embeddable_password.config.security.jwt.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@Configuration
@EnableWebSecurity
class SecurityConfig(
        @Autowired
        private val userDetailsService: CustomUserDetailsService,
        @Autowired
        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        @Autowired
        private val jwtTokenProvider: JwtTokenProvider
) : WebSecurityConfigurerAdapter()
{
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(http: HttpSecurity) {
        http
                .csrf().disable()
                .antMatcher("/api/**").authorizeRequests()
                .antMatchers("/api/v1/user/register").anonymous()
                .antMatchers("/api/v1/user/login").anonymous()
                .anyRequest().authenticated()
                .and()
                .apply(JwtConfig(jwtTokenProvider))
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder)
    }
}