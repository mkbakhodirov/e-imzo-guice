package com.example.eimzoguice.web;

import com.example.eimzoguice.config.AppConfig;
import com.google.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class FrontendTimestampServlet extends FrontendProxySupport {
    @Inject
    public FrontendTimestampServlet(AppConfig config) {
        super(config);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        proxyTimestamp(request, response);
    }
}
