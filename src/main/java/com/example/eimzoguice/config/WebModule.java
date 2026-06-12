package com.example.eimzoguice.config;

import com.example.eimzoguice.service.EImzoService;
import com.example.eimzoguice.web.BackendServlet;
import com.example.eimzoguice.web.DemoServlet;
import com.google.gson.Gson;
import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;

public class WebModule extends ServletModule {
    @Override
    protected void configureServlets() {
        bind(AppConfig.class).in(Scopes.SINGLETON);
        bind(EImzoService.class).in(Scopes.SINGLETON);
        bind(Gson.class).in(Scopes.SINGLETON);
        bind(DemoServlet.class).in(Scopes.SINGLETON);
        bind(BackendServlet.class).in(Scopes.SINGLETON);

        serve("/demo", "/demo/", "/demo/auth", "/demo/cabinet", "/demo/verify").with(DemoServlet.class);

        serve(
                "/backend/auth",
                "/backend/pkcs7/verify/attached",
                "/backend/pkcs7/verify/detached"
        ).with(BackendServlet.class);
    }
}
