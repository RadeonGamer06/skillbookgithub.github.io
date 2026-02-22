package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.AdminUsersService;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("Admin/Users")
@Produces(MediaType.APPLICATION_JSON)
public class AdminUsersController {

    @Inject
    private AdminUsersService adminUsersService;

    @GET
    @Path("getAllUsers")
    public Response getAllUsers(@Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject result = adminUsersService.getAllUsers();
        return build(result);
    }

    @PUT
    @Path("setRole/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setRole(@PathParam("id") int id,
                             String body,
                             @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();

        JSONObject json = new JSONObject(body);
        String role = json.optString("role", null);
        if (role == null || role.trim().isEmpty()) {
            JSONObject err = new JSONObject();
            err.put("statusCode", 400);
            err.put("message", "A 'role' mező kötelező (student | instructor | admin)");
            return Response.status(400).entity(err.toString()).build();
        }

        JSONObject result = adminUsersService.setRole(id, role.trim(), userId);
        return build(result);
    }

    @PUT
    @Path("updateUser/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") int id,
                                String body,
                                @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();

        JSONObject json = new JSONObject(body);
        String name  = json.optString("name",  null);
        String email = json.optString("email", null);
        String role  = json.optString("role",  null);

        JSONObject result = adminUsersService.adminUpdateUser(id, name, email, role, userId);
        return build(result);
    }

    @DELETE
    @Path("deleteUser/{id}")
    public Response deleteUser(@PathParam("id") int id,
                                @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject result = adminUsersService.adminDeleteUser(id, userId);
        return build(result);
    }

    private Response build(JSONObject r) {
        return Response.status(r.getInt("statusCode"))
                .entity(r.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    private Response unauth() {
        JSONObject err = new JSONObject();
        err.put("statusCode", 401);
        err.put("message", "Bejelentkezés szükséges");
        return Response.status(401).entity(err.toString()).build();
    }
}
