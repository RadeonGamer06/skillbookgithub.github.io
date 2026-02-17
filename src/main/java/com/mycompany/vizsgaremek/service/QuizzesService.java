package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Quizzes;
import com.mycompany.vizsgaremek.model.QuizQuestions;
import com.mycompany.vizsgaremek.model.Courses;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class QuizzesService {

    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    // ════════════════════════════════════════════════════════════════════════
    // QUIZZES
    // ════════════════════════════════════════════════════════════════════════

    public JSONObject getAllQuizzes() {
        JSONObject resp = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            Query q = em.createNativeQuery(
                "SELECT qz.id, qz.course_id, qz.title, c.title AS course_title " +
                "FROM quizzes qz LEFT JOIN courses c ON qz.course_id = c.id");
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            for (Object[] r : rows) {
                JSONObject o = new JSONObject();
                o.put("id",           r[0]);
                o.put("course_id",    r[1]);
                o.put("title",        r[2] != null ? r[2] : "");
                o.put("course_title", r[3] != null ? r[3] : "–");
                arr.put(o);
            }
            resp.put("statusCode", 200);
            resp.put("data", arr);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    public JSONObject createQuiz(Integer courseId, String title) {
        JSONObject resp = new JSONObject();
        try {
            Quizzes qz = new Quizzes();
            qz.setTitle(title);
            qz.setCourseId(em.find(Courses.class, courseId));
            em.persist(qz);
            resp.put("statusCode", 201);
            resp.put("status", "QuizCreated");
            resp.put("message", "Kvíz létrehozva");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    public JSONObject updateQuiz(Integer quizId, Integer courseId, String title) {
        JSONObject resp = new JSONObject();
        try {
            Quizzes qz = em.find(Quizzes.class, quizId);
            if (qz == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Kvíz nem található");
                return resp;
            }
            if (title != null && !title.trim().isEmpty()) qz.setTitle(title);
            if (courseId != null) qz.setCourseId(em.find(Courses.class, courseId));
            em.merge(qz);
            resp.put("statusCode", 200);
            resp.put("status", "QuizUpdated");
            resp.put("message", "Kvíz frissítve");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    public JSONObject deleteQuiz(Integer quizId) {
        JSONObject resp = new JSONObject();
        try {
            Quizzes qz = em.find(Quizzes.class, quizId);
            if (qz == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Kvíz nem található");
                return resp;
            }
            // cascade: delete results then questions
            em.createNativeQuery("DELETE FROM quiz_results WHERE quiz_id = :qid")
              .setParameter("qid", quizId).executeUpdate();
            em.createNativeQuery("DELETE FROM quiz_questions WHERE quiz_id = :qid")
              .setParameter("qid", quizId).executeUpdate();
            em.remove(em.contains(qz) ? qz : em.merge(qz));
            resp.put("statusCode", 200);
            resp.put("status", "QuizDeleted");
            resp.put("message", "Kvíz törölve");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    // ════════════════════════════════════════════════════════════════════════
    // QUIZ QUESTIONS
    // ════════════════════════════════════════════════════════════════════════

    public JSONObject getAllQuestions() {
        JSONObject resp = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            Query q = em.createNativeQuery(
                "SELECT qq.id, qq.quiz_id, qq.question, qq.correct_answer, qz.title AS quiz_title " +
                "FROM quiz_questions qq LEFT JOIN quizzes qz ON qq.quiz_id = qz.id");
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            for (Object[] r : rows) {
                JSONObject o = new JSONObject();
                o.put("id",             r[0]);
                o.put("quiz_id",        r[1]);
                o.put("question",       r[2] != null ? r[2] : "");
                o.put("correct_answer", r[3] != null ? r[3] : "");
                o.put("quiz_title",     r[4] != null ? r[4] : "–");
                arr.put(o);
            }
            resp.put("statusCode", 200);
            resp.put("data", arr);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    public JSONObject getQuestionsByQuiz(Integer quizId) {
        JSONObject resp = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            Query q = em.createNativeQuery(
                "SELECT id, quiz_id, question, correct_answer FROM quiz_questions WHERE quiz_id = :qid");
            q.setParameter("qid", quizId);
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            for (Object[] r : rows) {
                JSONObject o = new JSONObject();
                o.put("id",             r[0]);
                o.put("quiz_id",        r[1]);
                o.put("question",       r[2] != null ? r[2] : "");
                o.put("correct_answer", r[3] != null ? r[3] : "");
                arr.put(o);
            }
            resp.put("statusCode", 200);
            resp.put("data", arr);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    public JSONObject createQuestion(Integer quizId, String question, String correctAnswer) {
        JSONObject resp = new JSONObject();
        try {
            QuizQuestions qq = new QuizQuestions();
            qq.setQuestion(question);
            qq.setCorrectAnswer(correctAnswer);
            qq.setQuizId(em.find(Quizzes.class, quizId));
            em.persist(qq);
            resp.put("statusCode", 201);
            resp.put("status", "QuestionCreated");
            resp.put("message", "Kérdés létrehozva");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    public JSONObject updateQuestion(Integer questionId, Integer quizId, String question, String correctAnswer) {
        JSONObject resp = new JSONObject();
        try {
            QuizQuestions qq = em.find(QuizQuestions.class, questionId);
            if (qq == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Kérdés nem található");
                return resp;
            }
            if (question      != null) qq.setQuestion(question);
            if (correctAnswer != null) qq.setCorrectAnswer(correctAnswer);
            if (quizId        != null) qq.setQuizId(em.find(Quizzes.class, quizId));
            em.merge(qq);
            resp.put("statusCode", 200);
            resp.put("status", "QuestionUpdated");
            resp.put("message", "Kérdés frissítve");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    public JSONObject deleteQuestion(Integer questionId) {
        JSONObject resp = new JSONObject();
        try {
            QuizQuestions qq = em.find(QuizQuestions.class, questionId);
            if (qq == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Kérdés nem található");
                return resp;
            }
            em.remove(em.contains(qq) ? qq : em.merge(qq));
            resp.put("statusCode", 200);
            resp.put("status", "QuestionDeleted");
            resp.put("message", "Kérdés törölve");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    // ════════════════════════════════════════════════════════════════════════
    // QUIZ RESULTS
    // ════════════════════════════════════════════════════════════════════════

    public JSONObject getAllResults() {
        JSONObject resp = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            Query q = em.createNativeQuery(
                "SELECT qr.id, qr.user_id, qr.quiz_id, qr.score, qr.completed_at, " +
                "u.name AS student_name, qz.title AS quiz_title " +
                "FROM quiz_results qr " +
                "LEFT JOIN users u ON qr.user_id = u.id " +
                "LEFT JOIN quizzes qz ON qr.quiz_id = qz.id " +
                "ORDER BY qr.completed_at DESC");
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            for (Object[] r : rows) {
                JSONObject o = new JSONObject();
                o.put("id",           r[0]);
                o.put("user_id",      r[1]);
                o.put("quiz_id",      r[2]);
                o.put("score",        r[3] != null ? r[3] : JSONObject.NULL);
                o.put("completed_at", r[4] != null ? r[4].toString() : "");
                o.put("student_name", r[5] != null ? r[5] : "–");
                o.put("quiz_title",   r[6] != null ? r[6] : "–");
                arr.put(o);
            }
            resp.put("statusCode", 200);
            resp.put("data", arr);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    public JSONObject updateResult(Integer resultId, Double score) {
        JSONObject resp = new JSONObject();
        try {
            Query upd = em.createNativeQuery(
                "UPDATE quiz_results SET score = :score WHERE id = :rid");
            upd.setParameter("score", score);
            upd.setParameter("rid",   resultId);
            int affected = upd.executeUpdate();
            if (affected == 0) {
                resp.put("statusCode", 404);
                resp.put("message", "Eredmény nem található");
                return resp;
            }
            resp.put("statusCode", 200);
            resp.put("status", "ResultUpdated");
            resp.put("message", "Eredmény frissítve");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    public JSONObject deleteResult(Integer resultId) {
        JSONObject resp = new JSONObject();
        try {
            int affected = em.createNativeQuery("DELETE FROM quiz_results WHERE id = :rid")
                    .setParameter("rid", resultId).executeUpdate();
            if (affected == 0) {
                resp.put("statusCode", 404);
                resp.put("message", "Eredmény nem található");
                return resp;
            }
            resp.put("statusCode", 200);
            resp.put("status", "ResultDeleted");
            resp.put("message", "Eredmény törölve");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }
}
