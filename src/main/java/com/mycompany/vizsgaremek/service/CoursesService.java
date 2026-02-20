package com.mycompany.vizsgaremek.service;
import com.mycompany.vizsgaremek.model.Courses;
import com.mycompany.vizsgaremek.model.Users;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

@Stateless
public class CoursesService {
    
    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;
    
    // ════════════════════════════════════════════════════════════════════════
    // HELPER METHODS (változatlan)
    // ════════════════════════════════════════════════════════════════════════
    private LocalDateTime parseFlexibleDate(String value, boolean isEnd) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
            return null;
        }
        String v = value.trim();
        try {
            if (v.length() == 10 && v.charAt(4) == '-' && v.charAt(7) == '-') {
                LocalDate d = LocalDate.parse(v, DateTimeFormatter.ISO_LOCAL_DATE);
                return isEnd ? d.atTime(23, 59, 59) : d.atStartOfDay();
            }
            v = v.replace(' ', 'T');
            if (v.length() == 16) {
                return LocalDateTime.parse(v, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            }
            if (v.length() == 19) {
                return LocalDateTime.parse(v, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            }
            return LocalDateTime.parse(v);
        } catch (Exception ex) {
            return null;
        }
    }
    
    private void tryInvokeSetter(Object target, String methodName, LocalDateTime dt) {
        if (target == null || methodName == null || dt == null) return;
        for (Method m : target.getClass().getMethods()) {
            if (!m.getName().equals(methodName) || m.getParameterCount() != 1) continue;
            Class<?> p = m.getParameterTypes()[0];
            try {
                Object arg = null;
                if (p.equals(LocalDateTime.class)) {
                    arg = dt;
                } else if (p.equals(Date.class)) {
                    arg = Timestamp.valueOf(dt);
                } else if (p.equals(Timestamp.class)) {
                    arg = Timestamp.valueOf(dt);
                } else if (p.equals(String.class)) {
                    arg = dt.toString();
                } else {
                    continue;
                }
                m.invoke(target, arg);
                return;
            } catch (Exception ignored) {
            }
        }
    }
    
    private void setStartEndDates(Courses course, String startDate, String endDate) {
        LocalDateTime start = parseFlexibleDate(startDate, false);
        LocalDateTime end = parseFlexibleDate(endDate, true);
        if (start != null) {
            tryInvokeSetter(course, "setStartDate", start);
        }
        if (end != null) {
            tryInvokeSetter(course, "setEndDate", end);
        }
    }
    
    private String readDateAsIso(Object course, String getterName) {
        if (course == null || getterName == null) return null;
        try {
            Method m = course.getClass().getMethod(getterName);
            Object val = m.invoke(course);
            if (val == null) return null;
            if (val instanceof Timestamp) {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format((Timestamp) val);
            }
            if (val instanceof Date) {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format((Date) val);
            }
            if (val instanceof LocalDateTime) {
                return ((LocalDateTime) val).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (val instanceof LocalDate) {
                return ((LocalDate) val).format(DateTimeFormatter.ISO_LOCAL_DATE);
            }
            if (val instanceof String) {
                return (String) val;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
    
    private String extractStartDateIso(Courses c) {
        return readDateAsIso(c, "getStartDate");
    }
    
    private String extractEndDateIso(Courses c) {
        return readDateAsIso(c, "getEndDate");
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // CREATE COURSE
    // ════════════════════════════════════════════════════════════════════════
    public JSONObject createCourse(String title, String description, Integer price, Integer instructorId,
            Integer categoryId, Integer maxParticipants, String startDate, String endDate) {
        JSONObject resp = new JSONObject();
        try {
            // Natív SQL INSERT használata stored procedure helyett
            String sql = "INSERT INTO courses (title, description, price, instructor_id, category_id, max_participants, created_at) " +
                         "VALUES (:title, :description, :price, :instructorId, :categoryId, :maxParticipants, NOW())";
            
            Query query = em.createNativeQuery(sql);
            query.setParameter("title", title);
            query.setParameter("description", description);
            query.setParameter("price", price);
            query.setParameter("instructorId", instructorId);
            query.setParameter("categoryId", categoryId);
            query.setParameter("maxParticipants", maxParticipants);
            query.executeUpdate();
            
            // Az utoljára beszúrt ID lekérése
            BigInteger bigId = (BigInteger) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult();
            Integer newCourseId = bigId.intValueExact();
            
            // Dátumok beállítása ha meg vannak adva
            if ((startDate != null && !startDate.trim().isEmpty()) || (endDate != null && !endDate.trim().isEmpty())) {
                Courses newCourse = em.find(Courses.class, newCourseId);
                if (newCourse != null) {
                    setStartEndDates(newCourse, startDate, endDate);
                    em.merge(newCourse);
                }
            }
            resp.put("status", "CourseCreated");
            resp.put("statusCode", 201);
            resp.put("courseId", newCourseId);
            resp.put("message", "Tanfolyam sikeresen létrehozva");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // GET ALL COURSES
    // ════════════════════════════════════════════════════════════════════════
    public JSONObject getAllCourses() {
        JSONObject resp = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            Query query = em.createNativeQuery(
                "SELECT c.id, c.title, c.description, c.price, c.instructor_id, c.category_id, " +
                "c.max_participants, c.start_date, c.end_date, " +
                "u.name AS instructor_name, " +
                "u.profile_picture AS instructor_profile_picture, " +
                "c.header_image " +
                "FROM courses c LEFT JOIN users u ON c.instructor_id = u.id " +
                "ORDER BY c.title");
            List<Object[]> courses = query.getResultList();
            for (Object[] row : courses) {
                JSONObject o = new JSONObject();
                o.put("id",            row[0]);
                o.put("title",         row[1]);
                o.put("description",   row[2]);
                o.put("price",         row[3]);
                o.put("instructor_id", row[4]);
                o.put("category_id",   row[5]);
                o.put("max_participants", row[6]);
                o.put("start_date",    row[7]);
                o.put("end_date",      row[8]);
                o.put("instructor_name", row[9] != null ? row[9] : "Ismeretlen");
                o.put("instructor_profile_picture", row[10] != null ? row[10] : JSONObject.NULL);
                o.put("header_image",  row[11] != null ? row[11] : JSONObject.NULL);
                arr.put(o);
            }
            resp.put("status", "Success");
            resp.put("statusCode", 200);
            resp.put("data", arr);
            resp.put("courses", arr);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // GET COURSE BY ID (with instructor details)
    // ════════════════════════════════════════════════════════════════════════
    public JSONObject getCourseById(int id) {
        JSONObject resp = new JSONObject();
        try {
            Courses course = em.find(Courses.class, id);
            if (course == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Tanfolyam nem található");
                return resp;
            }
            JSONObject data = new JSONObject();
            data.put("id", course.getId());
            data.put("title", course.getTitle());
            data.put("description", course.getDescription());
            data.put("price", course.getPrice());
            data.put("instructor_id", course.getInstructorId());
            data.put("category_id", course.getCategoryId());
            data.put("max_participants", course.getMaxParticipants());
            data.put("start_date", extractStartDateIso(course));
            data.put("end_date", extractEndDateIso(course));
            data.put("header_image", course.getHeaderImage() != null ? course.getHeaderImage() : JSONObject.NULL);
            
            // Fetch instructor details
            if (course.getInstructorId() != null) {
                try {
                    Users instructor = em.find(Users.class, course.getInstructorId());
                    if (instructor != null) {
                        JSONObject instructorData = new JSONObject();
                        instructorData.put("id", instructor.getId());
                        instructorData.put("name", instructor.getName());
                        instructorData.put("email", instructor.getEmail());
                        instructorData.put("role", instructor.getRole());
                        instructorData.put("profile_picture", instructor.getProfilePicture());
                        instructorData.put("created_at", instructor.getCreatedAt().toString());
                        data.put("instructor", instructorData);
                    }
                } catch (Exception e) {
                    System.err.println("Error fetching instructor: " + e.getMessage());
                }
            }
            
            resp.put("status", "Success");
            resp.put("statusCode", 200);
            resp.put("data", data);  // Változtatva "course"-ról "data"-ra
            resp.put("course", data);  // Megtartjuk a régi kulcsot is a kompatibilitás miatt
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        return resp;
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // UPDATE COURSE
    // ════════════════════════════════════════════════════════════════════════
    public JSONObject updateCourse(int id, String title, String description, Integer price, 
                                   String startDate, String endDate, Integer userId) {
        JSONObject resp = new JSONObject();
        try {
            Courses course = em.find(Courses.class, id);
            if (course == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Tanfolyam nem található");
                return resp;
            }
            Users user = em.find(Users.class, userId);
            if (user == null) {
                resp.put("statusCode", 403);
                resp.put("message", "Nincs jogosultságod");
                return resp;
            }
            if (!"admin".equals(user.getRole()) && !course.getInstructorId().equals(userId)) {
                resp.put("statusCode", 403);
                resp.put("message", "Csak a saját tanfolyamodat frissítheted");
                return resp;
            }
            if (title != null && !title.trim().isEmpty()) course.setTitle(title);
            if (description != null) course.setDescription(description);
            if (price != null) course.setPrice(price);
            setStartEndDates(course, startDate, endDate);
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
    
    // ════════════════════════════════════════════════════════════════════════
    // ✅ DELETE COURSE - EXPLICIT TRANSACTION ANNOTATION
    // ════════════════════════════════════════════════════════════════════════
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public JSONObject deleteCourse(int courseId, Integer userId) {
        JSONObject resp = new JSONObject();
        
        System.out.println("=== TANFOLYAM TÖRLÉS ===");
        System.out.println("   Course ID: " + courseId);
        System.out.println("   User ID: " + userId);
        
        try {
            // 1. Jogosultság ellenőrzés
            System.out.println("   → Tanfolyam lekérése...");
            Courses course = em.find(Courses.class, courseId);
            if (course == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Tanfolyam nem található");
                return resp;
            }
            
            Users user = em.find(Users.class, userId);
            if (user == null) {
                resp.put("statusCode", 403);
                resp.put("message", "Nincs jogosultságod");
                return resp;
            }
            
            if (!"instructor".equals(user.getRole()) && !"admin".equals(user.getRole())) {
                resp.put("statusCode", 403);
                resp.put("message", "Nincs jogosultságod (csak oktató vagy admin)");
                return resp;
            }
            
            if (!"admin".equals(user.getRole()) && !course.getInstructorId().equals(userId)) {
                resp.put("statusCode", 403);
                resp.put("message", "Csak a saját tanfolyamadat törölheted");
                return resp;
            }
            
            System.out.println("   ✓ Jogosultság OK");
            
            // 2. TÖRLÉS
            System.out.println("   → Külső kulcs ellenőrzés kikapcsolása...");
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
            em.flush(); // FONTOS: flush a SET parancs után
            
            try {
                // Quiz ID-k lekérése
                System.out.println("   → Quizek lekérdezése...");
                @SuppressWarnings("unchecked")
                List<Integer> quizIds = em.createNativeQuery(
                    "SELECT id FROM quizzes WHERE course_id = :courseId")
                    .setParameter("courseId", courseId)
                    .getResultList();
                
                System.out.println("   ✓ " + quizIds.size() + " quiz találva");
                
                // Quiz eredmények és kérdések törlése
                for (Integer quizId : quizIds) {
                    em.createNativeQuery("DELETE FROM quiz_results WHERE quiz_id = :quizId")
                        .setParameter("quizId", quizId)
                        .executeUpdate();
                    
                    em.createNativeQuery("DELETE FROM quiz_questions WHERE quiz_id = :quizId")
                        .setParameter("quizId", quizId)
                        .executeUpdate();
                    
                    System.out.println("   ✓ Quiz #" + quizId + " törölve");
                }
                
                // Quizek törlése
                int deleted = em.createNativeQuery("DELETE FROM quizzes WHERE course_id = :courseId")
                    .setParameter("courseId", courseId)
                    .executeUpdate();
                System.out.println("   ✓ Quizek törölve: " + deleted);
                
                // ❌ MESSAGES TÖRLÉS KIHAGYVA
                // A messages táblában NINCS course_id oszlop!
                // Ez egy chat üzenet tábla (sender_id, receiver_id)
                
                // Beiratkozások
                deleted = em.createNativeQuery("DELETE FROM enrollments WHERE course_id = :courseId")
                    .setParameter("courseId", courseId)
                    .executeUpdate();
                System.out.println("   ✓ Beiratkozások törölve: " + deleted);
                
                // Értékelések
                deleted = em.createNativeQuery("DELETE FROM course_reviews WHERE course_id = :courseId")
                    .setParameter("courseId", courseId)
                    .executeUpdate();
                System.out.println("   ✓ Értékelések törölve: " + deleted);
                
                // Anyagok
                deleted = em.createNativeQuery("DELETE FROM course_materials WHERE course_id = :courseId")
                    .setParameter("courseId", courseId)
                    .executeUpdate();
                System.out.println("   ✓ Anyagok törölve: " + deleted);
                
                // Órák
                deleted = em.createNativeQuery("DELETE FROM course_sessions WHERE course_id = :courseId")
                    .setParameter("courseId", courseId)
                    .executeUpdate();
                System.out.println("   ✓ Órák törölve: " + deleted);
                
                // Tanfolyam
                deleted = em.createNativeQuery("DELETE FROM courses WHERE id = :courseId")
                    .setParameter("courseId", courseId)
                    .executeUpdate();
                System.out.println("   ✓ Tanfolyam törölve: " + deleted);
                
                em.flush(); // FONTOS: flush a DELETE parancsok után
                
            } finally {
                // MINDIG visszakapcsoljuk a külső kulcs ellenőrzést
                System.out.println("   → Külső kulcs ellenőrzés visszakapcsolása...");
                em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
                em.flush();
            }
            
            System.out.println("   ✅ SIKERES TÖRLÉS!");
            
            resp.put("status", "CourseDeleted");
            resp.put("statusCode", 200);
            resp.put("message", "Tanfolyam törölve");
            
        } catch (Exception e) {
            System.err.println("   ❌ HIBA:");
            e.printStackTrace();
            
            // Biztonság: külső kulcs ellenőrzés visszakapcsolása
            try {
                em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
                em.flush();
            } catch (Exception ignored) {}
            
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }
        
        return resp;
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // UPLOAD MATERIAL
    // ════════════════════════════════════════════════════════════════════════
    public JSONObject uploadMaterial(int courseId, int uploadedBy, String title, 
                                     java.io.InputStream fileStream, String originalFileName) {
        JSONObject resp = new JSONObject();

        try {
            Courses course = em.find(Courses.class, courseId);
            if (course == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Tanfolyam nem található");
                return resp;
            }

            if (!course.getInstructorId().equals(uploadedBy)) {
                resp.put("statusCode", 403);
                resp.put("message", "Csak a saját tanfolyamodhoz tölthetsz fel anyagot");
                return resp;
            }

            String uploadDir = "uploads/courses/" + courseId + "/";
            String basePath = System.getProperty("jboss.server.base.dir") + "/standalone/deployments/SkillBook.war/";
            String dirFull = basePath + uploadDir;
            Files.createDirectories(Paths.get(dirFull));

            String safeName = originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
            String uniqueName = UUID.randomUUID().toString().substring(0, 8) + "_" + safeName;
            String fullFilePath = dirFull + uniqueName;

            Files.copy(fileStream, Paths.get(fullFilePath), StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/courses/" + courseId + "/" + uniqueName;

            em.createNativeQuery(
                "INSERT INTO course_materials (course_id, title, file_url, uploaded_by, uploaded_at) " +
                "VALUES (:courseId, :title, :fileUrl, :uploadedBy, CURRENT_TIMESTAMP)"
            )
            .setParameter("courseId", courseId)
            .setParameter("title", title)
            .setParameter("fileUrl", fileUrl)
            .setParameter("uploadedBy", uploadedBy)
            .executeUpdate();

            resp.put("statusCode", 201);
            resp.put("status", "MaterialUploaded");
            resp.put("message", "Anyag sikeresen feltöltve");
            resp.put("fileUrl", fileUrl);

        } catch (IOException e) {
            resp.put("statusCode", 500);
            resp.put("message", "Fájl mentési hiba: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            resp.put("statusCode", 500);
            resp.put("message", "Adatbázis hiba: " + e.getMessage());
            e.printStackTrace();
        }

        return resp;
    }

    // ════════════════════════════════════════════════════════════════════════
    // FEJLÉC KÉP FELTÖLTÉSE - uploads mappába (mint a profilképek)
    // ════════════════════════════════════════════════════════════════════════
    public JSONObject uploadHeaderImage(int courseId, int userId, java.io.InputStream fileStream, String fileName) {
        JSONObject resp = new JSONObject();

        try {
            // Ellenőrizzük, hogy a tanfolyam létezik és a felhasználó az oktató
            Courses course = em.find(Courses.class, courseId);
            if (course == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Tanfolyam nem található");
                return resp;
            }

            if (!course.getInstructorId().equals(userId)) {
                resp.put("statusCode", 403);
                resp.put("message", "Csak a tanfolyam oktatója töltheti fel a fejléc képet");
                return resp;
            }

            // Fájl kiterjesztés
            String ext = "";
            if (fileName != null && fileName.contains(".")) {
                ext = fileName.substring(fileName.lastIndexOf("."));
            }

            // Egyedi fájlnév generálása
            String uniqueFileName = "course_header_" + courseId + "_" + System.currentTimeMillis() + ext;

            // Fájl mentési útvonal - uploads/course_headers könyvtárba (mint a profilképek)
            String uploadsDir = "C:/Users/Bagoly Donát/Desktop/SkillBook/server/wildfly-preview-26.1.1.Final/standalone/data/uploads/course_headers/";
            java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadsDir);

            // Könyvtár létrehozása, ha nem létezik
            if (!java.nio.file.Files.exists(uploadPath)) {
                java.nio.file.Files.createDirectories(uploadPath);
            }

            // Fájl mentése
            java.nio.file.Path filePath = uploadPath.resolve(uniqueFileName);
            java.nio.file.Files.copy(fileStream, filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // URL generálása - UploadsController-en keresztül
            String fileUrl = "/SkillBook/api/Uploads/course_headers/" + uniqueFileName;

            // Tanfolyam frissítése az új fejléc képpel
            course.setHeaderImage(fileUrl);
            em.merge(course);

            resp.put("statusCode", 200);
            resp.put("status", "HeaderImageUploaded");
            resp.put("message", "Fejléc kép sikeresen feltöltve");
            resp.put("headerImageUrl", fileUrl);

        } catch (IOException e) {
            resp.put("statusCode", 500);
            resp.put("message", "Fájl mentési hiba: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            resp.put("statusCode", 500);
            resp.put("message", "Adatbázis hiba: " + e.getMessage());
            e.printStackTrace();
        }

        return resp;
    }
}