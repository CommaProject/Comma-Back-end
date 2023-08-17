package com.team.comma.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.exception.RequireRefreshToken;
import com.team.comma.global.jwt.exception.TokenForgeryException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.team.comma.global.common.constant.ResponseCodeEnum.AUTHORIZATION_ERROR;
import static com.team.comma.global.jwt.support.CreationCookie.deleteAccessTokenCookie;
import static com.team.comma.global.jwt.support.CreationCookie.deleteCookie;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (TokenForgeryException e) {
            deleteCookie(response);

            setErrorResponse(response, AUTHORIZATION_ERROR.getCode() , e.getMessage());
        } catch (UsernameNotFoundException e) {
            deleteCookie(response);

            setErrorResponse(response, AUTHORIZATION_ERROR.getCode() , e.getMessage());
        } catch (RequireRefreshToken e) {
            deleteAccessTokenCookie(response);

            setErrorResponse(response, AUTHORIZATION_ERROR.getCode() , e.getMessage());
        }
    }

    private void setErrorResponse(HttpServletResponse response, int errorCode,
        String errorMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        MessageResponse errorResponse = MessageResponse.of(errorCode, errorMessage);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
