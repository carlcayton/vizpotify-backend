package com.arian.vizpotifybackend.services.auth.jwt;

import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.properties.JWTConfigProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JWTConfigProperties jwtConfig;

    public String createToken(UserDetail userDetail) {
        Claims claims = Jwts.claims().setSubject(userDetail.getSpotifyId());
        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtConfig.getValidityInMs());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSignInKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetail userDetail) {
        String spotifyId= extractSpotifyId(token);
        return !isTokenExpired(token) && spotifyId.equals(userDetail.getSpotifyId());
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractSpotifyId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignInKey(){
        byte[] keyBytes  = Decoders.BASE64.decode(jwtConfig.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


}
