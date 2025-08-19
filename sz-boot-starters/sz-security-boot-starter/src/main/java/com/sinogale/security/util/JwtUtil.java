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

/**
 * @ClassName JwtUtil
 * @Author duanchao
 * @Date 2021/7/21 12:10
 **/

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
        return this.getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) {
        return this.getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) {
        return this.getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = this.getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));

        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (Exception var4) {
            throw new IllegalTokenException("123"/*AuthErrorCode.TOKEN_INVALID.getName()*/);
        }
    }

    public Boolean isTokenExpired(String token) {
        Date expiration = this.getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private String generateToken(Long id, Map<String, Object> claims) {
        return this.doGenerateToken(claims, String.valueOf(id));
    }

    public String generateJwtToken(TokenInfo tokenInfo) {
        Long id = tokenInfo.getUserId();
        Map<String, Object> claims = Maps.newHashMap();
        claims.put("userType", tokenInfo.getUserType().getCode());
        claims.put("username", tokenInfo.getUsername());
        claims.put("id", tokenInfo.getUserId());
        claims.put("plat", tokenInfo.getPlatform().getCode());
        return this.generateToken(id, claims);
    }

    public TokenInfo getTokenInfo(String token) {
        Claims claims = this.getAllClaimsFromToken(token);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setUserType(UserType.of(Integer.valueOf(claims.get("userType").toString())).get());
        tokenInfo.setUsername(claims.get("username").toString());
        tokenInfo.setUserId(Long.valueOf(claims.get("id").toString()));
        tokenInfo.setPlatform(SZPlatform.of(Integer.valueOf(claims.get("plat").toString())).get());
        return tokenInfo;
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        Date createdDate = new Date();
        Date expirationDate = this.calculateExpirationDate(createdDate);
        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(createdDate).setExpiration(expirationDate).signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String refreshToken(String token) {
        Date createdDate = new Date();
        Date expirationDate = this.calculateExpirationDate(createdDate);
        Claims claims = this.getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);
        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder().setClaims(claims).signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public Boolean validateToken(String token) {
        return !this.isTokenExpired(token);
    }

    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + this.expiration * 1000L);
    }
}
