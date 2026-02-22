package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.UsersService;
import org.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.io.InputStream;
import java.util.List;

@Path("Users")
public class UsersController {

    @Inject
    private UsersService usersService;

    @POST
    @Path("createUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(String body) {
        JSONObject json = new JSONObject(body);
        
        String name = json.getString("name");
        String email = json.getString("email");
        String password = json.getString("password");
        String role = json.optString("role", "student");

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

    @POST
    @Path("forgotPassword")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forgotPassword(String body) {
        JSONObject json;
        try {
            json = new JSONObject(body);
        } catch (Exception e) {
            JSONObject err = new JSONObject();
            err.put("statusCode", 400);
            err.put("message", "Érvénytelen JSON formátum");
            return Response.status(400).entity(err.toString()).build();
        }

        String email = json.optString("email", "").trim();
        if (email.isEmpty()) {
            JSONObject err = new JSONObject();
            err.put("statusCode", 400);
            err.put("message", "Az email mező kötelező");
            return Response.status(400).entity(err.toString()).build();
        }

        JSONObject result = usersService.forgotPassword(email);
        return Response.status(result.optInt("statusCode", 500))
                       .entity(result.toString())
                       .build();
    }
    
    @POST
@Path("updateProfilePicture")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public Response updateProfilePicture(@Context HttpServletRequest request) {
    Integer userId = (Integer) request.getAttribute("userId");
    if (userId == null) {
        return Response.status(401)
                .entity("{\"message\":\"Unauthorized - missing userId in context\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    try {
        if (!ServletFileUpload.isMultipartContent(request)) {
            return Response.status(400)
                    .entity("{\"message\":\"A kérés nem multipart formátumú\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(50L * 1024 * 1024);      //max 50 Mb
        upload.setFileSizeMax(50L * 1024 * 1024);

        List<FileItem> items = upload.parseRequest(request);

        InputStream fileInputStream = null;
        String fileName = null;

        for (FileItem item : items) {
            if (!item.isFormField() && "profilePicture".equals(item.getFieldName())) {
                fileInputStream = item.getInputStream();
                fileName = item.getName();
                break;
            }
        }

        if (fileInputStream == null || fileName == null) {
            return Response.status(400)
                    .entity("{\"message\":\"Nincs feltöltött fájl\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject result = usersService.updateProfilePicture(userId, fileInputStream, fileName);

        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();

    } catch (Exception e) {
        e.printStackTrace();
        return Response.status(500)
                .entity("{\"message\":\"Fájl feldolgozási hiba: " + e.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
}
    
