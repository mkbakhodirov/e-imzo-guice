package com.example.eimzoguice.web;

import com.example.eimzoguice.service.EImzoService;
import com.google.gson.Gson;
import com.google.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import io.github.qo0p.eimzo.server.client.json.AuthJsonResponse;
import io.github.qo0p.eimzo.server.client.json.Pkcs7VerifyJsonResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DemoServlet extends ServletSupport {
    private final EImzoService eImzoService;
    private final Gson gson;

    @Inject
    public DemoServlet(EImzoService eImzoService, Gson gson) {
        this.eImzoService = eImzoService;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        switch (routePath(request)) {
            case "/demo" -> response.sendRedirect(request.getContextPath() + "/demo/");
            case "/demo/" -> index(request, response);
            case "/demo/cabinet" -> cabinet(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        switch (routePath(request)) {
            case "/demo/auth" -> auth(request, response);
            case "/demo/verify" -> verify(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void index(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("USER_INFO");
        session.removeAttribute("KEY_ID");
        forward(request, response, "/WEB-INF/views/demo/index.html");
    }

    private void cabinet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Object userInfo = session.getAttribute("USER_INFO");
        Object keyId = session.getAttribute("KEY_ID");

        if (userInfo == null) {
            html(response, "/WEB-INF/views/demo/cabinet-unauthorized.html", Map.of());
            return;
        }
        html(response, "/WEB-INF/views/demo/cabinet.html", Map.of(
                "userInfo", userInfo.toString(),
                "keyId", String.valueOf(keyId)
        ));
    }

    private void auth(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

    private void verify(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
