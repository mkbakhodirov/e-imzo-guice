package com.example.eimzoguice.web;

import com.example.eimzoguice.config.AppConfig;
import com.example.eimzoguice.util.BaseUtils;
import com.google.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

abstract class FrontendProxySupport extends ServletSupport {
    private final String eimzoServerBaseUrl;
    private final String challengeUrl;

    @Inject
    protected FrontendProxySupport(AppConfig config) {
        this.eimzoServerBaseUrl = config.required("eimzo.rest.service.host.base");
        this.challengeUrl = config.required("eimzo.rest.service.host.challenger");
    }

    protected void proxyChallenge(HttpServletRequest request, HttpServletResponse response) throws IOException {
        proxy("GET", challengeEndpoint(request), null, request, response);
    }

    protected void proxyTimestamp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        byte[] pkcs7 = request.getInputStream().readAllBytes();
        proxy("POST", join(eimzoServerBaseUrl, "/frontend/timestamp/pkcs7"), pkcs7, request, response);
    }

    private String challengeEndpoint(HttpServletRequest request) {
        String query = request.getQueryString();
        String url = challengeUrl.contains("/frontend/challenge")
                ? challengeUrl
                : join(challengeUrl, "/frontend/challenge");
        return query == null || query.isBlank() ? url : url + "?" + query;
    }

    private void proxy(String method, String url, byte[] body, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String clientIp = BaseUtils.getClientIp(request);
        System.out.printf("clientIp: %s; url: %s%n", clientIp, url);

        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(20000);
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("X-Real-IP", clientIp);

        if (body != null) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestProperty("Content-Length", Integer.toString(body.length));
            connection.setRequestProperty("Origin", eimzoServerBaseUrl);
            connection.setRequestProperty("Referer", join(eimzoServerBaseUrl, "/demo/cabinet.php"));
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            try (OutputStream output = connection.getOutputStream()) {
                output.write(body);
            }
        }

        int status = connection.getResponseCode();
        InputStream stream = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
        String responseBody = stream == null ? "" : new String(stream.readAllBytes(), StandardCharsets.UTF_8);

        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response.getWriter().write(responseBody);
    }

    private String join(String baseUrl, String path) {
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + path;
        }
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
    }
}
