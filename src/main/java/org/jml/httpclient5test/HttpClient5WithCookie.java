package org.jml.httpclient5test;

import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.io.entity.EntityUtils;

/**
 * This example demonstrates the use of native HTTP context filling, custom properties.
 */
public class HttpClient5WithCookie {

    public static void main(final String[] args) throws Exception {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            // Create a local cookie store
            final CookieStore cookieStore = new BasicCookieStore();
            // BasicClientCookie clientCookie = new BasicClientCookie("name", "www.wdbyte.com");
            // clientCookie.setDomain("http://httpbin.org/cookies");
            // Expiration time
            // clientCookie.setExpiryDate(new Date());
            // addCookie
            // cookieStore.addCookie(clientCookie);

            // Create local HttpClientContext
            final HttpClientContext localContext = HttpClientContext.create();
            // Bind the cookieStore to the localContext
            localContext.setCookieStore(cookieStore);

            final HttpGet httpget = new HttpGet("http://httpbin.org/cookies/set/cookieName/www.wdbyte.com");
            System.out.println("Execute request " + httpget.getMethod() + " " + httpget.getUri());

            // Get Cookie information.
            try (final CloseableHttpResponse response = httpclient.execute(httpget, localContext)) {
                System.out.println("----------------------------------------");
                System.out.println(response.getCode() + " " + response.getReasonPhrase());
                final List<Cookie> cookies = cookieStore.getCookies();
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("Local cookie: " + cookies.get(i));
                }
                EntityUtils.consume(response.getEntity());
            }
        }
    }

}