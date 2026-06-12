package com.example.eimzoguice.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

public class DemoCabinetServlet extends ServletSupport {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
}
