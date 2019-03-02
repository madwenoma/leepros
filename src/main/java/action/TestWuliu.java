package action;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2018/1/19.
 */
public class TestWuliu {
    public static void main(String[] args) throws IOException {
        System.out.println(DatatypeConverter.parseBase64Binary("ungcpbltavgahkmqoeed"));

        String appSecret = "ungcpbltavgahkmqoeed";
        String appKey = "VZtpcq07i";
        Algorithm algorithm = Algorithm.HMAC256(appSecret);
        String token = JWT.create()
                .withClaim("appKey", appKey)
                .withClaim("uid", "155553")
                .withClaim("nickname", "李佳")
                .withClaim("avatar", "")
                .withClaim("mobile", "")
                .withIssuedAt(new Date()) //签名的时间戳，⽂漫据此判断签名有效期
                .sign(algorithm);
        System.out.println(token.length());
    }

}
