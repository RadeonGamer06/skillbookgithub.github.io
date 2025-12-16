package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.UsersService;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

@Path("Users")
public class UsersController {

    @Inject   // ← CDI injektálja a UsersService EJB-t
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
@Path("updateUser")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response updateUser(String body) {
    JSONObject obj = new JSONObject(body);
    int userId = obj.getInt("userId");
    String name = obj.has("name") ? obj.getString("name") : null;
    String email = obj.has("email") ? obj.getString("email") : null;
    String role = obj.has("role") ? obj.getString("role") : null;
    
    JSONObject result = usersService.updateUser(userId, name, email, role);
    return Response.status(result.getInt("statusCode"))
            .entity(result.toString())
            .type(MediaType.APPLICATION_JSON)
            .build();
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
    
    
    //Még készül.
//deleteuser eljaras de ai-t kerdezd meg h adatbazisban hard delete kell neked
    
//getAllUsers- admineljárás
    
//
}