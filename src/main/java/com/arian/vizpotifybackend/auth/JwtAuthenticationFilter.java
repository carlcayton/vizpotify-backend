package com.arian.vizpotifybackend.auth;

import com.arian.vizpotifybackend.auth.AuthService;
import com.arian.vizpotifybackend.auth.JwtService;
import com.arian.vizpotifybackend.user.core.UserDetail;
import com.arian.vizpotifybackend.user.core.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserService userService;
    private final AuthService authService;
    private static final Set<String> permissiblePaths = new HashSet<>();

    static {
        permissiblePaths.add("/api/v1/users/");
        permissiblePaths.add("/api/v1/auth/callback/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = authService.extractJwtFromCookie(request);

        response.setHeader("Access-Control-Allow-Credentials", "true");

        if(isPermissiblePath(request.getRequestURI())  && !"POST".equals(request.getMethod())){
        filterChain.doFilter(request, response);
        return;
    }

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try{
            String userSpotifyId = this.jwtService.extractSpotifyId(token);
            if(userSpotifyId!=null && SecurityContextHolder.getContext()
                    .getAuthentication()==null){
                UserDetail userDetail = this.userService.loadUserDetailBySpotifyId(userSpotifyId);
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
        }catch (ExpiredJwtException e){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 or another custom code
                response.setHeader("X-Token-Expired", "true");
                return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPermissiblePath(String path) {
        return permissiblePaths.stream().anyMatch(path::startsWith);
    }

}
