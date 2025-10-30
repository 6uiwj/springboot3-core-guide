package com.springboot.jpa.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final UserDetailsService userDetailsService;

    @Value("${springboot.jwt.secret}") //yml파일에서 secretkey 가져오기
    private String secretKey = "secretKey";

    private final long tokenValiditySeconds = 1000L * 60 * 60;

    @PostConstruct //Spring이 Bean을 모두 생성하고 의존성 주입이 끝난 직후에 실행되는 메서드
    //secretKey 가공 (서명부분)
    public void init() {
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 시작");
        //JWT서명에 사용할 secretKey를 Base64 인코딩
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 완료");
    }


    //JWT 토큰 생성
    public String createToken(String userUid, List<String> roles) {
        LOGGER.info("[createToken] 토큰 생성 시작");
        //Payload(=클레임) 생성
        //setSubject : "sub(주제)"클레임 설정 - userUid
        /**
         * {
         *   "sub": "user123"
         * }
         */
        Claims claims = Jwts.claims().setSubject(userUid);
        //클레임에 권한정보 추가
        /**
         * {
         *   "sub": "user123",
         *   "roles": ["ROLE_USER", "ROLE_ADMIN"]
         * }
         */
        claims.put("roles", roles);
        Date now = new Date(); //발급 시각과 만료 시각 설정위해 Date 객체 생성

        String token = Jwts.builder()
                .setClaims(claims) //클레임 정보 설정
                .setIssuedAt(now) //발급시각(iat)
                .setExpiration(new Date(now.getTime() + tokenValiditySeconds)) //만료시각
                .signWith(SignatureAlgorithm.HS256, secretKey) //디지털 서명 (사용할 알고리즘, 인코딩한 키)
                .compact(); //결과물로 하나의문자열로 반환 (xxx.yyy.zzz)

        LOGGER.info("[createToken]");
        /**
         * 최종 결과
         * {
         *   "sub": "user123",
         *   "roles": ["ROLE_USER"],
         *   "iat": 1729892000,
         *   "exp": 1729895600
         * }
         */
        return token;
    }



    //JWT 검증 후 JWT를 Spring Security 인증 객체로 바꿔주는 역할
    /**
     * 최종 반환되는 Authentication 객체
     * UsernamePasswordAuthenticationToken
     *  ├─ principal : UserDetails(user123)
     *  ├─ credentials : ""
     *  └─ authorities : [ROLE_USER, ROLE_ADMIN]
     */
    public Authentication getAuthentication(String token) {
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 시작");
        //"token"에서 username을 추출해 DB에서 사용자정보 조회하여 userDetails에 담음
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        //UsernamePasswordAuthenticationToken : Authentication 인터페이스의 구현체 중 하나
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    //사용자 정보 복호화
    //setSigningKey :  토큰을 검증할 때 사용할 비밀키를 설정
    //parseClaimsJws(token) :  토큰을 파싱 (Base64 디코딩과 서명 검증) -> 검증 성공시 Jws<Claims> 객체 반환
    //Jws : 서명된 JWT
    //Claims : JWT안의 payload(사용자 데이터)
    public String getUsername(String token) {
        LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출");
        String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출 완료, info : {}", info);
        return info;
    }

    //클라이언트가 보낸 HTTP 요청 헤더에서 JWT 토큰을 꺼내는 역할
    public String resolveToken(HttpServletRequest request) {
        LOGGER.info("resolveToken] HTTP 헤더에서 Token 값 추출");
        return request.getHeader("X-AUTH-TOKEN");
    }

    //토큰 유효성 검사
    //  1. 서버가 발급한 토큰이 맞는지
    //  2. 아직 만료되지 않았는지
    //parseClaimsJws(token) : 실제 토큰 파싱 및 서명 검증
    public boolean validateToken(String token) {
        LOGGER.info("validationToken] 토큰 유효 체크 시작");
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date()); //만료된 토큰이 아니면 true
        } catch (Exception e) { //토큰 유효성 검사에 실패했다면
            LOGGER.info("[validateToken] 토큰 유효 체크 예외 발생");

            return false;
        }
    }

}
