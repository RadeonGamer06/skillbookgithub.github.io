package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.EnrollmentsService;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("Enrollments")
@Produces(MediaType.APPLICATION_JSON)
public class EnrollmentsController {

    @Inject
    private EnrollmentsService enrollmentsService;

    // GET /api/Enrollments/getAllEnrollments  — admin
    @GET
    @Path("getAllEnrollments")
    public Response getAllEnrollments(@Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject result = enrollmentsService.getAllEnrollments();
        return build(result);
    }

    // GET /api/Enrollments/getMyEnrollments
    @GET
    @Path("getMyEnrollments")
    public Response getMyEnrollments(@Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject result = enrollmentsService.getEnrollmentsByUser(userId);
        return build(result);
    }

    // POST /api/Enrollments/enroll
    // Body: { "courseId": 5, "sessionId": null }
    @POST
    @Path("enroll")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response enroll(String body, @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject json = new JSONObject(body);
        Integer courseId  = json.getInt("courseId");
        Integer sessionId = json.has("sessionId") && !json.isNull("sessionId")
                ? json.getInt("sessionId") : null;
        JSONObject result = enrollmentsService.createEnrollment(userId, courseId, sessionId);
        return build(result);
    }

    // PUT /api/Enrollments/updateEnrollment/{id}
    // Body: { "status": "canceled" }
    @PUT
    @Path("updateEnrollment/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateEnrollment(@PathParam("id") int id,
                                     String body,
                                     @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject json   = new JSONObject(body);
        String status     = json.optString("status", null);
        JSONObject result = enrollmentsService.updateEnrollment(id, status);
        return build(result);
    }

    // DELETE /api/Enrollments/deleteEnrollment/{id}
    @DELETE
    @Path("deleteEnrollment/{id}")
    public Response deleteEnrollment(@PathParam("id") int id,
                                     @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject result = enrollmentsService.deleteEnrollment(id, userId);
        return build(result);
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
