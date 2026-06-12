package com.example.eimzoguice.web;

import com.example.eimzoguice.service.EImzoService;
import com.google.gson.Gson;
import com.google.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import uz.yt.eimzo.server.client.json.AuthJsonResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DemoAuthServlet extends ServletSupport {
    private final EImzoService eImzoService;
    private final Gson gson;

    @Inject
    public DemoAuthServlet(EImzoService eImzoService, Gson gson) {
        this.eImzoService = eImzoService;
        this.gson = gson;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            String keyId = requiredParameter(request, "keyId");
            String pkcs7 = requiredParameter(request, "pkcs7");
            AuthJsonResponse auth = eImzoService.authenticate(pkcs7, request);
            result.put("status", auth.getStatus());
            if (auth.getStatus() != 1) {
                result.put("message", auth.getMessage());
                json(response, gson, result);
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("USER_INFO", gson.toJson(auth.getSubjectCertificateInfo()));
            session.setAttribute("KEY_ID", keyId);
            result.put("redirect", "cabinet");
            json(response, gson, result);
        } catch (Exception e) {
            json(response, gson, errorBody(e));
        }
    }
}
