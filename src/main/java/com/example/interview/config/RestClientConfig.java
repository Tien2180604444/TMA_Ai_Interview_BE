package com.example.interview.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.cert.X509Certificate;

@Configuration
public class RestClientConfig {
    @Value("${gemini.base-url}")
    private String geminiBaseURL;
    @Bean
    public RestClient restClient() throws Exception {
        // Tạo TrustManager tin tưởng tất cả các chứng chỉ
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        // Tạo HttpClient của Java sử dụng SSLContext trên
        HttpClient httpClient = HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();

        // Build RestClient với HttpClient đã bypass SSL
        return RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .baseUrl(geminiBaseURL)
                .build();
    }
}
