package org.jml.httpclient5test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChain.Scope;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.ChainElement;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;

/**
 * Demonstrates how to customize processing with interception at request and response time.
 */
public class HttpClient5Interceptors {

    public static void main(final String[] args) throws Exception {
        try (final CloseableHttpClient httpclient = HttpClients.custom()
            //  Add a request id to the request header
            .addRequestInterceptorFirst(new HttpRequestInterceptor() {
                private final AtomicLong count = new AtomicLong(0);
                @Override
                public void process(
                    final HttpRequest request,
                    final EntityDetails entity,
                    final HttpContext context) throws HttpException, IOException {
                    request.setHeader("request-id", Long.toString(count.incrementAndGet()));
                }
            })
            .addExecInterceptorAfter(ChainElement.PROTOCOL.name(), "custom", new ExecChainHandler() {
                // For request id 2, simulate a 404 response and customize the content of the response.
                @Override
                public ClassicHttpResponse execute(
                    final ClassicHttpRequest request,
                    final Scope scope,
                    final ExecChain chain) throws IOException, HttpException {

                    final Header idHeader = request.getFirstHeader("request-id");
                    if (idHeader != null && "2".equalsIgnoreCase(idHeader.getValue())) {
                        final ClassicHttpResponse response = new BasicClassicHttpResponse(HttpStatus.SC_NOT_FOUND,
                            "Oppsie");
                        response.setEntity(new StringEntity("bad luck", ContentType.TEXT_PLAIN));
                        return response;
                    } else {
                        return chain.proceed(request, scope);
                    }
                }
            })
            .build()) {

            for (int i = 0; i < 3; i++) {
                final HttpGet httpget = new HttpGet("http://httpbin.org/get");

                try (final CloseableHttpResponse response = httpclient.execute(httpget)) {
                    System.out.println("----------------------------------------");
                    System.out.println("Execute request " + httpget.getMethod() + " " + httpget.getUri());
                    System.out.println(response.getCode() + " " + response.getReasonPhrase());
                    System.out.println(EntityUtils.toString(response.getEntity()));
                }
            }
        }
    }

}