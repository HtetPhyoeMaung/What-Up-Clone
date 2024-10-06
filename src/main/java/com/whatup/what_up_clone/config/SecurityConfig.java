

package com.whatup.what_up_clone.config;


import com.whatup.what_up_clone.entity.Role;
import com.whatup.what_up_clone.security.JwtAuthEntryPoint;
import com.whatup.what_up_clone.security.JwtAuthenticationFilter;
import com.whatup.what_up_clone.security.UserDetailServiceForCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    //hello
    @Autowired
    private UserDetailServiceForCustomer userDetailServiceForCustomer;

    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;




    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)throws Exception{
       httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
               .cors(Customizer.withDefaults())
                .authorizeHttpRequests(request -> request.requestMatchers("/what_up/api/v1/auth/**")
                        .permitAll()
                                .requestMatchers("/what_up/api/v1/admin/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/what_up/api/v1/user/**").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())

                        .anyRequest().authenticated()
                        )
               .exceptionHandling(handler->handler.authenticationEntryPoint(jwtAuthEntryPoint))
               .sessionManagement(manager->manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
       httpSecurity.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
       return httpSecurity.build();



    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter();
    }


    @Bean
    AuthenticationManager configure(HttpSecurity http,PasswordEncoder passwordEncoder) throws Exception {
        var builder =http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(getUserProvider(passwordEncoder));

//        builder.authenticationProvider(getAdminProvider(passwordEncoder));
//
//        builder.authenticationProvider(getSuperProvider(passwordEncoder));
        return builder.build();
    }



    private AuthenticationProvider getUserProvider(PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailServiceForCustomer);
        return provider;
    }

//    private AuthenticationProvider getAdminProvider(PasswordEncoder passwordEncoder){
//
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder);
//        provider.setUserDetailsService(userDetailServiceForAdmin);
//        return provider;
//    }
//    private AuthenticationProvider getSuperProvider(PasswordEncoder passwordEncoder){
//
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder);
//        provider.setUserDetailsService(userDetailServiceForSuper);
//        return provider;
//    }



}
