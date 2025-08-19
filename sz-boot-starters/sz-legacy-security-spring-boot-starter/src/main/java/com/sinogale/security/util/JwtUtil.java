package com.sinogale.security.util;

import com.google.common.collect.Maps;
import com.sinogale.common.constants.SZPlatform;
import com.sinogale.common.constants.UserType;
import com.sinogale.security.base.TokenInfo;
import com.sinogale.security.exception.IllegalTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public class JwtUtil {

    public static final String USERNAME = "username";

    public static final String USER_TYPE = "userType";

    public static final String ID = "id";

    public static final String PLATFORM = "plat";

    private final String secret;

    private final Long expiration;

    public JwtUtil(String secret, Long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public String getSubject(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
        try {
            return (Claims) Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new IllegalTokenException("token 无效");
        }
    }

    public Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return Boolean.valueOf(expiration.before(new Date()));
    }

    private String generateToken(Long id, Map<String, Object> claims) {
        return doGenerateToken(claims, String.valueOf(id));
    }

    public String generateJwtToken(TokenInfo tokenInfo) {
        Long id = tokenInfo.getUserId();
        Map<String, Object> claims = Maps.newHashMap();
        claims.put("userType", tokenInfo.getUserType().getCode());
        claims.put("username", tokenInfo.getUsername());
        claims.put("id", tokenInfo.getUserId());
        claims.put("plat", tokenInfo.getPlatform().getCode());
        return generateToken(id, claims);
    }

    public TokenInfo getTokenInfo(String token) {
        Claims claims = getAllClaimsFromToken(token);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setUserType(UserType.of(Integer.valueOf(claims.get("userType").toString())).get());
        tokenInfo.setUsername(claims.get("username").toString());
        tokenInfo.setUserId(Long.valueOf(claims.get("id").toString()));
        tokenInfo.setPlatform(SZPlatform.of(Integer.valueOf(claims.get("plat").toString())).get());
        return tokenInfo;
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        Date createdDate = new Date();
        Date expirationDate = calculateExpirationDate(createdDate);
        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String refreshToken(String token) {
        Date createdDate = new Date();
        Date expirationDate = calculateExpirationDate(createdDate);
        Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);
        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token) {
        return Boolean.valueOf(!isTokenExpired(token).booleanValue());
    }

    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + this.expiration.longValue() * 1000L);
    }
}