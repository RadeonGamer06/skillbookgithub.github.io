package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.QuizzesService;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("Quizzes")
@Produces(MediaType.APPLICATION_JSON)
public class QuizzesController {

    @Inject
    private QuizzesService quizzesService;

    // ════════════════════════════════════════════════════════════════════════
    // QUIZZES
    // ════════════════════════════════════════════════════════════════════════

    // GET /api/Quizzes/getAllQuizzes
    @GET
    @Path("getAllQuizzes")
    public Response getAllQuizzes(@Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        return build(quizzesService.getAllQuizzes());
    }

    // POST /api/Quizzes/createQuiz
    // Body: { "courseId": 3, "title": "Java alapok kvíz" }
    @POST
    @Path("createQuiz")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createQuiz(String body, @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject json = new JSONObject(body);
        return build(quizzesService.createQuiz(
                json.getInt("courseId"),
                json.getString("title")));
    }

    // PUT /api/Quizzes/updateQuiz/{id}
    @PUT
    @Path("updateQuiz/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateQuiz(@PathParam("id") int id,
                                String body,
                                @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject json = new JSONObject(body);
        Integer courseId = json.has("course_id") ? json.getInt("course_id") : null;
        String title     = json.optString("title", null);
        return build(quizzesService.updateQuiz(id, courseId, title));
    }

    // DELETE /api/Quizzes/deleteQuiz/{id}
    @DELETE
    @Path("deleteQuiz/{id}")
    public Response deleteQuiz(@PathParam("id") int id,
                                @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        return build(quizzesService.deleteQuiz(id));
    }

    // ════════════════════════════════════════════════════════════════════════
    // QUESTIONS
    // ════════════════════════════════════════════════════════════════════════

    // GET /api/Quizzes/getAllQuestions
    @GET
    @Path("getAllQuestions")
    public Response getAllQuestions(@Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        return build(quizzesService.getAllQuestions());
    }

    // GET /api/Quizzes/getQuestionsByQuiz/{quizId}
    @GET
    @Path("getQuestionsByQuiz/{quizId}")
    public Response getQuestionsByQuiz(@PathParam("quizId") int quizId) {
        return build(quizzesService.getQuestionsByQuiz(quizId));
    }

    // POST /api/Quizzes/createQuestion
    // Body: { "quizId": 2, "question": "Mi a Java?", "correctAnswer": "Programozási nyelv" }
    @POST
    @Path("createQuestion")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createQuestion(String body, @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject json = new JSONObject(body);
        return build(quizzesService.createQuestion(
                json.getInt("quizId"),
                json.getString("question"),
                json.getString("correctAnswer")));
    }

    // PUT /api/Quizzes/updateQuestion/{id}
    @PUT
    @Path("updateQuestion/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateQuestion(@PathParam("id") int id,
                                    String body,
                                    @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject json = new JSONObject(body);
        return build(quizzesService.updateQuestion(
                id,
                json.has("quiz_id") ? json.getInt("quiz_id") : null,
                json.optString("question", null),
                json.optString("correct_answer", null)));
    }

    // DELETE /api/Quizzes/deleteQuestion/{id}
    @DELETE
    @Path("deleteQuestion/{id}")
    public Response deleteQuestion(@PathParam("id") int id,
                                    @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        return build(quizzesService.deleteQuestion(id));
    }

    // ════════════════════════════════════════════════════════════════════════
    // RESULTS
    // ════════════════════════════════════════════════════════════════════════

    // GET /api/Quizzes/getAllResults
    @GET
    @Path("getAllResults")
    public Response getAllResults(@Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        return build(quizzesService.getAllResults());
    }

    // PUT /api/Quizzes/updateResult/{id}
    // Body: { "score": 85.50 }
    @PUT
    @Path("updateResult/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateResult(@PathParam("id") int id,
                                  String body,
                                  @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        JSONObject json = new JSONObject(body);
        double score = json.optDouble("score", 0.0);
        return build(quizzesService.updateResult(id, score));
    }

    // DELETE /api/Quizzes/deleteResult/{id}
    @DELETE
    @Path("deleteResult/{id}")
    public Response deleteResult(@PathParam("id") int id,
                                  @Context HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return unauth();
        return build(quizzesService.deleteResult(id));
    }

    private Response build(JSONObject r) {
        return Response.status(r.getInt("statusCode")).entity(r.toString()).build();
    }
    private Response unauth() {
        return Response.status(401)
                .entity("{\"statusCode\":401,\"message\":\"Bejelentkezés szükséges\"}")
                .build();
    }
}
