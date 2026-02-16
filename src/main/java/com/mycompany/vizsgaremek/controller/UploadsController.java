package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.UsersService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.nio.file.Files;

/**
 * ✅ ÚJ CONTROLLER - Képek kiszolgálására
 * Ez szolgálja ki a feltöltött profilképeket statikus fájlokként
 */
@Path("Uploads")
public class UploadsController {

    /**
     * Profilkép lekérése fájlnév alapján
     * GET /api/Uploads/profile_pictures/{filename}
     * 
     * Példa: http://127.0.0.1:8080/SkillBook/api/Uploads/profile_pictures/profile_80_abc123.jpg
     */
    @GET
    @Path("profile_pictures/{filename}")
    @Produces("image/*")  // Bármilyen képtípus
    public Response getProfilePicture(@PathParam("filename") String filename) {
        try {
            System.out.println("📷 PROFILKÉP LEKÉRÉS: " + filename);

            // Biztonság: ne engedjük a "../" karaktereket (path traversal attack védelem)
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                System.err.println("❌ BIZTONSÁGI HIBA: Érvénytelen fájlnév");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\":\"Érvénytelen fájlnév\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            // Fájl lekérése a lemezről
            File imageFile = UsersService.getProfilePictureFile(filename);

            if (!imageFile.exists()) {
                System.err.println("❌ Fájl nem található: " + imageFile.getAbsolutePath());
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\":\"Kép nem található\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            // MIME type meghatározása fájlkiterjesztés alapján
            String contentType = "image/jpeg";  // alapértelmezett
            String lowerFilename = filename.toLowerCase();
            
            if (lowerFilename.endsWith(".png")) {
                contentType = "image/png";
            } else if (lowerFilename.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (lowerFilename.endsWith(".webp")) {
                contentType = "image/webp";
            } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            }

            // Fájl beolvasása és visszaküldése
            byte[] imageData = Files.readAllBytes(imageFile.toPath());

            System.out.println("✅ Kép kiszolgálva: " + filename + " (" + imageData.length + " byte)");

            return Response.ok(imageData)
                    .type(contentType)
                    .header("Cache-Control", "public, max-age=86400")  // 24 óra cache
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Hiba a kép kiszolgálása során: " + e.getMessage());
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"Szerver hiba: " + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}