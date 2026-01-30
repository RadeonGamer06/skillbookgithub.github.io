package com.mycompany.vizsgaremek.security;

import com.mycompany.vizsgaremek.security.JwtUtil;
import io.jsonwebtoken.Claims;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.IOException;

@Provider
@PreMatching
public class JwtAuthFilter implements ContainerRequestFilter {

    @Context
    private HttpServletRequest servletRequest;

    private static final String[] PUBLIC_PATHS = {
        "/Users/login",
        "/Users/createUser",
        "/Courses/getAllCourses",  // ✅ Tanfolyamok listája nyilvános
        "/Courses/getCourseById"   // ✅ Tanfolyam részletei nyilvános
    };

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        System.out.println("JWT FILTER PATH = " + path);

        // Public endpoint-ok
        for (String publicPath : PUBLIC_PATHS) {
            if (path.contains(publicPath)) {
                System.out.println("JWT FILTER: Public path, engedélyezve");
                return;
            }
        }

        // Authorization header
        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("JWT FILTER: Nincs Bearer token");
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"Missing or invalid Authorization header\"}")
                    .build()
            );
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = JwtUtil.validate(token);
            Integer userId = claims.get("userId", Integer.class);

            // ✅ KRITIKUS: HttpServletRequest-be írjuk az userId-t
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
                    .build()
            );
        }
    }
}