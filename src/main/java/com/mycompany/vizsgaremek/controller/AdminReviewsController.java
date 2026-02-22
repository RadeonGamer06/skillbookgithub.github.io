package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.ReviewsService;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("Reviews")
@Produces(MediaType.APPLICATION_JSON)
public class AdminReviewsController {

    @Inject
    private ReviewsService reviewsService;

    @GET
    @Path("getAllReviews")
    public Response getAllReviews(@Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject result = reviewsService.getAllReviews();
        return build(result);
    }
    
    @PUT
    @Path("updateReview/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateReview(@PathParam("id") int id,
                                  String body,
                                  @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject json    = new JSONObject(body);
        Integer rating     = json.has("rating") ? json.getInt("rating") : null;
        String  comment    = json.optString("comment", null);
        JSONObject result  = reviewsService.adminUpdateReview(id, rating, comment, userId);
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
