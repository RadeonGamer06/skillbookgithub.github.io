package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Users;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class AdminUsersService {

    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    // ════════════════════════════════════════════════════════════════════════
    // GET ALL USERS
    // ════════════════════════════════════════════════════════════════════════
    public JSONObject getAllUsers() {
        JSONObject resp = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            Query q = em.createNativeQuery(
                "SELECT id, name, email, role, profile_picture, created_at " +
                "FROM users ORDER BY created_at DESC");
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            for (Object[] r : rows) {
                JSONObject o = new JSONObject();
                o.put("id",              r[0]);
                o.put("name",            r[1] != null ? r[1] : "");
                o.put("email",           r[2] != null ? r[2] : "");
                o.put("role",            r[3] != null ? r[3] : "student");
                o.put("profile_picture", r[4] != null ? r[4] : JSONObject.NULL);
                o.put("created_at",      r[5] != null ? r[5].toString() : "");
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

    // ════════════════════════════════════════════════════════════════════════
    // SET ROLE  (admin only — bármilyen rangra, beleértve "admin"-t)
    // ════════════════════════════════════════════════════════════════════════
    public JSONObject setRole(Integer targetUserId, String newRole, Integer requestingUserId) {
        JSONObject resp = new JSONObject();
        try {
            // Hívó jogosultsága
            Users requester = em.find(Users.class, requestingUserId);
            if (requester == null || !"admin".equals(requester.getRole())) {
                resp.put("statusCode", 403);
                resp.put("message", "Csak admin adhat rangot");
                return resp;
            }

            // Érvényes rang?
            if (!"student".equals(newRole) && !"instructor".equals(newRole) && !"admin".equals(newRole)) {
                resp.put("statusCode", 400);
                resp.put("message", "Érvénytelen rang. Lehetséges értékek: student, instructor, admin");
                return resp;
            }

            Users target = em.find(Users.class, targetUserId);
            if (target == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Felhasználó nem található");
                return resp;
            }

            String oldRole = target.getRole();
            target.setRole(newRole);
            em.merge(target);

            resp.put("statusCode", 200);
            resp.put("status", "RoleUpdated");
            resp.put("message", target.getName() + " rangja megváltozott: " + oldRole + " → " + newRole);
            resp.put("userId", targetUserId);
            resp.put("oldRole", oldRole);
            resp.put("newRole", newRole);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    // ════════════════════════════════════════════════════════════════════════
    // UPDATE USER (admin — name, email, role together)
    // ════════════════════════════════════════════════════════════════════════
    public JSONObject adminUpdateUser(Integer targetId, String name, String email, String role,
                                      Integer requestingUserId) {
        JSONObject resp = new JSONObject();
        try {
            Users requester = em.find(Users.class, requestingUserId);
            if (requester == null || !"admin".equals(requester.getRole())) {
                resp.put("statusCode", 403);
                resp.put("message", "Csak admin szerkeszthet felhasználókat");
                return resp;
            }

            Users target = em.find(Users.class, targetId);
            if (target == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Felhasználó nem található");
                return resp;
            }

            if (name  != null && !name.trim().isEmpty())  target.setName(name.trim());
            if (email != null && !email.trim().isEmpty())  target.setEmail(email.trim());
            if (role  != null && !role.trim().isEmpty())   target.setRole(role.trim());

            em.merge(target);
            resp.put("statusCode", 200);
            resp.put("status", "UserUpdated");
            resp.put("message", "Felhasználó frissítve");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }

    // ════════════════════════════════════════════════════════════════════════
    // DELETE USER (admin)
    // ════════════════════════════════════════════════════════════════════════
    public JSONObject adminDeleteUser(Integer targetId, Integer requestingUserId) {
        JSONObject resp = new JSONObject();
        try {
            Users requester = em.find(Users.class, requestingUserId);
            if (requester == null || !"admin".equals(requester.getRole())) {
                resp.put("statusCode", 403);
                resp.put("message", "Csak admin törölhet felhasználókat");
                return resp;
            }
            if (targetId.equals(requestingUserId)) {
                resp.put("statusCode", 400);
                resp.put("message", "Nem törölheted saját magadat");
                return resp;
            }
            Users target = em.find(Users.class, targetId);
            if (target == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Felhasználó nem található");
                return resp;
            }
            // Cascade clean-up
            em.createNativeQuery("DELETE FROM quiz_results WHERE user_id = :uid").setParameter("uid", targetId).executeUpdate();
            em.createNativeQuery("DELETE FROM messages WHERE sender_id = :uid OR receiver_id = :uid").setParameter("uid", targetId).executeUpdate();
            em.createNativeQuery("DELETE FROM enrollments WHERE user_id = :uid").setParameter("uid", targetId).executeUpdate();
            em.createNativeQuery("DELETE FROM course_reviews WHERE user_id = :uid").setParameter("uid", targetId).executeUpdate();
            em.createNativeQuery("UPDATE courses SET instructor_id = NULL WHERE instructor_id = :uid").setParameter("uid", targetId).executeUpdate();

            em.remove(em.contains(target) ? target : em.merge(target));

            resp.put("statusCode", 200);
            resp.put("status", "UserDeleted");
            resp.put("message", "Felhasználó törölve");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }
}
