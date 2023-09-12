package com.arian.vizpotifybackend.filter;

import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.services.auth.jwt.JwtService;
import com.arian.vizpotifybackend.services.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserService userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader= request.getHeader("Authorization");
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
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

}
