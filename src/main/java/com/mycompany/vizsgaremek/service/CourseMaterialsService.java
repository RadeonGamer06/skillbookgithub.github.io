package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.CourseMaterials;
import com.mycompany.vizsgaremek.model.Courses;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class CourseMaterialsService {

    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    // ════════════════════════════════════════════════════════════════════════
    // GET ALL (admin)
    // ════════════════════════════════════════════════════════════════════════
    public JSONObject getAllMaterials() {
        JSONObject resp = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            Query q = em.createNativeQuery(
                "SELECT cm.id, cm.course_id, cm.title, cm.file_url, cm.uploaded_by, cm.uploaded_at, " +
                "c.title AS course_title, u.name AS uploader_name " +
                "FROM course_materials cm " +
                "LEFT JOIN courses c ON cm.course_id = c.id " +
                "LEFT JOIN users u ON cm.uploaded_by = u.id " +
                "ORDER BY cm.uploaded_at DESC");
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            for (Object[] r : rows) {
                JSONObject o = new JSONObject();
                o.put("id",            r[0]);
                o.put("course_id",     r[1]);
                o.put("title",         r[2] != null ? r[2] : "");
                o.put("file_url",      r[3] != null ? r[3] : "");
                o.put("uploaded_by",   r[4] != null ? r[4] : JSONObject.NULL);
                o.put("uploaded_at",   r[5] != null ? r[5].toString() : "");
                o.put("course_title",  r[6] != null ? r[6] : "–");
                o.put("uploader_name", r[7] != null ? r[7] : "–");
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
    // GET BY COURSE
    // ════════════════════════════════════════════════════════════════════════
    public JSONObject getMaterialsByCourse(Integer courseId) {
        JSONObject resp = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            Query q = em.createNativeQuery(
                "SELECT id, course_id, title, file_url, uploaded_by, uploaded_at " +
                "FROM course_materials WHERE course_id = :cid ORDER BY uploaded_at");
            q.setParameter("cid", courseId);
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            for (Object[] r : rows) {
                JSONObject o = new JSONObject();
                o.put("id",          r[0]);
                o.put("course_id",   r[1]);
                o.put("title",       r[2] != null ? r[2] : "");
                o.put("file_url",    r[3] != null ? r[3] : "");
                o.put("uploaded_by", r[4] != null ? r[4] : JSONObject.NULL);
                o.put("uploaded_at", r[5] != null ? r[5].toString() : "");
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
    // DELETE
    // ════════════════════════════════════════════════════════════════════════
    public JSONObject deleteMaterial(Integer materialId) {
        JSONObject resp = new JSONObject();
        try {
            CourseMaterials cm = em.find(CourseMaterials.class, materialId);
            if (cm == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Tananyag nem található");
                return resp;
            }
            em.remove(cm);
            resp.put("statusCode", 200);
            resp.put("status", "MaterialDeleted");
            resp.put("message", "Tananyag törölve");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }
}
