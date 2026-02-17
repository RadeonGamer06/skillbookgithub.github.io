package com.mycompany.vizsgaremek.controller;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

/**
 * Admin Messages végpontok.
 * GET    /api/Messages/getAllMessages    → összes üzenet (admin)
 * DELETE /api/Messages/deleteMessage/{id}
 */
@Path("Messages")
@Produces(MediaType.APPLICATION_JSON)
public class AdminMessagesController {

    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    // GET /api/Messages/getAllMessages
    @GET
    @Path("getAllMessages")
    public Response getAllMessages(@Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject resp = new JSONObject();
        JSONArray arr   = new JSONArray();
        try {
            Query q = em.createNativeQuery(
                "SELECT m.id, m.sender_id, m.receiver_id, m.content, m.sentAt, " +
                "s.name AS sender_name, rv.name AS receiver_name " +
                "FROM messages m " +
                "LEFT JOIN users s  ON m.sender_id   = s.id " +
                "LEFT JOIN users rv ON m.receiver_id = rv.id " +
                "ORDER BY m.sentAt DESC");
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            for (Object[] r : rows) {
                JSONObject o = new JSONObject();
                o.put("id",            r[0]);
                o.put("sender_id",     r[1]);
                o.put("receiver_id",   r[2]);
                o.put("content",       r[3] != null ? r[3] : "");
                o.put("sentAt",        r[4] != null ? r[4].toString() : "");
                o.put("sender_name",   r[5] != null ? r[5] : "–");
                o.put("receiver_name", r[6] != null ? r[6] : "–");
                arr.put(o);
            }
            resp.put("statusCode", 200);
            resp.put("data", arr);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return Response.status(resp.getInt("statusCode")).entity(resp.toString()).build();
    }

    // DELETE /api/Messages/deleteMessage/{id}
    @DELETE
    @Path("deleteMessage/{id}")
    public Response deleteMessage(@PathParam("id") int id,
                                   @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject resp = new JSONObject();
        try {
            int affected = em.createNativeQuery("DELETE FROM messages WHERE id = :id")
                    .setParameter("id", id).executeUpdate();
            if (affected == 0) {
                resp.put("statusCode", 404);
                resp.put("message", "Üzenet nem található");
            } else {
                resp.put("statusCode", 200);
                resp.put("status", "MessageDeleted");
                resp.put("message", "Üzenet törölve");
            }
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
