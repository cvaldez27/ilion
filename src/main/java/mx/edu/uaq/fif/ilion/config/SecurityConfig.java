package mx.edu.uaq.fif.ilion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/login", "/register", "/css/**", "/doctor/css/**", "/doctor/js/**", "/patient/css/**", "/patient/js/**").permitAll()
                .requestMatchers("/doctor/**").hasRole("DOCTOR")
                .requestMatchers("/patient/**").hasRole("PATIENT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                             HttpServletResponse response,
                                             Authentication authentication) throws IOException {
                
                // Forma correcta de obtener los roles
                Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
                
                // Verificar roles con el prefijo correcto
                if (authorities.contains("ROLE_DOCTOR")) {
                    response.sendRedirect("/doctor/dashboard");
                } else if (authorities.contains("ROLE_PATIENT")) {
                    response.sendRedirect("/patient/dashboard");
                } else {
                    response.sendRedirect("/login?error");
                }
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}