package com.naga.grpcweb.fluxconfig;

        import net.devh.boot.grpc.server.security.check.AccessPredicate;
        import net.devh.boot.grpc.server.security.check.GrpcSecurityMetadataSource;
        import net.devh.boot.grpc.server.security.check.ManualGrpcSecurityMetadataSource;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.security.access.AccessDecisionManager;
        import org.springframework.security.access.AccessDecisionVoter;
        import org.springframework.security.access.vote.AuthenticatedVoter;
        import org.springframework.security.access.vote.RoleVoter;
        import org.springframework.security.access.vote.UnanimousBased;
        import org.springframework.security.authentication.ReactiveAuthenticationManager;
        import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
        import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
        import org.springframework.security.config.web.server.ServerHttpSecurity;
        import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
        import org.springframework.security.core.userdetails.User;
        import org.springframework.security.core.userdetails.UserDetails;
        import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.security.web.server.SecurityWebFilterChain;

        import java.util.Arrays;
        import java.util.List;

        @Configuration
        @EnableWebFluxSecurity
        public class SecurityConfig {

            @Bean
            public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
            }

            @Bean
            public MapReactiveUserDetailsService userDetailsService() {
                UserDetails user = User.withUsername("user")
                        .password(passwordEncoder().encode("password"))
                        .roles("USER")
                        .build();
                return new MapReactiveUserDetailsService(user);
            }

            @Bean
            public ReactiveAuthenticationManager reactiveAuthenticationManager() {
                UserDetailsRepositoryReactiveAuthenticationManager manager =
                    new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
                manager.setPasswordEncoder(passwordEncoder());
                return manager;
            }

            @Bean
            public AccessDecisionManager accessDecisionManager() {
                List<AccessDecisionVoter<?>> decisionVoters = Arrays.asList(
                        new RoleVoter(),
                        new AuthenticatedVoter()
                );
                return new UnanimousBased(decisionVoters);
            }

            @Bean
            public GrpcSecurityMetadataSource grpcSecurityMetadataSource() {
                ManualGrpcSecurityMetadataSource metadataSource = new ManualGrpcSecurityMetadataSource();
                metadataSource.setDefault(AccessPredicate.permitAll());
                return metadataSource;
            }

            @Bean
            public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
                return http
                        .authorizeExchange(exchanges -> exchanges
                                .anyExchange().permitAll()
                        )
                        .csrf(csrf -> csrf.disable())
                        .build();
            }
        }
