// com.mycompany.vizsgaremek.controller.ApplicationConfig.java
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.security.CorsFilter;
import com.mycompany.vizsgaremek.security.JwtAuthFilter;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;
import java.util.HashSet;


@ApplicationPath("/api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        
        // Controllers
        resources.add(UsersController.class);
        resources.add(CoursesController.class);  // ✅ ÚJ: Tanfolyamok kezelése
        
        // Filters
        resources.add(JwtAuthFilter.class);
        resources.add(CorsFilter.class);
        
        return resources;
    }
}