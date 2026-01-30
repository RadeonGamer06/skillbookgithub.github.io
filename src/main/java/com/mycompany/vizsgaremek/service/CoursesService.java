package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Courses;
import com.mycompany.vizsgaremek.model.Users;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import org.json.JSONArray;
import org.json.JSONObject;

@Stateless
public class CoursesService {
    
    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    public JSONObject createCourse(String title, String description, Integer price, 
                                    Integer instructorId, String category, 
                                    Integer maxParticipants, String startDate, String endDate) {
        JSONObject resp = new JSONObject();

        try {
            // First, check if user is an instructor
            Users user = em.find(Users.class, instructorId);
            if (user == null || !"instructor".equals(user.getRole())) {
                resp.put("statusCode", 403);
                resp.put("message", "Csak tanárok hozhatnak létre tanfolyamokat");
                return resp;
            }

            // Handle category - find or use default
            Integer categoryId = null;
            
            if (category != null && !category.trim().isEmpty()) {
                try {
                    // Try to find existing category by name
                    categoryId = em.createQuery(
                        "SELECT c.id FROM Categories c WHERE c.name = :name", Integer.class)
                        .setParameter("name", category.trim())
                        .getSingleResult();
                } catch (NoResultException e) {
                    // Category doesn't exist, use default (ID = 1 or create one)
                    categoryId = 1; // Default to first category
                }
            } else {
                // No category provided, use default
                categoryId = 1; // Default category
            }

            StoredProcedureQuery query = em.createStoredProcedureQuery("createCourse");

            query.registerStoredProcedureParameter("titleIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("descriptionIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("priceIN", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("instructorIdIN", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("categoryIdIN", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("maxParticipantsIN", Integer.class, ParameterMode.IN);

            query.setParameter("titleIN", title);
            query.setParameter("descriptionIN", description);
            query.setParameter("priceIN", price);
            query.setParameter("instructorIdIN", instructorId);
            query.setParameter("categoryIdIN", categoryId);
            query.setParameter("maxParticipantsIN", maxParticipants != null ? maxParticipants : 20);

            query.execute();

            // TODO: If startDate and endDate are provided, create a course_session
            // For now, we'll skip this to keep it simple

            resp.put("status", "CourseCreated");
            resp.put("statusCode", 201);
            resp.put("message", "Tanfolyam sikeresen létrehozva");

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "DatabaseError");
            resp.put("statusCode", 500);
            resp.put("message", "Hiba történt: " + e.getMessage());
        }

        return resp;
    }

    public JSONObject getAllCourses() {
        JSONObject resp = new JSONObject();
        JSONArray arr = new JSONArray();

        try {
            StoredProcedureQuery sp = em.createStoredProcedureQuery("getAllCourses", Courses.class);

            @SuppressWarnings("unchecked")
            List<Courses> courses = sp.getResultList();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (Courses c : courses) {
                JSONObject o = new JSONObject();
                o.put("id", c.getId());
                o.put("title", c.getTitle());
                o.put("description", c.getDescription());
                o.put("price", c.getPrice());
                o.put("instructor_id", c.getInstructorId());
                o.put("category_id", c.getCategoryId());
                o.put("max_participants", c.getMaxParticipants());
                o.put("created_at", c.getCreatedAt() != null ? sdf.format(c.getCreatedAt()) : null);
                
                // Get instructor name
                if (c.getInstructorId() != null) {
                    try {
                        Users instructor = em.find(Users.class, c.getInstructorId());
                        o.put("instructor_name", instructor != null ? instructor.getName() : "Ismeretlen");
                    } catch (Exception e) {
                        o.put("instructor_name", "Ismeretlen");
                    }
                }
                
                arr.put(o);
            }

            resp.put("status", "CoursesFetched");
            resp.put("statusCode", 200);
            resp.put("data", arr);

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "DatabaseError");
            resp.put("message", e.getMessage());
            resp.put("statusCode", 500);
        }

        return resp;
    }

    public JSONObject getCourseById(int courseId) {
        JSONObject resp = new JSONObject();
        
        try {
            StoredProcedureQuery sp = em.createStoredProcedureQuery("getCourseById", Courses.class);
            sp.registerStoredProcedureParameter("courseIdIN", Integer.class, ParameterMode.IN);
            sp.setParameter("courseIdIN", courseId);

            Courses course = (Courses) sp.getSingleResult();

            JSONObject c = new JSONObject();
            c.put("id", course.getId());
            c.put("title", course.getTitle());
            c.put("description", course.getDescription());
            c.put("price", course.getPrice());
            c.put("instructor_id", course.getInstructorId());
            c.put("category_id", course.getCategoryId());
            c.put("max_participants", course.getMaxParticipants());
            c.put("created_at", course.getCreatedAt() != null ? course.getCreatedAt().toString() : null);

            // Get instructor name
            if (course.getInstructorId() != null) {
                try {
                    Users instructor = em.find(Users.class, course.getInstructorId());
                    c.put("instructor_name", instructor != null ? instructor.getName() : "Ismeretlen");
                } catch (Exception e) {
                    c.put("instructor_name", "Ismeretlen");
                }
            }

            resp.put("status", "CourseFound");
            resp.put("statusCode", 200);
            resp.put("data", c);
            
        } catch (NoResultException e) {
            resp.put("status", "CourseNotFound");
            resp.put("statusCode", 404);
            resp.put("message", "Tanfolyam nem található: " + courseId);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "DatabaseError");
            resp.put("statusCode", 500);
            resp.put("message", e.getMessage());
        }
        
        return resp;
    }

    public JSONObject updateCourse(int courseId, String title, String description, 
                                   Integer price, Integer userId) {
        JSONObject resp = new JSONObject();

        try {
            // Check if course exists and user is the instructor
            Courses course = em.find(Courses.class, courseId);
            if (course == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Tanfolyam nem található");
                return resp;
            }

            Users user = em.find(Users.class, userId);
            if (user == null || (!"instructor".equals(user.getRole()) && !"admin".equals(user.getRole()))) {
                resp.put("statusCode", 403);
                resp.put("message", "Nincs jogosultságod");
                return resp;
            }

            if (!"admin".equals(user.getRole()) && !course.getInstructorId().equals(userId)) {
                resp.put("statusCode", 403);
                resp.put("message", "Csak a saját tanfolyamadat módosíthatod");
                return resp;
            }

            // Update fields
            if (title != null && !title.trim().isEmpty()) {
                course.setTitle(title);
            }
            if (description != null) {
                course.setDescription(description);
            }
            if (price != null) {
                course.setPrice(price);
            }

            em.merge(course);

            resp.put("status", "CourseUpdated");
            resp.put("statusCode", 200);
            resp.put("message", "Tanfolyam frissítve");

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }

        return resp;
    }

    public JSONObject deleteCourse(int courseId, Integer userId) {
        JSONObject resp = new JSONObject();

        try {
            Courses course = em.find(Courses.class, courseId);
            if (course == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Tanfolyam nem található");
                return resp;
            }

            Users user = em.find(Users.class, userId);
            if (user == null || (!"instructor".equals(user.getRole()) && !"admin".equals(user.getRole()))) {
                resp.put("statusCode", 403);
                resp.put("message", "Nincs jogosultságod");
                return resp;
            }

            if (!"admin".equals(user.getRole()) && !course.getInstructorId().equals(userId)) {
                resp.put("statusCode", 403);
                resp.put("message", "Csak a saját tanfolyamadat törölheted");
                return resp;
            }

            StoredProcedureQuery q = em.createStoredProcedureQuery("deleteCourse");
            q.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
            q.setParameter(1, courseId);
            q.execute();

            resp.put("status", "CourseDeleted");
            resp.put("statusCode", 200);
            resp.put("message", "Tanfolyam törölve");

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }

        return resp;
    }
}