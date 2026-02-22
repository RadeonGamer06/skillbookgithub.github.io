package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.Messages;
import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.service.MessagesService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("chat")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MessagesController {

    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    @Inject
    private MessagesService messagesService;

    @GET
    @Path("users")
    public Response getChatUsers(@Context HttpServletRequest request) {
        try {
            Integer myId = (Integer) request.getAttribute("userId");
            if (myId == null) {
                return Response.status(401)
                        .entity("{\"error\":\"Bejelentkezés szükséges\"}")
                        .build();
            }

            List<Users> users = em.createQuery(
                    "SELECT u FROM Users u WHERE u.id != :id ORDER BY u.name ASC",
                    Users.class)
                    .setParameter("id", myId)
                    .getResultList();

            JSONArray arr = new JSONArray();
            for (Users partner : users) {
                List<Messages> chat = messagesService.getChat(myId, partner.getId());
                Messages last = chat.isEmpty() ? null : chat.get(chat.size() - 1);

                JSONObject obj = new JSONObject();
                obj.put("id",   partner.getId());
                obj.put("name", partner.getName() != null ? partner.getName() : partner.getEmail());
                obj.put("email", partner.getEmail() != null ? partner.getEmail() : "");
                obj.put("role", partner.getRole() != null ? partner.getRole() : "");
                obj.put("profilePicture",
                        partner.getProfilePicture() != null ? partner.getProfilePicture() : "");
                obj.put("hasConversation", last != null);
                obj.put("lastMessageAt",
                        last != null ? last.getSentAt().toString() : JSONObject.NULL);
                obj.put("lastMessagePreview",
                        last != null
                                ? (last.getContent().length() > 55
                                        ? last.getContent().substring(0, 52) + "..."
                                        : last.getContent())
                                : "");
                arr.put(obj);
            }

            JSONObject resp = new JSONObject();
            resp.put("statusCode", 200);
            resp.put("data", arr);
            return Response.ok(resp.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject err = new JSONObject();
            err.put("statusCode", 500);
            err.put("error", e.getMessage());
            return Response.status(500).entity(err.toString()).build();
        }
    }

    @GET
    @Path("messages")
    public Response getMessages(
            @Context HttpServletRequest request,
            @QueryParam("partnerId") int partnerId) {
        try {
            Integer myId = (Integer) request.getAttribute("userId");
            if (myId == null) {
                return Response.status(401)
                        .entity("{\"statusCode\":401,\"error\":\"Bejelentkezés szükséges\"}")
                        .build();
            }

            List<Messages> messages = messagesService.getChat(myId, partnerId);
            JSONArray arr = new JSONArray();

            for (Messages m : messages) {
                JSONObject obj = new JSONObject();
                obj.put("id",       m.getId());
                obj.put("content",  m.getContent());
                obj.put("sentAt",   m.getSentAt().toString());
                obj.put("senderId", m.getSender().getId());
                obj.put("isMe",     m.getSender().getId() == myId);
                arr.put(obj);
            }

            JSONObject resp = new JSONObject();
            resp.put("statusCode", 200);
            resp.put("data", arr);
            return Response.ok(resp.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject err = new JSONObject();
            err.put("statusCode", 500);
            err.put("error", "Hiba a lekérés során: " + e.getMessage());
            return Response.status(500).entity(err.toString()).build();
        }
    }

    @POST
    @Path("send")
    @Transactional
    public Response sendMessage(
            @Context HttpServletRequest request,
            String body) {
        try {
            Integer myId = (Integer) request.getAttribute("userId");
            if (myId == null) {
                return Response.status(401)
                        .entity("{\"statusCode\":401,\"error\":\"Bejelentkezés szükséges\"}")
                        .build();
            }

            JSONObject json = new JSONObject(body);
            int receiverId   = json.getInt("receiverId");
            String content   = json.getString("content").trim();

            if (content.isEmpty()) {
                return Response.status(400)
                        .entity("{\"statusCode\":400,\"error\":\"Az üzenet nem lehet üres\"}")
                        .build();
            }

            Users sender   = em.find(Users.class, myId);
            Users receiver = em.find(Users.class, receiverId);

            if (receiver == null) {
                return Response.status(404)
                        .entity("{\"statusCode\":404,\"error\":\"Címzett nem található\"}")
                        .build();
            }

            messagesService.sendMessage(sender, receiver, content);

            JSONObject resp = new JSONObject();
            resp.put("statusCode", 201);
            resp.put("message", "Üzenet sikeresen elküldve");
            return Response.status(201).entity(resp.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject err = new JSONObject();
            err.put("statusCode", 500);
            err.put("error", "Szerver hiba: " + e.getMessage());
            return Response.status(500).entity(err.toString()).build();
        }
    }
}