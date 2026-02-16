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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Path("/chat")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MessagesController {

    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    @Inject
    private MessagesService messagesService;

    /**
     * 1. FELHASZNÁLÓK / BESZÉLGETÉSEK LISTÁZÁSA
     * Megkeresi az összes felhasználót, akivel a bejelentkezett user beszélgethet.
     */
    @GET
    @Path("/users")
    public Response getChatUsers(@Context HttpServletRequest request) {
        try {
            Integer myId = (Integer) request.getAttribute("userId");
            if (myId == null) {
                return Response.status(401).entity("{\"error\":\"Bejelentkezés szükséges\"}").build();
            }

            // Összes többi felhasználó lekérése
            List<Users> users = em.createQuery("SELECT u FROM Users u WHERE u.id != :id", Users.class)
                    .setParameter("id", myId)
                    .getResultList();

            List<ConversationDto> resultList = new ArrayList<>();

            for (Users partner : users) {
                // Lekérjük a chat előzményt a partnerrel az utolsó üzenet miatt
                List<Messages> chat = messagesService.getChat(myId, partner.getId());
                Messages last = chat.isEmpty() ? null : chat.get(chat.size() - 1);

                String preview = (last != null) 
                    ? (last.getContent().length() > 60 ? last.getContent().substring(0, 57) + "..." : last.getContent())
                    : "Nincs üzenet";

                resultList.add(new ConversationDto(
                        partner.getId(),
                        partner.getName() != null ? partner.getName() : partner.getEmail(),
                        partner.getRole(),
                        last != null ? last.getSentAt().toString() : null,
                        preview
                ));
            }

            // Rendezés: akinél van frissebb üzenet, az kerül előre
            resultList.sort((a, b) -> {
                if (a.lastMessageAt == null) return 1;
                if (b.lastMessageAt == null) return -1;
                return b.lastMessageAt.compareTo(a.lastMessageAt);
            });

            return Response.ok(new JSONArray(resultList).toString()).build();

        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * 2. KONKRÉT CHAT TÖRTÉNET LEKÉRÉSE
     */
    @GET
    @Path("/getMessages")
    public Response getMessages(@Context HttpServletRequest request, @QueryParam("partnerId") int partnerId) {
        try {
            Integer myId = (Integer) request.getAttribute("userId");
            if (myId == null) return Response.status(401).build();

            List<Messages> messages = messagesService.getChat(myId, partnerId);
            JSONArray arr = new JSONArray();

            for (Messages m : messages) {
                JSONObject obj = new JSONObject();
                obj.put("id", m.getId());
                obj.put("content", m.getContent());
                obj.put("sentAt", m.getSentAt().toString());
                obj.put("isMe", m.getSender().getId().equals(myId));
                arr.put(obj);
            }
            return Response.ok(arr.toString()).build();
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"Hiba a lekérés során\"}").build();
        }
    }

    /**
     * 3. ÜZENET KÜLDÉSE
     */
    @POST
    @Path("/send")
    @Transactional
    public Response sendMessage(@Context HttpServletRequest request, String body) {
        try {
            Integer myId = (Integer) request.getAttribute("userId");
            if (myId == null) return Response.status(401).build();

            JSONObject json = new JSONObject(body);
            int receiverId = json.getInt("receiverId");
            String content = json.getString("content");

            Users sender = em.find(Users.class, myId);
            Users receiver = em.find(Users.class, receiverId);

            if (receiver == null) {
                return Response.status(404).entity("{\"error\":\"Címzett nem található\"}").build();
            }

            messagesService.sendMessage(sender, receiver, content);

            return Response.ok("{\"message\":\"Sikeres küldés\"}").build();
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"Szerver hiba: " + e.getMessage() + "\"}").build();
        }
    }

    // Segédosztály a lista formázásához
    public static class ConversationDto {
        public Integer id;
        public String name;
        public String role;
        public String lastMessageAt;
        public String lastMessagePreview;

        public ConversationDto(Integer id, String name, String role, String lastAt, String preview) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.lastMessageAt = lastAt;
            this.lastMessagePreview = preview;
        }
    }
}