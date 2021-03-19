package org.macross.apigateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.macross.AppleStore_Common_Config.model.entity.User;

import java.util.Date;

/**
 * JWT工具类
 */
public class JWTUtils {

    /**
     * 过期时间：一周
     */
    private static final long EXPIRE = 60000 * 60 * 24 * 7;

    /**
     * 加密密钥
     */
    private static final String SECRET = "macross.AF";

    /**
     * 令牌前缀
     */
    private static final String TOKEN_PREFIX = "shoppingMall";

    /**
     * subject
     */
    private static final String SUBJECT ="shoppingMall";

    /**
     * 根据用户信息，生成token
     * @param user
     * @return
     */
    public static String genericJsonWebToken(User user){

        String token = Jwts.builder().setSubject(SUBJECT)
                .claim("head_img",user.getHeadImg())
                .claim("id",user.getId())
                .claim("name",user.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256,SECRET).compact();

        token = TOKEN_PREFIX + token;
        return token;
    }

    /**
     * 校验token
     * @param token
     * @return
     */
    public static Claims checkJWT(String token){

        try{

            final Claims claims = Jwts.parser().setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX,"")).getBody();


            return claims;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


}
