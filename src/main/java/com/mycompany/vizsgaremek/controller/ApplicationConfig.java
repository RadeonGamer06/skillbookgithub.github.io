// com.mycompany.vizsgaremek.controller.ApplicationConfig.java
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.security.JwtUtil;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;
import java.util.HashSet;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(UsersController.class);
        resources.add(JwtUtil.class);
        // ha van CORS filter, azt is ide
        return resources;
    }
}

