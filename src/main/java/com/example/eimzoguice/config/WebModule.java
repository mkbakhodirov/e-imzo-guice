package com.example.eimzoguice.config;

import com.example.eimzoguice.service.EImzoService;
import com.example.eimzoguice.web.BackendAuthServlet;
import com.example.eimzoguice.web.BackendVerifyAttachedServlet;
import com.example.eimzoguice.web.BackendVerifyDetachedServlet;
import com.example.eimzoguice.web.DemoAuthServlet;
import com.example.eimzoguice.web.DemoCabinetServlet;
import com.example.eimzoguice.web.DemoIndexServlet;
import com.example.eimzoguice.web.DemoRedirectServlet;
import com.example.eimzoguice.web.DemoVerifyServlet;
import com.google.gson.Gson;
import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;

public class WebModule extends ServletModule {
    @Override
    protected void configureServlets() {
        bind(AppConfig.class).in(Scopes.SINGLETON);
        bind(EImzoService.class).in(Scopes.SINGLETON);
        bind(Gson.class).in(Scopes.SINGLETON);
        bind(DemoRedirectServlet.class).in(Scopes.SINGLETON);
        bind(DemoIndexServlet.class).in(Scopes.SINGLETON);
        bind(DemoAuthServlet.class).in(Scopes.SINGLETON);
        bind(DemoCabinetServlet.class).in(Scopes.SINGLETON);
        bind(DemoVerifyServlet.class).in(Scopes.SINGLETON);
        bind(BackendAuthServlet.class).in(Scopes.SINGLETON);
        bind(BackendVerifyAttachedServlet.class).in(Scopes.SINGLETON);
        bind(BackendVerifyDetachedServlet.class).in(Scopes.SINGLETON);

        serve("/demo").with(DemoRedirectServlet.class);
        serve("/demo/").with(DemoIndexServlet.class);
        serve("/demo/auth").with(DemoAuthServlet.class);
        serve("/demo/cabinet").with(DemoCabinetServlet.class);
        serve("/demo/verify").with(DemoVerifyServlet.class);

        serve("/backend/auth").with(BackendAuthServlet.class);
        serve("/backend/pkcs7/verify/attached").with(BackendVerifyAttachedServlet.class);
        serve("/backend/pkcs7/verify/detached").with(BackendVerifyDetachedServlet.class);
    }
}
