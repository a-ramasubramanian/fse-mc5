package com.stackroute.gateway.filter;


import com.stackroute.gateway.exception.InvalidHeaderException;
import com.stackroute.gateway.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * JwtValidationFilter filters the requests based on the validity of the JwtToken passed in the request header
 */
public class JwtValidationFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String SIGNING_KEY = "confidential";
    public static final String OPTIONS = "OPTIONS";

    /*
     * Below method checks the presence of Authorization header with JWT token. It extracts and validates the token from header
     * On Successful validation, the request is forwarded to the next filter/controller in the filter chain
     */
    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (OPTIONS.equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            logger.info("Authorization Header : " + authHeader);
            if (authHeader == null || !authHeader.startsWith(BEARER)) {
                throw new InvalidHeaderException();
            }

            final String token = authHeader.substring(7);
            logger.info("token\t" + token);

            try {
                final Claims claims = Jwts.parser()
                        .setSigningKey(SIGNING_KEY)
                        .parseClaimsJws(token)
                        .getBody();
                request.setAttribute("claims", claims);
            } catch (final ExpiredJwtException ex) {
                logger.error("JWT token expired : " + ex.getMessage());
                throw new InvalidTokenException("JWT token expired");
            } catch (final JwtException ex) {
                logger.error("JWT validation failed : " + ex.getMessage());
                throw new InvalidTokenException("Invalid JWT token");
            }
        }
        filterChain.doFilter(request, response);
    }
}