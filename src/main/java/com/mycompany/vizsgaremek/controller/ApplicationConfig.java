package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.security.CorsFilter;
import com.mycompany.vizsgaremek.security.JwtAuthFilter;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();

        // Controllers
        resources.add(UsersController.class);
        resources.add(AuthController.class);
        resources.add(CoursesController.class);
        resources.add(ContactController.class);
        resources.add(CategoriesController.class);
        resources.add(MessagesController.class);
        resources.add(ReviewsController.class);
        resources.add(UploadsController.class);  // ✅ ÚJ - képek kiszolgálása
        
        // Security filters
        resources.add(JwtAuthFilter.class);
        resources.add(CorsFilter.class);

        return resources;
    }
}