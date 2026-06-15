package com.example.eimzoguice.service;

import com.example.eimzoguice.config.AppConfig;
import com.example.eimzoguice.util.BaseUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.http.HttpServletRequest;
import io.github.qo0p.eimzo.server.client.Client;
import io.github.qo0p.eimzo.server.client.http.HttpClient;
import io.github.qo0p.eimzo.server.client.json.AuthJsonResponse;
import io.github.qo0p.eimzo.server.client.json.Pkcs7VerifyJsonResponse;

import java.net.MalformedURLException;
import java.net.URL;

@Singleton
public class EImzoService {
    private final Client client;
    private final String challengerHost;

    @Inject
    public EImzoService(AppConfig config) {
        try {
            this.client = new HttpClient(new URL(config.required("eimzo.rest.service.host.base")));
            this.challengerHost = config.required("eimzo.rest.service.host.challenger");
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid E-IMZO base URL", e);
        }
    }

    public AuthJsonResponse authenticate(String pkcs7, HttpServletRequest request) throws Exception {
        String clientIp = BaseUtils.getClientIp(request);
        return client.auth(clientIp, challengerHost, pkcs7);
    }

    public Pkcs7VerifyJsonResponse verifyAttached(String pkcs7, HttpServletRequest request) throws Exception {
        String clientIp = BaseUtils.getClientIp(request);
        return client.verifyPkcs7Attached(clientIp, challengerHost, pkcs7);
    }

    public Pkcs7VerifyJsonResponse verifyDetached(String data64, String pkcs7, HttpServletRequest request) throws Exception {
        String clientIp = BaseUtils.getClientIp(request);
        return client.verifyPkcs7Detached(clientIp, challengerHost, data64, pkcs7);
    }
}
