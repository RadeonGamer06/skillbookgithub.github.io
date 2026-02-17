package com.mycompany.vizsgaremek.security;

import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class JwtAuthFilter implements ContainerRequestFilter {

    @Context
    private HttpServletRequest servletRequest;

    // ════════════════════════════════════════════════════════════════════════
    // FONTOS: NINCS kezdő perjel! A path így érkezik: "Users/login" stb.
    // ════════════════════════════════════════════════════════════════════════
    private static final String[] PUBLIC_PREFIXES = {
            "Users/login",
            "Users/createUser",
            "Users/forgotPassword",
            "Auth/validate",
            "Auth/refresh",
            "Courses/getAllCourses",
            "Courses/getCourseById",
            "Contact/sendMessage",
            "Categories/getAllCategories",
            "Uploads/"
    };

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // CORS preflight → átengedjük
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            return;
        }

        String path = requestContext.getUriInfo().getPath();
        if (path == null) path = "";
        if (path.startsWith("/")) path = path.substring(1);

        System.out.println("JWT FILTER PATH = " + path);

        // Public endpointok ellenőrzése
        for (String prefix : PUBLIC_PREFIXES) {
            if (path.startsWith(prefix)) {
                System.out.println("JWT FILTER: Public path, engedélyezve");
                return;
            }
        }

        // JWT token kötelező
        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("JWT FILTER: Nincs Bearer token");
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"message\":\"Missing or invalid Authorization header\"}")
                            .type("application/json")
                            .build()
            );
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = JwtUtil.validate(token);
            Integer userId = claims.get("userId", Integer.class);

            if (userId == null) {
                requestContext.abortWith(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity("{\"message\":\"Invalid token - missing userId\"}")
                                .type("application/json")
                                .build()
                );
                return;
            }

            // ════════════════════════════════════════════════════════════
            // userId beállítása a request attribute-ba → így éri el minden
            // Controller a @Context HttpServletRequest request.getAttribute("userId")-vel
            // ════════════════════════════════════════════════════════════
            if (servletRequest != null) {
                servletRequest.setAttribute("userId", userId);
                System.out.println("JWT FILTER: userId beállítva = " + userId);
            } else {
                System.err.println("JWT FILTER: servletRequest is NULL!");
            }

        } catch (Exception e) {
            System.out.println("JWT FILTER: Token érvénytelen - " + e.getMessage());
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"message\":\"Invalid or expired token\"}")
                            .type("application/json")
                            .build()
            );
        }
    }
}