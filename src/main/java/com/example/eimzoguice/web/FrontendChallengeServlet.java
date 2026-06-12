package com.example.eimzoguice.web;

import com.example.eimzoguice.config.AppConfig;
import com.google.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class FrontendChallengeServlet extends FrontendProxySupport {
    @Inject
    public FrontendChallengeServlet(AppConfig config) {
        super(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        proxyChallenge(request, response);
    }
}
