package com.example.eimzoguice.web;

import com.example.eimzoguice.service.EImzoService;
import com.google.gson.Gson;
import com.google.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BackendServlet extends ServletSupport {
    private final EImzoService eImzoService;
    private final Gson gson;

    @Inject
    public BackendServlet(EImzoService eImzoService, Gson gson) {
        this.eImzoService = eImzoService;
        this.gson = gson;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            switch (routePath(request)) {
                case "/backend/auth" -> authenticate(request, response);
                case "/backend/pkcs7/verify/attached" -> verifyAttached(request, response);
                case "/backend/pkcs7/verify/detached" -> verifyDetached(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            json(response, gson, errorBody(e));
        }
    }

    private void authenticate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String pkcs7 = body(request);
        json(response, gson, eImzoService.authenticate(pkcs7, request));
    }

    private void verifyAttached(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String pkcs7 = body(request);
        json(response, gson, eImzoService.verifyAttached(pkcs7, request));
    }

    private void verifyDetached(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String[] parts = body(request).split("\\|", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Detached verification body must be data64|pkcs7");
        }
        json(response, gson, eImzoService.verifyDetached(parts[0], parts[1], request));
    }

    private String body(HttpServletRequest request) throws IOException {
        return new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
