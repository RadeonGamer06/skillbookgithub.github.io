package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.UsersService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.nio.file.Files;

@Path("Uploads")
public class UploadsController {

    @GET
    @Path("profile_pictures/{filename}")
    @Produces("image/*")
    public Response getProfilePicture(@PathParam("filename") String filename) {
        try {
            System.out.println("📷 PROFILKÉP LEKÉRÉS: " + filename);

            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                System.err.println("❌ BIZTONSÁGI HIBA: Érvénytelen fájlnév");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\":\"Érvénytelen fájlnév\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            File imageFile = UsersService.getProfilePictureFile(filename);

            if (!imageFile.exists()) {
                System.err.println("❌ Fájl nem található: " + imageFile.getAbsolutePath());
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\":\"Kép nem található\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

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

    @GET
    @Path("course_headers/{filename}")
    @Produces("image/*")
    public Response getCourseHeader(@PathParam("filename") String filename) {
        try {
            System.out.println("🎨 TANFOLYAM FEJLÉC KÉP LEKÉRÉS: " + filename);

            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                System.err.println("❌ BIZTONSÁGI HIBA: Érvénytelen fájlnév");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\":\"Érvénytelen fájlnév\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            String uploadsDir = "C:/Users/Bagoly Donát/Desktop/SkillBook/server/wildfly-preview-26.1.1.Final/standalone/data/uploads/course_headers/";
            File imageFile = new File(uploadsDir + filename);

            if (!imageFile.exists()) {
                System.err.println("❌ Fájl nem található: " + imageFile.getAbsolutePath());
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\":\"Kép nem található\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

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

            byte[] imageData = Files.readAllBytes(imageFile.toPath());

            System.out.println("✅ Tanfolyam fejléc kép kiszolgálva: " + filename + " (" + imageData.length + " byte)");

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