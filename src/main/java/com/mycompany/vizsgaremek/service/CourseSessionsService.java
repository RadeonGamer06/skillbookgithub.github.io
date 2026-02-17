package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.CourseSessions;
import com.mycompany.vizsgaremek.model.Courses;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Stateless
public class CourseSessionsService {

    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public JSONObject getAllSessions() {
        JSONObject resp = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            Query q = em.createNativeQuery(
                "SELECT cs.id, cs.course_id, cs.start_at, cs.end_at, c.title AS course_title " +
                "FROM course_sessions cs " +
                "LEFT JOIN courses c ON cs.course_id = c.id " +
                "ORDER BY cs.start_at DESC");
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            for (Object[] r : rows) {
                JSONObject o = new JSONObject();
                o.put("id",           r[0]);
                o.put("course_id",    r[1]);
                o.put("start_at",     r[2] != null ? r[2].toString() : "");
                o.put("end_at",       r[3] != null ? r[3].toString() : "");
                o.put("course_title", r[4] != null ? r[4] : "–");
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

    public JSONObject getSessionsByCourse(Integer courseId) {
        JSONObject resp = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            Query q = em.createNativeQuery(
                "SELECT id, course_id, start_at, end_at FROM course_sessions " +
                "WHERE course_id = :cid ORDER BY start_at");
            q.setParameter("cid", courseId);
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            for (Object[] r : rows) {
                JSONObject o = new JSONObject();
                o.put("id",        r[0]);
                o.put("course_id", r[1]);
                o.put("start_at",  r[2] != null ? r[2].toString() : "");
                o.put("end_at",    r[3] != null ? r[3].toString() : "");
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

    public JSONObject createSession(Integer courseId, String startAt, String endAt) {
        JSONObject resp = new JSONObject();
        try {
            CourseSessions cs = new CourseSessions();
            cs.setCourseId(em.find(Courses.class, courseId));
            cs.setStartAt(parseDate(startAt));
            cs.setEndAt(parseDate(endAt));
            em.persist(cs);
            resp.put("statusCode", 201);
            resp.put("status", "SessionCreated");
            resp.put("message", "Időpont létrehozva");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    public JSONObject updateSession(Integer sessionId, Integer courseId, String startAt, String endAt) {
        JSONObject resp = new JSONObject();
        try {
            CourseSessions cs = em.find(CourseSessions.class, sessionId);
            if (cs == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Időpont nem található");
                return resp;
            }
            if (courseId != null) cs.setCourseId(em.find(Courses.class, courseId));
            if (startAt != null && !startAt.isEmpty()) cs.setStartAt(parseDate(startAt));
            if (endAt   != null && !endAt.isEmpty())   cs.setEndAt(parseDate(endAt));
            em.merge(cs);
            resp.put("statusCode", 200);
            resp.put("status", "SessionUpdated");
            resp.put("message", "Időpont frissítve");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    public JSONObject deleteSession(Integer sessionId) {
        JSONObject resp = new JSONObject();
        try {
            CourseSessions cs = em.find(CourseSessions.class, sessionId);
            if (cs == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Időpont nem található");
                return resp;
            }
            // Remove enrollments pointing to this session first
            em.createNativeQuery("UPDATE enrollments SET session_id = NULL WHERE session_id = :sid")
              .setParameter("sid", sessionId).executeUpdate();
            em.remove(cs);
            resp.put("statusCode", 200);
            resp.put("status", "SessionDeleted");
            resp.put("message", "Időpont törölve");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    private Date parseDate(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(s.replace(" ", "T"));
        } catch (Exception e) {
            try { return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s); }
            catch (Exception ex) { return null; }
        }
    }
}
