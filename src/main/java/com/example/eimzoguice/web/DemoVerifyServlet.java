package com.example.eimzoguice.web;

import com.example.eimzoguice.service.EImzoService;
import com.google.gson.Gson;
import com.google.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uz.yt.eimzo.server.client.json.Pkcs7VerifyJsonResponse;

import java.io.IOException;

public class DemoVerifyServlet extends ServletSupport {
    private final EImzoService eImzoService;
    private final Gson gson;

    @Inject
    public DemoVerifyServlet(EImzoService eImzoService, Gson gson) {
        this.eImzoService = eImzoService;
        this.gson = gson;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pkcs7wtst = requiredParameter(request, "pkcs7wtst");
            String data64 = request.getParameter("data64");
            Pkcs7VerifyJsonResponse result = data64 == null || data64.isBlank()
                    ? eImzoService.verifyAttached(pkcs7wtst, request)
                    : eImzoService.verifyDetached(data64, pkcs7wtst, request);
            json(response, gson, result);
        } catch (Exception e) {
            json(response, gson, errorBody(e));
        }
    }
}
