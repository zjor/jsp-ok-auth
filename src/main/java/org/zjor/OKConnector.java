package org.zjor;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Sergey Royz
 * @since: 21.10.2013
 */
@Slf4j
public class OKConnector {

    public static final String CODE_EXCHANGE_URL = "http://api.odnoklassniki.ru/oauth/token.do";
    public static final String API_ENDPOINT_URL = "http://api.odnoklassniki.ru/fb.do";


    private CloseableHttpClient httpClient;

    private String clientId;
    private String appKey;
    private String appSecret;

    public OKConnector(String clientId, String appKey, String appSecret) {
        this.clientId = clientId;
        this.appKey = appKey;
        this.appSecret = appSecret;

        httpClient = HttpClientBuilder.create().build();
    }

    public String exchangeCode(String code, String redirectUri) throws IOException {

        HttpPost request = new HttpPost(CODE_EXCHANGE_URL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("redirect_uri", redirectUri));
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("client_secret", appSecret));

        request.setEntity(new UrlEncodedFormEntity(params));

        HttpResponse httpResponse = httpClient.execute(request);
        String content = EntityUtils.toString(httpResponse.getEntity());
        log.info("Response: {}", content);

        AccessToken accessToken = (new GsonBuilder()).create().fromJson(content, AccessToken.class);
        return accessToken.getAccessToken();
    }

    public OkUserDTO getCurrentUser(String accessToken) throws IOException {
        URIBuilder uriBuilder = fromString(API_ENDPOINT_URL);
        String method = "users.getCurrentUser";
        uriBuilder
                .setParameter("sig", generateSignature(method, accessToken))
                .setParameter("access_token", accessToken)
                .setParameter("application_key", appKey)
                .setParameter("method", method);
        HttpResponse response = httpClient.execute(new HttpGet(uriBuilder.toString()));
        String content = EntityUtils.toString(response.getEntity());
        log.info(content);

        return new GsonBuilder().create().fromJson(content, OkUserDTO.class);
    }

    private String generateSignature(String method, String accessToken) {
        StringBuilder params = new StringBuilder();
        params
                .append("application_key=").append(appKey)
                .append("method=").append(method)
                .append(DigestUtils.md5Hex(accessToken + appSecret));

        return DigestUtils.md5Hex(params.toString());
    }

    private URIBuilder fromString(String s) {
        try {
            return new URIBuilder(s);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    private static class AccessToken {

        @SerializedName("token_type")
        private String tokenType;

        @SerializedName("refresh_token")
        private String refreshToken;

        @SerializedName("access_token")
        private String accessToken;
    }


}
