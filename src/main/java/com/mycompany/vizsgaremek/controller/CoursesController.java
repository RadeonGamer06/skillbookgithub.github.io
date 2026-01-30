package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.CoursesService;
import org.json.JSONObject;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.servlet.http.HttpServletRequest;

@Path("Courses")
public class CoursesController {

    @Inject
    private CoursesService coursesService;

    @POST
    @Path("createCourse")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCourse(String body, @Context HttpServletRequest request) {
        // Get instructor ID from JWT token
        Integer instructorId = (Integer) request.getAttribute("userId");
        if (instructorId == null) {
            return Response.status(401)
                    .entity("{\"message\":\"Unauthorized - missing userId in context\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject obj = new JSONObject(body);

        String title = obj.getString("title");
        String description = obj.optString("description", null);
        Integer price = obj.getInt("price");
        String category = obj.optString("category", null);
        Integer maxParticipants = obj.optInt("max_participants", 20);
        
        // Date fields (for now, we'll store them separately or just ignore)
        String startDate = obj.optString("start_date", null);
        String endDate = obj.optString("end_date", null);

        JSONObject result = coursesService.createCourse(
            title, description, price, instructorId, category, maxParticipants, startDate, endDate
        );

        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllCourses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCourses() {
        JSONObject result = coursesService.getAllCourses();
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getCourseById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourseById(@PathParam("id") int id) {
        JSONObject result = coursesService.getCourseById(id);
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("updateCourse/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCourse(@PathParam("id") int id, String body, @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Response.status(401)
                    .entity("{\"message\":\"Unauthorized\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject obj = new JSONObject(body);
        
        String title = obj.optString("title", null);
        String description = obj.optString("description", null);
        Integer price = obj.has("price") ? obj.getInt("price") : null;

        JSONObject result = coursesService.updateCourse(id, title, description, price, userId);
        
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("deleteCourse/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCourse(@PathParam("id") int id, @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Response.status(401)
                    .entity("{\"message\":\"Unauthorized\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject result = coursesService.deleteCourse(id, userId);
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}