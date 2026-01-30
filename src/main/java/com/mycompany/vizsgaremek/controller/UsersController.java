package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.UsersService;
import org.json.JSONObject;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.servlet.http.HttpServletRequest;

@Path("Users")
public class UsersController {

    @Inject
    private UsersService usersService;

    @POST
    @Path("createUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(String body) {
        JSONObject obj = new JSONObject(body);

        String name = obj.getString("name");
        String email = obj.getString("email");
        String password = obj.getString("password");
        String role = obj.has("role") ? obj.getString("role") : null;

        JSONObject result = usersService.createUser(name, email, password, role);

        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("/updateUser/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateUser(@PathParam("id") int id, String body) {
        JSONObject json = new JSONObject(body);

        String name = json.optString("name", null);
        String email = json.optString("email", null);
        String role = json.optString("role", null);

        return usersService.updateUser(id, name, email, role).toString();
    }

    @DELETE
    @Path("deleteUser/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("userId") int userId) {
        JSONObject result = usersService.deleteUser(userId);
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        JSONObject result = usersService.getAllUsers();
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getUserById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") int id) {
        JSONObject result = usersService.getUserById(id);
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String body) {
        JSONObject obj = new JSONObject(body);

        String email = obj.getString("email");
        String password = obj.getString("password");

        JSONObject result = usersService.login(email, password);

        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


    
    
    
    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMe(@Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Response.status(401)
                    .entity("{\"message\":\"Unauthorized - missing userId in context\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject result = usersService.getCurrentUser(userId);
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("updateProfile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProfile(
            String body,
            @Context HttpServletRequest request
    ) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Response.status(401)
                    .entity("{\"message\":\"Unauthorized - missing userId in context\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject json = new JSONObject(body);

        String name            = json.optString("name", null);
        String email           = json.optString("email", null);
        String currentPassword = json.optString("currentPassword", null);
        String newPassword     = json.optString("newPassword", null);

        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            return Response.status(400)
                    .entity("{\"message\":\"Jelenlegi jelszó kötelező a módosításhoz\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject result = usersService.updateProfile(
                userId, name, email, currentPassword, newPassword);

        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("deleteMe")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMe(@Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Response.status(401)
                    .entity("{\"message\":\"Unauthorized - missing userId in context\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject result = usersService.deleteCurrentUser(userId);
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}