package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.CourseSessionsService;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("Sessions")
@Produces(MediaType.APPLICATION_JSON)
public class CourseSessionsController {

    @Inject
    private CourseSessionsService sessionsService;

    @GET
    @Path("getAllSessions")
    public Response getAllSessions(@Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        return build(sessionsService.getAllSessions());
    }

    @GET
    @Path("getByCourse/{courseId}")
    public Response getByCourse(@PathParam("courseId") int courseId) {
        return build(sessionsService.getSessionsByCourse(courseId));
    }

    @POST
    @Path("createSession")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSession(String body, @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject json = new JSONObject(body);
        return build(sessionsService.createSession(
                json.getInt("courseId"),
                json.optString("startAt", null),
                json.optString("endAt", null)));
    }

    @PUT
    @Path("updateSession/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateSession(@PathParam("id") int id,
                                  String body,
                                  @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject json = new JSONObject(body);
        Integer courseId = json.has("course_id") ? json.getInt("course_id") : null;
        return build(sessionsService.updateSession(
                id, courseId,
                json.optString("start_at", null),
                json.optString("end_at", null)));
    }

    @DELETE
    @Path("deleteSession/{id}")
    public Response deleteSession(@PathParam("id") int id,
                                  @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        return build(sessionsService.deleteSession(id));
    }

    private Response build(JSONObject r) {
        return Response.status(r.getInt("statusCode")).entity(r.toString()).build();
    }
    private Response unauth() {
        return Response.status(401)
                .entity("{\"statusCode\":401,\"message\":\"Bejelentkezés szükséges\"}")
                .build();
    }
}
