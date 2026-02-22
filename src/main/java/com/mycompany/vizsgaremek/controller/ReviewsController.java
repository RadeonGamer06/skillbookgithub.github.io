package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.ReviewsService;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("Reviews")
public class ReviewsController {

    @Inject
    private ReviewsService reviewsService;

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReview(String body, @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Response.status(401)
                    .entity("{\"message\":\"Unauthorized - missing userId\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject json = new JSONObject(body);
        
        Integer courseId = json.optInt("course_id", -1);
        Integer rating = json.optInt("rating", -1);
        String comment = json.optString("comment", null);

        if (courseId == -1) {
            return Response.status(400)
                    .entity("{\"message\":\"course_id kötelező\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (rating < 1 || rating > 5) {
            return Response.status(400)
                    .entity("{\"message\":\"rating 1 és 5 között kell legyen\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject result = reviewsService.createReview(userId, courseId, rating, comment);

        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getByCourse/{courseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReviewsByCourse(@PathParam("courseId") int courseId) {
        JSONObject result = reviewsService.getReviewsByCourse(courseId);

        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getByUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReviewsByUser(@Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Response.status(401)
                    .entity("{\"message\":\"Unauthorized\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject result = reviewsService.getReviewsByUser(userId);

        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("delete/{reviewId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteReview(@PathParam("reviewId") int reviewId, 
                                 @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Response.status(401)
                    .entity("{\"message\":\"Unauthorized\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject result = reviewsService.deleteReview(reviewId, userId);

        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
