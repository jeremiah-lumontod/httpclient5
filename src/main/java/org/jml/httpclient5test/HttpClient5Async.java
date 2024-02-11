package org.jml.httpclient5test;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.hc.client5.http.async.methods.AbstractCharResponseConsumer;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder;

/**
 * HttpClient 5 异步请求
* @author https://www.wdbyte.com
 * @date 2022/06/25
 */
public class HttpClient5Async {

    public static void main(String[] args) {
        getAsync1("http://httpbin.org/get");
        getAsync2("http://httpbin.org/get");
        getAsync3("http://httpbin.org/get");
    }

    /**
     * Asynchronous requests
     *
     * @param url
     * @return
     */
    public static String getAsync1(String url) {
        try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault()) {
            // start http clinet
            httpclient.start();
            // Execution of requests
            SimpleHttpRequest request1 = SimpleHttpRequests.get(url);
            Future<SimpleHttpResponse> future = httpclient.execute(request1, null);
            // Wait until the return is complete
            SimpleHttpResponse response1 = future.get();
            System.out.println("getAsync1:" + request1.getRequestUri() + "->" + response1.getCode());
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Asynchronous requests, callbacks based on response
     *
     * @param url
     * @return
     */
    public static String getAsync2(String url) {
        try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault()) {
            // start http clinet
            httpclient.start();

            final CountDownLatch latch = new CountDownLatch(1);
            final SimpleHttpRequest request = SimpleHttpRequests.get(url);
            httpclient.execute(request, new FutureCallback<SimpleHttpResponse>() {
                @Override
                public void completed(SimpleHttpResponse response2) {
                    latch.countDown();
                    System.out.println("getAsync2:" + request.getRequestUri() + "->" + response2.getCode());
                }

                @Override
                public void failed(Exception ex) {
                    latch.countDown();
                    System.out.println("getAsync2:" + request.getRequestUri() + "->" + ex);
                }

                @Override
                public void cancelled() {
                    latch.countDown();
                    System.out.println("getAsync2:" + request.getRequestUri() + " cancelled");
                }

            });
            latch.await();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Asynchronous requests, do something about the response stream
     *
     * @param url
     * @return
     */
    public static String getAsync3(String url) {
        try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault()) {
            // start http clinet
            httpclient.start();

            final SimpleHttpRequest request = SimpleHttpRequests.get(url);

            final CountDownLatch latch = new CountDownLatch(1);
            AsyncRequestProducer producer = AsyncRequestBuilder.get("http://httpbin.org/get").build();
            AbstractCharResponseConsumer<HttpResponse> consumer3 = new AbstractCharResponseConsumer<HttpResponse>() {

                HttpResponse response;

                @Override
                protected void start(HttpResponse response, ContentType contentType) throws HttpException, IOException {
                    System.out.println("getAsync3: Start response....");
                    this.response = response;
                }

                @Override
                protected int capacityIncrement() {
                    return Integer.MAX_VALUE;
                }

                @Override
                protected void data(CharBuffer data, boolean endOfStream) throws IOException {
                    System.out.println("getAsync3: Data received....");
                    // Do something useful
                }

                @Override
                protected HttpResponse buildResult() throws IOException {
                    System.out.println("getAsync3: Receiving completed...");
                    return response;
                }

                @Override
                public void releaseResources() {
                }

            };
            httpclient.execute(producer, consumer3, new FutureCallback<HttpResponse>() {

                @Override
                public void completed(HttpResponse response) {
                    latch.countDown();
                    System.out.println("getAsync3: "+request.getRequestUri() + "->" + response.getCode());
                }

                @Override
                public void failed(Exception ex) {
                    latch.countDown();
                    System.out.println("getAsync3: "+request.getRequestUri() + "->" + ex);
                }

                @Override
                public void cancelled() {
                    latch.countDown();
                    System.out.println("getAsync3: "+request.getRequestUri() + " cancelled");
                }

            });
            latch.await();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;

    }
}