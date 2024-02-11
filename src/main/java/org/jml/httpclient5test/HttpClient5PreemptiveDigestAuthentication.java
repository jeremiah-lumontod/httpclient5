package org.jml.httpclient5test;

import org.apache.hc.client5.http.auth.AuthExchange;
import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.DigestScheme;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.EntityUtils;

/**
 *
 * Example of how HttpClient validates multiple requests
 */
public class HttpClient5PreemptiveDigestAuthentication {

    public static void main(final String[] args) throws Exception {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {

            final HttpHost target = new HttpHost("http", "httpbin.org", 80);

            final HttpClientContext localContext = HttpClientContext.create();
            final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                    new AuthScope(target),
                    new UsernamePasswordCredentials("admin", "123456".toCharArray()));
            localContext.setCredentialsProvider(credentialsProvider);

            final HttpGet httpget = new HttpGet("http://httpbin.org/digest-auth/auth/admin/123456");

            System.out.println("Execute request " + httpget.getMethod() + " " + httpget.getUri());
            for (int i = 0; i < 2; i++) {
                try (final CloseableHttpResponse response = httpclient.execute(target, httpget, localContext)) {
                    System.out.println("----------------------------------------");
                    System.out.println(response.getCode() + " " + response.getReasonPhrase());
                    EntityUtils.consume(response.getEntity());

                    final AuthExchange authExchange = localContext.getAuthExchange(target);
                    if (authExchange != null) {
                        final AuthScheme authScheme = authExchange.getAuthScheme();
                        if (authScheme instanceof DigestScheme) {
                            final DigestScheme digestScheme = (DigestScheme) authScheme;
                            System.out.println("Nonce: " + digestScheme.getNonce() +
                                    "; count: " + digestScheme.getNounceCount());
                        }
                    }
                }
            }
        }
    }

}