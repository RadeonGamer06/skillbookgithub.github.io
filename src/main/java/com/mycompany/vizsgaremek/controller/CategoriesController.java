package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.CategoriesService;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("Categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoriesController {

    @Inject
    private CategoriesService categoriesService;

    @GET
    @Path("getAllCategories")
    public Response getAllCategories() {
        JSONObject result = (JSONObject) categoriesService.getAllCategories();

        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
