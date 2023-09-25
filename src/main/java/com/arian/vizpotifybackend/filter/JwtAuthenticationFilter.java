package com.arian.vizpotifybackend.filter;

import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.services.auth.jwt.JwtService;
import com.arian.vizpotifybackend.services.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserService userService;

    private final List<String> allowedOrigins = List.of("http://localhost:3000");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String authHeader= request.getHeader("Authorization");
//        if(authHeader==null || !authHeader.startsWith("Bearer ")){
//            filterChain.doFilter(request, response);
//            return;
//        }
//        String token = authHeader.substring(7);
        String token = extractJwtFromCookie(request);
        // Access-Control-Allow-Origin
        String origin = request.getHeader("Origin");

//        response.setHeader("Access-Control-Allow-Origin", allowedOrigins.contains(origin) ? origin : "");
//        response.setHeader("Vary", "Origin");
//
//        // Access-Control-Max-Age
//        response.setHeader("Access-Control-Max-Age", "3600");
//
//        // Access-Control-Allow-Credentials
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//
//        // Access-Control-Allow-Methods
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//
//        // Access-Control-Allow-Headers
//        response.setHeader("Access-Control-Allow-Headers",
//                "Origin, X-Requested-With, Content-Type, Accept, " + "X-CSRF-TOKEN");

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        String userSpotifyId = this.jwtService.extractSpotifyId(token);
        if(userSpotifyId!=null && SecurityContextHolder.getContext()
                .getAuthentication()==null){
            UserDetail userDetail = this.userService.lodUserDetailBySpotifyId(userSpotifyId);
           if(jwtService.isTokenValid(token,  userDetail)){
               PreAuthenticatedAuthenticationToken auth = new PreAuthenticatedAuthenticationToken(
                       userDetail,
                       null,
                       null
               );
               auth.setDetails(
                       new WebAuthenticationDetailsSource()
                               .buildDetails(request));
               SecurityContextHolder.getContext().setAuthentication(auth);
           }
        }
        filterChain.doFilter(request, response);
    }
    private String extractJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
