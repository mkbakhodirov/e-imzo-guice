package com.example.eimzoguice.web;

import com.example.eimzoguice.service.EImzoService;
import com.google.gson.Gson;
import com.google.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BackendAuthServlet extends ServletSupport {
    private final EImzoService eImzoService;
    private final Gson gson;

    @Inject
    public BackendAuthServlet(EImzoService eImzoService, Gson gson) {
        this.eImzoService = eImzoService;
        this.gson = gson;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pkcs7 = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            json(response, gson, eImzoService.authenticate(pkcs7, request));
        } catch (Exception e) {
            json(response, gson, errorBody(e));
        }
    }
}
