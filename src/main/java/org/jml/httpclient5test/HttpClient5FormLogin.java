package org.jml.httpclient5test;

import java.util.ArrayList;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

/**
 * Demo of form-based login
 * 
 * @author https://www.wdbyte.com
 */
public class HttpClient5FormLogin {

    public static void main(final String[] args) throws Exception {
        final BasicCookieStore cookieStore = new BasicCookieStore();
        try (final CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build()) {

            // I should have used a POST request to send the form parameters, but there is no corresponding interface in httpbin.org for testing, so I switched to a GET request here
            // HttpPost httpPost = new HttpPost("http://httpbin.org/cookies/set/username/wdbyte.com");
            HttpGet httpPost = new HttpGet("http://httpbin.org/cookies/set/username/wdbyte.com");
            // POST Form
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("username", "wdbyte.com"));
            nvps.add(new BasicNameValuePair("password", "secret"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));

            try (final CloseableHttpResponse response2 = httpclient.execute(httpPost)) {
                final HttpEntity entity = response2.getEntity();

                System.out.println("Login form get: " + response2.getCode() + " " + response2.getReasonPhrase());
                System.out.println("Current response information "+EntityUtils.toString(entity));;

                System.out.println("Post Login Cookie:");
                final List<Cookie> cookies = cookieStore.getCookies();
                if (cookies.isEmpty()) {
                    System.out.println("None");
                } else {
                    for (int i = 0; i < cookies.size(); i++) {
                        System.out.println("- " + cookies.get(i));
                    }
                }
            }
        }
    }
}