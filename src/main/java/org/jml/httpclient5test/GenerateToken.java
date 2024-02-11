package org.jml.httpclient5test;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GenerateToken {

    public static void main(String[] args) {
        String result = post("http://localhost:8000/realms/appsdeveloperblog/protocol/openid-connect/token");
        System.out.println(result);
    }
    public static String post(String url) {
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        // form parameters.
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("grant_type", "password"));
        nvps.add(new BasicNameValuePair("username", "dev"));
        nvps.add(new BasicNameValuePair("password", "Dev@123"));
        nvps.add(new BasicNameValuePair("client_id", "photo-app-PKCE-client"));
        nvps.add(new BasicNameValuePair("client_secret", "1ELQVUPVLPx66Py1jqSIBtBIT1BUJCag"));
        nvps.add(new BasicNameValuePair("scope", "openid profile"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                System.out.println(response.getVersion()); // HTTP/1.1
                System.out.println(response.getCode()); // 200
                System.out.println(response.getReasonPhrase()); // OK

                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
                //System.out.println(result);
                // Ensure that the stream is fully consumed
                EntityUtils.consume(entity);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}