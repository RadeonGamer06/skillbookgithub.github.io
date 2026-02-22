package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.EmailService;
import org.json.JSONObject;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("Contact")
public class ContactController {

    @Inject
    private EmailService emailService;

    @POST
    @Path("sendMessage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendContactMessage(String body) {
        JSONObject resp = new JSONObject();
        
        try {
            JSONObject obj = new JSONObject(body);

            String name = obj.optString("name", "").trim();
            String email = obj.optString("email", "").trim();
            String subject = obj.optString("subject", "").trim();
            String message = obj.optString("message", "").trim();

            if (name.isEmpty() || email.isEmpty() || subject.isEmpty() || message.isEmpty()) {
                resp.put("statusCode", 400);
                resp.put("message", "Minden mező kitöltése kötelező!");
                return Response.status(400)
                        .entity(resp.toString())
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                resp.put("statusCode", 400);
                resp.put("message", "Érvénytelen email cím!");
                return Response.status(400)
                        .entity(resp.toString())
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            boolean emailSent = emailService.sendContactEmail(name, email, subject, message);

            if (emailSent) {
                resp.put("status", "MessageSent");
                resp.put("statusCode", 200);
                resp.put("message", "Üzeneted sikeresen elküldve! Hamarosan válaszolunk.");
                
                return Response.status(200)
                        .entity(resp.toString())
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            } else {
                resp.put("status", "EmailError");
                resp.put("statusCode", 500);
                resp.put("message", "Hiba történt az email küldése során. Próbáld újra később!");
                
                return Response.status(500)
                        .entity(resp.toString())
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

        } catch (Exception e) {
            System.err.println("Kapcsolati űrlap hiba: " + e.getMessage());
            e.printStackTrace();
            
            resp.put("status", "Error");
            resp.put("statusCode", 500);
            resp.put("message", "Szerverhiba történt. Kérlek próbáld újra!");
            
            return Response.status(500)
                    .entity(resp.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
