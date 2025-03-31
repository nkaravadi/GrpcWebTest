package com.naga.grpcweb.fluxconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import net.devh.boot.grpc.server.security.authentication.BasicGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
public class AutheManagerConfig {

    @Autowired
    private ReactiveUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            try {
                UserDetails userDetails = userDetailsService.findByUsername(authentication.getName()).block();
                if (userDetails != null &&
                    passwordEncoder.matches((String) authentication.getCredentials(), userDetails.getPassword())) {
                    return new UsernamePasswordAuthenticationToken(
                        userDetails,
                        authentication.getCredentials(),
                        userDetails.getAuthorities());
                }
            } catch (Exception e) {
                // Authentication failed
            }
            throw new BadCredentialsException("Invalid username or password");
        };
    }

    @Bean
    public GrpcAuthenticationReader authenticationReader() {
        return new BasicGrpcAuthenticationReader();
    }
}
