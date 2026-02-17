package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.Categories;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * Admin Categories végpontok — kiegészíti a meglévő CategoriesController-t.
 * POST   /api/Categories/createCategory
 * PUT    /api/Categories/updateCategory/{id}
 * DELETE /api/Categories/deleteCategory/{id}
 */
@Path("Categories")
@Produces(MediaType.APPLICATION_JSON)
public class AdminCategoriesController {

    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    // POST /api/Categories/createCategory
    // Body: { "name": "Python", "slug": "python" }
    @POST
    @Path("createCategory")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCategory(String body, @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject resp = new JSONObject();
        try {
            JSONObject json = new JSONObject(body);
            String name = json.optString("name", "").trim();
            String slug = json.optString("slug", "").trim();
            if (name.isEmpty() || slug.isEmpty()) {
                resp.put("statusCode", 400);
                resp.put("message", "Név és slug kötelező");
                return Response.status(400).entity(resp.toString()).build();
            }
            Categories c = new Categories();
            c.setName(name);
            c.setSlug(slug);
            em.persist(c);
            resp.put("statusCode", 201);
            resp.put("status", "CategoryCreated");
            resp.put("message", "Kategória létrehozva");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return Response.status(resp.getInt("statusCode")).entity(resp.toString()).build();
    }

    // PUT /api/Categories/updateCategory/{id}
    // Body: { "name": "...", "slug": "..." }
    @PUT
    @Path("updateCategory/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCategory(@PathParam("id") int id,
                                    String body,
                                    @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject resp = new JSONObject();
        try {
            Categories c = em.find(Categories.class, id);
            if (c == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Kategória nem található");
                return Response.status(404).entity(resp.toString()).build();
            }
            JSONObject json = new JSONObject(body);
            String name = json.optString("name", null);
            String slug = json.optString("slug", null);
            if (name != null && !name.trim().isEmpty()) c.setName(name.trim());
            if (slug != null && !slug.trim().isEmpty()) c.setSlug(slug.trim());
            em.merge(c);
            resp.put("statusCode", 200);
            resp.put("status", "CategoryUpdated");
            resp.put("message", "Kategória frissítve");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return Response.status(resp.getInt("statusCode")).entity(resp.toString()).build();
    }

    // DELETE /api/Categories/deleteCategory/{id}
    @DELETE
    @Path("deleteCategory/{id}")
    public Response deleteCategory(@PathParam("id") int id,
                                    @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject resp = new JSONObject();
        try {
            Categories c = em.find(Categories.class, id);
            if (c == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Kategória nem található");
                return Response.status(404).entity(resp.toString()).build();
            }
            // Null-ra állítjuk a hivatkozó tanfolyamoknál
            em.createNativeQuery("UPDATE courses SET category_id = NULL WHERE category_id = :cid")
              .setParameter("cid", id).executeUpdate();
            em.remove(em.contains(c) ? c : em.merge(c));
            resp.put("statusCode", 200);
            resp.put("status", "CategoryDeleted");
            resp.put("message", "Kategória törölve");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return Response.status(resp.getInt("statusCode")).entity(resp.toString()).build();
    }

    private Response unauth() {
        return Response.status(401)
                .entity("{\"statusCode\":401,\"message\":\"Bejelentkezés szükséges\"}")
                .build();
    }
}
