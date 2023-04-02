package com.team.comma.controller;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AccountException;

import org.springframework.core.ResolvableType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.comma.dto.MessageDTO;
import com.team.comma.dto.OauthRequest;
import com.team.comma.dto.RequestUserDTO;
import com.team.comma.dto.TokenDTO;
import com.team.comma.service.OauthService;
import com.team.comma.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class UserController {

	final private String authorizationRequestBaseUri = "oauth2/authorization";
	final private UserService userService;
	final private ClientRegistrationRepository clientRegistrationRepository;
	final private Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
	final private OauthService oauthService;
	
	@PostMapping(value = "/login")
	public TokenDTO login(@RequestBody RequestUserDTO userDTO , HttpServletResponse response) throws AccountException {
		return userService.login(userDTO , response);
	}
	
	@PostMapping(value = "/register")
	public MessageDTO register(@RequestBody RequestUserDTO userDTO) throws AccountException {
		return userService.register(userDTO);
	}
	
	@GetMapping("/oauth")
    public Map getLoginLink() {
		Iterable<ClientRegistration> clientRegistrations = null;

        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
                .as(Iterable.class);

        if (type != ResolvableType.NONE &&
                ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }

        clientRegistrations.forEach(registration ->
                oauth2AuthenticationUrls.put(registration.getClientName(),
                        authorizationRequestBaseUri + "/" + registration.getRegistrationId()));

        return oauth2AuthenticationUrls;
	}
	
	@GetMapping(value = "/oauth/login")
	public void OauthLogin(@RequestParam(name = "code") String code , @RequestParam(name = "state" , required = false) String state) throws AccountException {
		oauthService.loginOauthServer(OauthRequest.builder().code(code).state(state).type("kakao").build());
	}
	
	/*
	 * 
	@GetMapping(value = "/oauth/login")
	public void OauthLogin(@RequestBody OauthRequest oauthRequest) {
		oauthService.loginOauthServer(oauthRequest);
	}
	 */
	
}
