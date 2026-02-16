package com.mycompany.vizsgaremek.service;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ReviewsService {

    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    /**
     * Értékelés létrehozása
     */
    public JSONObject createReview(Integer userId, Integer courseId, Integer rating, String comment) {
        JSONObject resp = new JSONObject();

        try {
            // Ellenőrzés: létezik-e már értékelés ettől a usertől erre a tanfolyamra
            Query checkQuery = em.createNativeQuery(
                "SELECT COUNT(*) FROM course_reviews WHERE user_id = :userId AND course_id = :courseId");
            checkQuery.setParameter("userId", userId);
            checkQuery.setParameter("courseId", courseId);
            
            Number count = (Number) checkQuery.getSingleResult();
            
            if (count.intValue() > 0) {
                resp.put("statusCode", 400);
                resp.put("message", "Már értékelted ezt a tanfolyamot");
                return resp;
            }

            // Értékelés mentése
            Query insertQuery = em.createNativeQuery(
                "INSERT INTO course_reviews (user_id, course_id, rating, comment, created_at) " +
                "VALUES (:userId, :courseId, :rating, :comment, NOW())");
            
            insertQuery.setParameter("userId", userId);
            insertQuery.setParameter("courseId", courseId);
            insertQuery.setParameter("rating", rating);
            insertQuery.setParameter("comment", comment);
            insertQuery.executeUpdate();

            resp.put("statusCode", 201);
            resp.put("status", "ReviewCreated");
            resp.put("message", "Értékelés sikeresen létrehozva");

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }

        return resp;
    }

    /**
     * Tanfolyam értékeléseinek lekérése
     */
    public JSONObject getReviewsByCourse(Integer courseId) {
        JSONObject resp = new JSONObject();
        JSONArray reviews = new JSONArray();

        try {
            Query query = em.createNativeQuery(
                "SELECT cr.id, cr.user_id, cr.course_id, cr.rating, cr.comment, cr.created_at, " +
                "u.name as user_name, u.email as user_email " +
                "FROM course_reviews cr " +
                "LEFT JOIN users u ON cr.user_id = u.id " +
                "WHERE cr.course_id = :courseId " +
                "ORDER BY cr.created_at DESC");
            
            query.setParameter("courseId", courseId);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();

            for (Object[] row : results) {
                JSONObject review = new JSONObject();
                review.put("id", row[0]);
                review.put("user_id", row[1]);
                review.put("course_id", row[2]);
                review.put("rating", row[3]);
                review.put("comment", row[4] != null ? row[4] : "");
                review.put("created_at", row[5] != null ? row[5].toString() : "");
                review.put("user_name", row[6] != null ? row[6] : "Névtelen");
                review.put("user_email", row[7] != null ? row[7] : "");
                reviews.put(review);
            }

            resp.put("statusCode", 200);
            resp.put("status", "Success");
            resp.put("reviews", reviews);

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }

        return resp;
    }

    /**
     * Felhasználó értékeléseinek lekérése
     */
    public JSONObject getReviewsByUser(Integer userId) {
        JSONObject resp = new JSONObject();
        JSONArray reviews = new JSONArray();

        try {
            Query query = em.createNativeQuery(
                "SELECT cr.id, cr.user_id, cr.course_id, cr.rating, cr.comment, cr.created_at, " +
                "c.title as course_title " +
                "FROM course_reviews cr " +
                "LEFT JOIN courses c ON cr.course_id = c.id " +
                "WHERE cr.user_id = :userId " +
                "ORDER BY cr.created_at DESC");
            
            query.setParameter("userId", userId);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();

            for (Object[] row : results) {
                JSONObject review = new JSONObject();
                review.put("id", row[0]);
                review.put("user_id", row[1]);
                review.put("course_id", row[2]);
                review.put("rating", row[3]);
                review.put("comment", row[4] != null ? row[4] : "");
                review.put("created_at", row[5] != null ? row[5].toString() : "");
                review.put("course_title", row[6] != null ? row[6] : "Ismeretlen");
                reviews.put(review);
            }

            resp.put("statusCode", 200);
            resp.put("status", "Success");
            resp.put("reviews", reviews);

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }

        return resp;
    }

    /**
     * Értékelés törlése
     */
    public JSONObject deleteReview(Integer reviewId, Integer userId) {
        JSONObject resp = new JSONObject();

        try {
            // Ellenőrzés: a user sajátja-e az értékelés
            Query checkQuery = em.createNativeQuery(
                "SELECT user_id FROM course_reviews WHERE id = :reviewId");
            checkQuery.setParameter("reviewId", reviewId);
            
            List<?> results = checkQuery.getResultList();
            
            if (results.isEmpty()) {
                resp.put("statusCode", 404);
                resp.put("message", "Értékelés nem található");
                return resp;
            }

            Integer ownerId = ((Number) results.get(0)).intValue();
            
            if (!ownerId.equals(userId)) {
                resp.put("statusCode", 403);
                resp.put("message", "Nincs jogosultságod törölni ezt az értékelést");
                return resp;
            }

            // Törlés
            Query deleteQuery = em.createNativeQuery(
                "DELETE FROM course_reviews WHERE id = :reviewId");
            deleteQuery.setParameter("reviewId", reviewId);
            deleteQuery.executeUpdate();

            resp.put("statusCode", 200);
            resp.put("status", "ReviewDeleted");
            resp.put("message", "Értékelés törölve");

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }

        return resp;
    }
}
