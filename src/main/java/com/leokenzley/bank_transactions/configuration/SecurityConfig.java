package com.leokenzley.bank_transactions.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests((requests) -> requests
        .requestMatchers("/accounts/**").hasRole("ADMIN")
        .requestMatchers("/transactions/**").hasAnyRole("USER")
        .requestMatchers("/", "/login", "/h2-console/**", "/logout").permitAll()
        .anyRequest().authenticated()
      )
      .formLogin((form) -> form
        .loginPage("/login")
        .defaultSuccessUrl("/init")
        .permitAll()
      )
      .logout((logout) -> logout
        .logoutUrl("/logout") // URL padrão para logout
        .logoutSuccessUrl("/login?logout") // Redireciona para /login com parâmetro logout
        .invalidateHttpSession(true) // Invalida a sessão
        .deleteCookies("JSESSIONID") // Remove cookies da sessão
        .permitAll()
      )
      .exceptionHandling((exceptions) -> exceptions
        .accessDeniedPage("/access-danied") // aqui configuramos a página customizada
      )
      .csrf(csrf -> csrf
        .ignoringRequestMatchers("/h2-console/**")
      )
      .headers(headers -> headers.frameOptions(frame -> frame.disable()));

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails admin = User.withDefaultPasswordEncoder()
      .username("admin")
      .password("admin")
      .roles("ADMIN")
      .build();

    UserDetails user = User.withDefaultPasswordEncoder()
      .username("user")
      .password("user")
      .roles("USER")
      .build();

    return new InMemoryUserDetailsManager(admin, user);
  }
}
