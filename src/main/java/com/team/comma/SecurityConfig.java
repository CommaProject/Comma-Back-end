package com.team.comma;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.team.comma.oauth.OAuth2Provider;
import com.team.comma.security.JwtAuthenticationFilter;
import com.team.comma.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@PropertySource("classpath:application-oauth.properties")
@RequiredArgsConstructor
public class SecurityConfig {

	final private JwtTokenProvider jwtTokenProvider;
	final private List<String> clients = Arrays.asList("google", "kakao", "naver");
	final private Environment env;
	final private String CLIENT_PROPERTY_KEY= "spring.security.oauth2.client.registration.";
	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/security/**").hasRole("USER")
                .anyRequest().permitAll()
                .and().logout() 
                .logoutUrl("/logout") // logout URL에 접근하면
                .deleteCookies("refreshToken") 
                .deleteCookies("accessToken") // refreshToken 과 accessToken 삭제
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request , response , Exception) -> {
                	response.sendRedirect("/authentication/denied"); // 인증되지 않은 사용자
                })
                .accessDeniedPage("/authorization/denied"); // 인가되지 않은 사용자가 접속했을 때
        
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), // 필터
				UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }
    
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = clients.stream()
                .map(c -> getRegistration(c))
                .filter(registration -> registration != null)
                .collect(Collectors.toList());
        return new InMemoryClientRegistrationRepository(registrations);
    }
    
    private ClientRegistration getRegistration(String client){
        // API Client Id 불러오기
        String clientId = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-id");

        // API Client Id 값이 존재하는지 확인하기
        if (clientId == null) {
            return null;
        }

        // API Client Secret 불러오기
        String clientSecret = env.getProperty(
                CLIENT_PROPERTY_KEY + client + ".client-secret");

        if (client.equals("google")) {
            return OAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();
        }

        if (client.equals("kakao")) {
            return OAuth2Provider.KAKAO.getBuilder(client)
                    .clientId(clientId)
                    .build();
        }

        if (client.equals("naver")) {
            return OAuth2Provider.NAVER.getBuilder(client)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();
        }

        return null;
    }
}
