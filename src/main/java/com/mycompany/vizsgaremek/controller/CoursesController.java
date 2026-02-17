package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.CoursesService;
import org.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.InputStream;
import java.util.List;

@Path("Courses")
public class CoursesController {

    @Inject
    private CoursesService coursesService;

    @POST
    @Path("createCourse")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCourse(String body, @Context HttpServletRequest request) {
        Integer instructorId = (Integer) request.getAttribute("userId");
        if (instructorId == null) {
            return Response.status(401)
                    .entity("{\"message\":\"Unauthorized - missing userId in context\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject obj = new JSONObject(body);

        String title = obj.getString("title");
        String description = obj.optString("description", null);
        Integer price = obj.getInt("price");

        Integer categoryId = obj.has("categoryId") ? obj.getInt("categoryId") : null;

        Integer maxParticipants = obj.optInt("max_participants", 20);

        String startDate = obj.optString("start_date", null);
        String endDate = obj.optString("end_date", null);

        JSONObject result = coursesService.createCourse(
                title, description, price, instructorId, categoryId, maxParticipants, startDate, endDate
        );

        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllCourses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCourses() {
        JSONObject result = coursesService.getAllCourses();
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getCourseById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourseById(@PathParam("id") int id) {
        JSONObject result = coursesService.getCourseById(id);
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("updateCourse/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCourse(@PathParam("id") int id, String body, @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Response.status(401)
                    .entity("{\"message\":\"Unauthorized\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject obj = new JSONObject(body);

        String title = obj.optString("title", null);
        String description = obj.optString("description", null);
        Integer price = obj.has("price") ? obj.getInt("price") : null;
        String startDate = obj.optString("start_date", null);
        String endDate = obj.optString("end_date", null);

        JSONObject result = coursesService.updateCourse(id, title, description, price, startDate, endDate, userId);

        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("deleteCourse/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCourse(@PathParam("id") int id, @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Response.status(401)
                    .entity("{\"message\":\"Unauthorized\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject result = coursesService.deleteCourse(id, userId);
        return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("uploadMaterial/{courseId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadMaterial(
            @PathParam("courseId") int courseId,
            @Context HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            JSONObject err = new JSONObject();
            err.put("statusCode", 401);
            err.put("message", "Unauthorized - missing userId");
            return Response.status(401).entity(err.toString()).build();
        }

        try {
            // Ellenőrizzük, hogy multipart request-e
            if (!ServletFileUpload.isMultipartContent(request)) {
                JSONObject err = new JSONObject();
                err.put("statusCode", 400);
                err.put("message", "A kérés nem multipart formátumú");
                return Response.status(400).entity(err.toString()).build();
            }

            // FileUpload konfiguráció
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            
            List<FileItem> items = upload.parseRequest(request);
            
            String title = null;
            InputStream fileStream = null;
            String fileName = null;
            
            // Feldolgozzuk a form field-eket
            for (FileItem item : items) {
                if (item.isFormField()) {
                    if ("title".equals(item.getFieldName())) {
                        title = item.getString("UTF-8");
                    }
                } else {
                    if ("file".equals(item.getFieldName())) {
                        fileStream = item.getInputStream();
                        fileName = item.getName();
                    }
                }
            }

            if (title == null || title.trim().isEmpty()) {
                JSONObject err = new JSONObject();
                err.put("statusCode", 400);
                err.put("message", "Cím kötelező");
                return Response.status(400).entity(err.toString()).build();
            }

            if (fileStream == null) {
                JSONObject err = new JSONObject();
                err.put("statusCode", 400);
                err.put("message", "Nincs feltöltött fájl");
                return Response.status(400).entity(err.toString()).build();
            }

            JSONObject result = coursesService.uploadMaterial(courseId, userId, title, fileStream, fileName);

            return Response.status(result.optInt("statusCode", 500))
                    .entity(result.toString())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject err = new JSONObject();
            err.put("statusCode", 500);
            err.put("message", "Fájl feldolgozási hiba: " + e.getMessage());
            return Response.status(500).entity(err.toString()).build();
        }
    }

    @POST
    @Path("uploadHeaderImage/{courseId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadHeaderImage(
            @PathParam("courseId") int courseId,
            @Context HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            JSONObject err = new JSONObject();
            err.put("statusCode", 401);
            err.put("message", "Unauthorized - missing userId");
            return Response.status(401).entity(err.toString()).build();
        }

        try {
            if (!ServletFileUpload.isMultipartContent(request)) {
                JSONObject err = new JSONObject();
                err.put("statusCode", 400);
                err.put("message", "A kérés nem multipart formátumú");
                return Response.status(400).entity(err.toString()).build();
            }

            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(10L * 1024 * 1024);      // max 10 MB
            upload.setFileSizeMax(10L * 1024 * 1024);  // max 10 MB
            
            List<FileItem> items = upload.parseRequest(request);
            
            InputStream fileStream = null;
            String fileName = null;
            
            for (FileItem item : items) {
                if (!item.isFormField() && "headerImage".equals(item.getFieldName())) {
                    fileStream = item.getInputStream();
                    fileName = item.getName();
                    break;
                }
            }

            if (fileStream == null) {
                JSONObject err = new JSONObject();
                err.put("statusCode", 400);
                err.put("message", "Nincs feltöltött fájl");
                return Response.status(400).entity(err.toString()).build();
            }

            JSONObject result = coursesService.uploadHeaderImage(courseId, userId, fileStream, fileName);

            return Response.status(result.optInt("statusCode", 500))
                    .entity(result.toString())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject err = new JSONObject();
            err.put("statusCode", 500);
            err.put("message", "Fájl feldolgozási hiba: " + e.getMessage());
            return Response.status(500).entity(err.toString()).build();
        }
    }
}