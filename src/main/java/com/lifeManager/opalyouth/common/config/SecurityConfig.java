package com.lifeManager.opalyouth.common.config;


import com.lifeManager.opalyouth.filter.JwtAuthorizationFilter;
import com.lifeManager.opalyouth.repository.MemberRepository;
import com.lifeManager.opalyouth.service.OpalPrincipalService;
import com.lifeManager.opalyouth.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    private final MemberRepository memberRepository;
    private final CorsConfig corsConfig;
    private final OpalPrincipalService opalPrincipalService;
    private final JwtUtils jwtUtils;



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilter(corsConfig.corsFilter());
        http.httpBasic().disable();
        http.csrf().disable();
        http.formLogin().disable();
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // jwt filter
        http.addFilterBefore(
                new JwtAuthorizationFilter(memberRepository, jwtUtils),
                BasicAuthenticationFilter.class
        );

        // authorization
        http.authorizeRequests()
                .antMatchers("/login/**", "/signup", "/").permitAll()
                .anyRequest().hasRole("USER");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        daoAuthenticationProvider.setUserDetailsService(opalPrincipalService);

        return daoAuthenticationProvider;
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
////        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
//        return http
//                // session stateless
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                // jwt filter
//                .addFilterBefore(
//                        new JwtAuthenticationFilter(authenticationManager),
//                        UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(
//                        new JwtAuthorizationFilter(memberRepository),
//                        BasicAuthenticationFilter.class
//                )
//                // authorization
//                .authorizeHttpRequests()
//                .antMatchers("/login", "/signup").permitAll()
//                .anyRequest().permitAll() // todo : 로그인 구축 시 authenticated()로 바꾸기
//                .and()
//                .csrf().disable()
//                .formLogin().disable()
//                .build();
//    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
