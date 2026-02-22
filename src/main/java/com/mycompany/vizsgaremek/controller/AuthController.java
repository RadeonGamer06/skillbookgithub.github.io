package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("Auth")
public class AuthController {

    @POST
    @Path("validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateToken(String body) {
        try {
            JSONObject json = new JSONObject(body);
            String token = json.optString("token", null);

            if (token == null || token.trim().isEmpty()) {
                JSONObject error = new JSONObject();
                error.put("statusCode", 400);
                error.put("valid", false);
                error.put("message", "Token kötelező");

                return Response.status(400)
                        .entity(error.toString())
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            Claims claims = JwtUtil.validate(token);
            
            Integer userId = claims.get("userId", Integer.class);
            String email = claims.getSubject();

            JSONObject response = new JSONObject();
            response.put("statusCode", 200);
            response.put("valid", true);
            response.put("userId", userId);
            response.put("email", email);
            response.put("issuedAt", claims.getIssuedAt().getTime());
            response.put("expiration", claims.getExpiration().getTime());

            return Response.ok()
                    .entity(response.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (JwtException e) {
            JSONObject error = new JSONObject();
            error.put("statusCode", 401);
            error.put("valid", false);
            error.put("message", "Érvénytelen vagy lejárt token: " + e.getMessage());

            return Response.status(401)
                    .entity(error.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("statusCode", 500);
            error.put("valid", false);
            error.put("message", "Szerver hiba: " + e.getMessage());

            return Response.status(500)
                    .entity(error.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @POST
    @Path("refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshToken(@Context HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                JSONObject error = new JSONObject();
                error.put("statusCode", 400);
                error.put("message", "Hiányzó vagy hibás Authorization header");

                return Response.status(400)
                        .entity(error.toString())
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            String token = authHeader.substring(7);
            Claims claims;

            try {
                claims = JwtUtil.validate(token);
            } catch (ExpiredJwtException e) {
                claims = e.getClaims();

                long gracePeriodMs = 24 * 60 * 60 * 1000L; // 1 nap
                if (claims.getExpiration().getTime() + gracePeriodMs < System.currentTimeMillis()) {
                    JSONObject error = new JSONObject();
                    error.put("statusCode", 401);
                    error.put("message", "A token túl rég lejárt, nem frissíthető. Kérlek jelentkezz be újra.");

                    return Response.status(401)
                            .entity(error.toString())
                            .type(MediaType.APPLICATION_JSON)
                            .build();
                }
            }

            Integer userId = claims.get("userId", Integer.class);
            String email = claims.getSubject();

            if (userId == null || email == null) {
                throw new JwtException("Hiányzó adatok a tokenben");
            }

            String newToken = JwtUtil.generateToken(userId, email);

            JSONObject response = new JSONObject();
            response.put("statusCode", 200);
            response.put("token", newToken);
            response.put("message", "Token sikeresen frissítve");

            return Response.ok()
                    .entity(response.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (JwtException e) {
            JSONObject error = new JSONObject();
            error.put("statusCode", 401);
            error.put("message", "Érvénytelen token: " + e.getMessage());

            return Response.status(401)
                    .entity(error.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("statusCode", 500);
            error.put("message", "Szerver hiba: " + e.getMessage());

            return Response.status(500)
                    .entity(error.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @GET
    @Path("info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTokenInfo(@Context HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                JSONObject error = new JSONObject();
                error.put("statusCode", 400);
                error.put("message", "Hiányzó Authorization header");

                return Response.status(400)
                        .entity(error.toString())
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            String token = authHeader.substring(7);
            Claims claims = JwtUtil.validate(token);

            JSONObject response = new JSONObject();
            response.put("statusCode", 200);
            response.put("userId", claims.get("userId", Integer.class));
            response.put("email", claims.getSubject());
            response.put("issuedAt", claims.getIssuedAt());
            response.put("expiration", claims.getExpiration());
            response.put("remainingTime", claims.getExpiration().getTime() - System.currentTimeMillis());

            return Response.ok()
                    .entity(response.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (JwtException e) {
            JSONObject error = new JSONObject();
            error.put("statusCode", 401);
            error.put("message", "Érvénytelen token");

            return Response.status(401)
                    .entity(error.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("statusCode", 500);
            error.put("message", "Szerver hiba: " + e.getMessage());

            return Response.status(500)
                    .entity(error.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}