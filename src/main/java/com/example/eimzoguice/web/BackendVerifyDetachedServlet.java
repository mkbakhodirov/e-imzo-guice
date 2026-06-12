package com.example.eimzoguice.web;

import com.example.eimzoguice.service.EImzoService;
import com.google.gson.Gson;
import com.google.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BackendVerifyDetachedServlet extends ServletSupport {
    private final EImzoService eImzoService;
    private final Gson gson;

    @Inject
    public BackendVerifyDetachedServlet(EImzoService eImzoService, Gson gson) {
        this.eImzoService = eImzoService;
        this.gson = gson;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String[] parts = body.split("\\|", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Detached verification body must be data64|pkcs7");
            }
            json(response, gson, eImzoService.verifyDetached(parts[0], parts[1], request));
        } catch (Exception e) {
            json(response, gson, errorBody(e));
        }
    }
}
