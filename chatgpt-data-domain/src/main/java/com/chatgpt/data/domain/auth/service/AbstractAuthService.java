package com.chatgpt.data.domain.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.chatgpt.data.domain.auth.model.entity.AuthStateEntity;
import com.chatgpt.data.domain.auth.model.valobj.AuthTypeVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @description: 使用抽象类把 JWT 生成 Token 单独提到抽象类里。<br/>简化了 Shiro 等组件的引入，只需要 JWT 的 Token 校验即可。<br/>如果你提供的服务有更多的场景需要校验，可以引用 Shiro 或者 Spring Security 框架
 * @date 2024/2/27 21:31
 */
@Slf4j
public abstract class AbstractAuthService implements IAuthService {
    private static final String defaultBase64EncodedSecretKey = "B*B^D%fe";
    private final String base64EncodedSecretKey = Base64.encodeBase64String(defaultBase64EncodedSecretKey.getBytes());
    private final Algorithm algorithm = Algorithm.HMAC256(Base64.decodeBase64(base64EncodedSecretKey));

    @Override
    public AuthStateEntity doLogin(String code) {
        // 1. 如果不是4位有效数字字符串，则返回验证码无效
        if (!code.matches("\\d{4}")) {
            log.info("鉴权，用户输入的验证码无效 {}", code);
            return AuthStateEntity.builder()
                    .code(AuthTypeVO.A0002.getCode())
                    .info(AuthTypeVO.A0002.getInfo())
                    .build();
        }
        // 2. 校验判断，非成功则直接返回
        AuthStateEntity authStateEntity = this.checkCode(code);
        if (!authStateEntity.getCode().equals(AuthTypeVO.A0000.getCode())) {
            return authStateEntity;
        }

        // 3. 获取 Token 并返回
        HashMap<String, Object> chaim = new HashMap<>();
        chaim.put("openId", authStateEntity.getOpenId());
        String token = encode(authStateEntity.getOpenId(), 7 * 24 * 60 * 60 * 1000, chaim);
        authStateEntity.setToken(token);
        return authStateEntity;
    }

    protected abstract AuthStateEntity checkCode(String code);

    /**
     * 生成jwt字符串
     * <br/>jwt字符串包括三个部分
     * <br/>1. header（头部）
     * <br/>-当前字符串的类型，一般都是“JWT”
     * <br/>-哪种算法加密，“HS256”或者其他的加密算法
     * <br/>所以一般都是固定的，没有什么变化
     * <br/>2. payload（有效载荷）
     * <br/>一般有四个最常见的标准字段（下面有）
     * <br/>iat：签发时间，也就是这个jwt什么时候生成的
     * <br/>jti：JWT的唯一标识
     * <br/>iss：签发人，一般都是username或者userId
     * <br/>exp：过期时间
     * <br/>3. signature（签名）
     * <br/>签名是对前两部分（即header和payload经过Base64Url编码后的字符串）的加密结果，通过指定的签名算法和一个只有服务器知道的秘密key（secret）生成，以确保消息在传输过程中未被篡改。签名过程通常结合了header中声明的加密算法
     */
    protected String encode(String issuer, long ttlMillis, Map<String, Object> claims) {
        // issuer签发人，ttlMillis生存时间，claims是指还想要在jwt中存储一些非隐私信息
        if (claims == null) {
            claims = new HashMap<>();
        }
        long nowMillis = System.currentTimeMillis();
        JwtBuilder builder = Jwts.builder()
                // 载荷
                .setClaims(claims)
                // JWT的唯一标识
                .setId(UUID.randomUUID().toString())
                // 签发时间
                .setIssuedAt(new Date(nowMillis))
                // 签发人（一般都是username或者userId）
                .setSubject(issuer)
                // 生成jwt使用的算法和秘钥
                .signWith(SignatureAlgorithm.HS256, base64EncodedSecretKey);
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();

    }

    // 解析jwtToken，传入jwtToken生成对应的username和password等字段。Claim是一个map
    // 也就是拿到荷载部分所有的键值对
    protected Claims decode(String jwtToken) {
        // 得到 DefaJwtParser
        return Jwts.parser()
                // 设置签名的密钥
                .setSigningKey(base64EncodedSecretKey)
                .parseClaimsJws(jwtToken)
                .getBody();
    }


    // 判断jwtToken是否合法
    protected boolean isVerify(String jwtToken) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(jwtToken);
            // 校验不通过会抛出异常
            // 判断合法的标准：1. 头部和荷载部分没有篡改过。2. 没有过期
            return true;
        } catch (Exception e) {
            log.error("鉴权，Token 校验失败 {}", jwtToken);
            return false;
        }
    }
}
