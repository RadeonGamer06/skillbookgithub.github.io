package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.CourseMaterialsService;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("Materials")
@Produces(MediaType.APPLICATION_JSON)
public class CourseMaterialsController {

    @Inject
    private CourseMaterialsService materialsService;

    // GET /api/Materials/getAllMaterials  — admin
    @GET
    @Path("getAllMaterials")
    public Response getAllMaterials(@Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        return build(materialsService.getAllMaterials());
    }

    // GET /api/Materials/getByCourse/{courseId}
    @GET
    @Path("getByCourse/{courseId}")
    public Response getByCourse(@PathParam("courseId") int courseId) {
        return build(materialsService.getMaterialsByCourse(courseId));
    }

    // DELETE /api/Materials/deleteMaterial/{id}
    @DELETE
    @Path("deleteMaterial/{id}")
    public Response deleteMaterial(@PathParam("id") int id,
                                   @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        return build(materialsService.deleteMaterial(id));
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
