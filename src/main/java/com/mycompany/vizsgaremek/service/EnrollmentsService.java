package com.mycompany.vizsgaremek.service;

import javax.inject.Inject;
import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.model.Enrollments;
import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.model.Courses;
import com.mycompany.vizsgaremek.model.CourseSessions;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class EnrollmentsService {

    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    public JSONObject getAllEnrollments() {
        JSONObject resp = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            Query q = em.createNativeQuery(
                "SELECT e.id, e.user_id, e.course_id, e.session_id, e.status, e.created_at, " +
                "u.name AS student_name, c.title AS course_title " +
                "FROM enrollments e " +
                "LEFT JOIN users u ON e.user_id = u.id " +
                "LEFT JOIN courses c ON e.course_id = c.id " +
                "ORDER BY e.created_at DESC");
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            for (Object[] r : rows) {
                JSONObject o = new JSONObject();
                o.put("id",           r[0]);
                o.put("user_id",      r[1]);
                o.put("course_id",    r[2]);
                o.put("session_id",   r[3] != null ? r[3] : JSONObject.NULL);
                o.put("status",       r[4] != null ? r[4] : "registered");
                o.put("created_at",   r[5] != null ? r[5].toString() : "");
                o.put("student_name", r[6] != null ? r[6] : "–");
                o.put("course_title", r[7] != null ? r[7] : "–");
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

    public JSONObject getEnrollmentsByUser(Integer userId) {
        JSONObject resp = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            Query q = em.createNativeQuery(
                "SELECT e.id, e.user_id, e.course_id, e.session_id, e.status, e.created_at, " +
                "c.title AS course_title, c.start_date, c.end_date, " +
                "c.header_image, c.description, " +
                "u.name AS instructor_name " +
                "FROM enrollments e " +
                "LEFT JOIN courses c ON e.course_id = c.id " +
                "LEFT JOIN users u ON c.instructor_id = u.id " +
                "WHERE e.user_id = :uid ORDER BY e.created_at DESC");
            q.setParameter("uid", userId);
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            for (Object[] r : rows) {
                JSONObject o = new JSONObject();
                o.put("id",              r[0]);
                o.put("user_id",         r[1]);
                o.put("course_id",       r[2]);
                o.put("session_id",      r[3]  != null ? r[3]  : JSONObject.NULL);
                o.put("status",          r[4]  != null ? r[4]  : "registered");
                o.put("created_at",      r[5]  != null ? r[5].toString() : "");
                o.put("course_title",    r[6]  != null ? r[6]  : "–");
                o.put("start_date",      r[7]  != null ? r[7].toString() : "");
                o.put("end_date",        r[8]  != null ? r[8].toString() : "");
                o.put("header_image",    r[9]  != null ? r[9]  : "");
                o.put("description",     r[10] != null ? r[10] : "");
                o.put("instructor_name", r[11] != null ? r[11] : "");
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

    public JSONObject createEnrollment(Integer userId, Integer courseId, Integer sessionId) {
        JSONObject resp = new JSONObject();
        try {
            Query check = em.createNativeQuery(
                "SELECT COUNT(*) FROM enrollments WHERE user_id = :uid AND course_id = :cid");
            check.setParameter("uid", userId);
            check.setParameter("cid", courseId);
            Number cnt = (Number) check.getSingleResult();
            if (cnt.intValue() > 0) {
                resp.put("statusCode", 400);
                resp.put("message", "Már be vagy iratkozva erre a tanfolyamra");
                return resp;
            }
            Enrollments e = new Enrollments();
            e.setUserId(em.find(Users.class, userId));
            e.setCourseId(em.find(Courses.class, courseId));
            if (sessionId != null) {
                e.setSessionId(em.find(CourseSessions.class, sessionId));
            }
            e.setStatus("registered");
            e.setCreatedAt(new Date());
            em.persist(e);
            resp.put("statusCode", 201);
            resp.put("status", "EnrollmentCreated");
            resp.put("message", "Sikeresen beiratkoztál");
        } catch (Exception ex) {
            ex.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + ex.getMessage());
        }
        return resp;
    }

    public JSONObject updateEnrollment(Integer enrollmentId, String status) {
        JSONObject resp = new JSONObject();
        try {
            Enrollments e = em.find(Enrollments.class, enrollmentId);
            if (e == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Beiratkozás nem található");
                return resp;
            }
            if (status != null && !status.trim().isEmpty()) {
                e.setStatus(status);
            }
            em.merge(e);
            resp.put("statusCode", 200);
            resp.put("status", "EnrollmentUpdated");
            resp.put("message", "Beiratkozás frissítve");
        } catch (Exception ex) {
            ex.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + ex.getMessage());
        }
        return resp;
    }

    public JSONObject deleteEnrollment(Integer enrollmentId, Integer requestingUserId) {
        JSONObject resp = new JSONObject();
        try {
            Enrollments e = em.find(Enrollments.class, enrollmentId);
            if (e == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Beiratkozás nem található");
                return resp;
            }
            Users requester = em.find(Users.class, requestingUserId);
            boolean isAdmin = requester != null && "admin".equals(requester.getRole());
            boolean isOwner = e.getUserId() != null && e.getUserId().getId().equals(requestingUserId);
            if (!isAdmin && !isOwner) {
                resp.put("statusCode", 403);
                resp.put("message", "Nincs jogosultságod");
                return resp;
            }
            em.remove(e);
            resp.put("statusCode", 200);
            resp.put("status", "EnrollmentDeleted");
            resp.put("message", "Beiratkozás törölve");
        } catch (Exception ex) {
            ex.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + ex.getMessage());
        }
        return resp;
    }

    /**
     * Számla email küldése a bejelentkezett felhasználónak tanfolyam vásárlás után.
     */
    @Inject
    private EmailService emailService;
    public JSONObject sendCourseInvoice(Integer userId,
                                        String courseName,
                                        long coursePrice, long vatAmount, long totalAmount,
                                        String transactionId,
                                        String courseStart, String courseEnd,
                                        String instructorName) {
        JSONObject resp = new JSONObject();
        try {
            Users user = em.find(Users.class, userId);
            if (user == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Felhasználó nem található");
                return resp;
            }

            boolean sent = emailService.sendInvoiceEmail(
                    user.getName(),
                    user.getEmail(),
                    courseName,
                    coursePrice,
                    vatAmount,
                    totalAmount,
                    transactionId,
                    courseStart,
                    courseEnd,
                    instructorName
            );

            if (sent) {
                resp.put("statusCode", 200);
                resp.put("message", "Számla email sikeresen elküldve");
            } else {
                resp.put("statusCode", 500);
                resp.put("message", "Email küldési hiba");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }
}