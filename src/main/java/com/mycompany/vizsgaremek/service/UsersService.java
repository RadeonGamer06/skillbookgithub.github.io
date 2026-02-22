package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.security.JwtUtil;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UsersService {

    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    @Inject
    private EmailService emailService;

    private static final String UPLOAD_DIR = 
        "C:\\Users\\Bagoly Donát\\Desktop\\SkillBook\\server\\wildfly-preview-26.1.1.Final\\standalone\\data\\uploads\\profile_pictures";
    
    private static final String URL_PREFIX = "/SkillBook/api/Uploads/profile_pictures/";

   
    public JSONObject createUser(String name, String email, String password, String role) {
        JSONObject resp = new JSONObject();

        try {
            System.out.println("📝 USER LÉTREHOZÁS:");
            System.out.println("   Név: " + name);
            System.out.println("   Email: " + email);
            System.out.println("   Role: " + role);

            String hashedPw = BCrypt.hashpw(password, BCrypt.gensalt(12));
            
            if (role == null || role.trim().isEmpty()) {
                role = "student";
            }

            StoredProcedureQuery query = em.createStoredProcedureQuery("createUser");
            query.registerStoredProcedureParameter("nameIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("emailIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("passwordIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("roleIN", String.class, ParameterMode.IN);

            query.setParameter("nameIN", name);
            query.setParameter("emailIN", email);
            query.setParameter("passwordIN", hashedPw);
            query.setParameter("roleIN", role);

            query.execute();

            System.out.println("✅ User sikeresen létrehozva");

            try {
                emailService.sendWelcomeEmail(name, email);
                System.out.println("✅ Üdvözlő email elküldve");
            } catch (Exception ex) {
                System.err.println("⚠️ Email hiba (nem blokkoló): " + ex.getMessage());
            }

            resp.put("status", "UserCreated");
            resp.put("statusCode", 201);
            resp.put("message", "Sikeres regisztráció!");

        } catch (Exception e) {
            e.printStackTrace();
            
            String errorMsg = e.getMessage();
            if (errorMsg != null && 
                (errorMsg.contains("Duplicate") || errorMsg.contains("unique"))) {
                
                if (errorMsg.toLowerCase().contains("email")) {
                    resp.put("message", "Ez az email cím már foglalt!");
                } else {
                    resp.put("message", "Ez a név vagy email már használatban van!");
                }
                resp.put("statusCode", 409);
            } else {
                resp.put("message", "Hiba történt: " + errorMsg);
                resp.put("statusCode", 500);
            }
        }

        return resp;
    }

    public JSONObject updateProfilePicture(Integer userId, InputStream fileInputStream, String fileName) {
        JSONObject resp = new JSONObject();

        try {
            System.out.println("📷 PROFILKÉP FELTÖLTÉS:");
            System.out.println("   User ID: " + userId);
            System.out.println("   Fájlnév: " + fileName);

            Users user = em.find(Users.class, userId);
            if (user == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Felhasználó nem található");
                return resp;
            }

            if (fileInputStream == null || fileName == null) {
                resp.put("statusCode", 400);
                resp.put("message", "Hiányzó fájl");
                return resp;
            }

            String profilePicUrl = saveProfilePicture(fileInputStream, fileName, userId);

            if (profilePicUrl == null) {
                resp.put("statusCode", 500);
                resp.put("message", "Fájl mentési hiba");
                return resp;
            }

            try {
                StoredProcedureQuery picQuery = em.createStoredProcedureQuery("updateProfilePicture");
                picQuery.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
                picQuery.registerStoredProcedureParameter("pictureIN", String.class, ParameterMode.IN);
                picQuery.setParameter("userIdIN", userId);
                picQuery.setParameter("pictureIN", profilePicUrl);
                picQuery.execute();
                
                System.out.println("✅ Profilkép sikeresen frissítve: " + profilePicUrl);

                try {
                    emailService.sendProfilePictureChangeEmail(user.getName(), user.getEmail());
                    System.out.println("✅ Profilkép módosítási email elküldve");
                } catch (Exception emailEx) {
                    System.err.println("⚠️ Profilkép email hiba (nem blokkoló): " + emailEx.getMessage());
                }

                resp.put("statusCode", 200);
                resp.put("message", "Profilkép sikeresen feltöltve");
                resp.put("profilePicture", profilePicUrl);

            } catch (Exception dbEx) {
                System.err.println("⚠️ ADATBÁZIS HIBA:");
                System.err.println("   Az 'updateProfilePicture' stored procedure nem létezik!");
                System.err.println("   Futtasd a következő SQL-t:");
                System.err.println("");
                System.err.println("   ALTER TABLE users ADD COLUMN profile_picture VARCHAR(500);");
                System.err.println("");
                System.err.println("   DELIMITER $$");
                System.err.println("   CREATE PROCEDURE updateProfilePicture(");
                System.err.println("       IN userIdIN INT,");
                System.err.println("       IN pictureIN VARCHAR(500)");
                System.err.println("   )");
                System.err.println("   BEGIN");
                System.err.println("       UPDATE users SET profile_picture = pictureIN WHERE id = userIdIN;");
                System.err.println("   END$$");
                System.err.println("   DELIMITER ;");
                System.err.println("");
                
                dbEx.printStackTrace();
                
                resp.put("statusCode", 500);
                resp.put("message", "Adatbázis hiba - a profilkép funkció még nincs támogatva. Futtasd az SQL migrációt!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Váratlan hiba: " + e.getMessage());
        }

        return resp;
    }
    
    private String saveProfilePicture(InputStream fileInputStream, String fileName, Integer userId) {
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    System.err.println("❌ Nem sikerült létrehozni: " + UPLOAD_DIR);
                    return null;
                }
                System.out.println("✅ Könyvtár létrehozva: " + UPLOAD_DIR);
            }

            String extension = "";
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                extension = fileName.substring(dotIndex);
            }

            String uniqueFileName = "profile_" + userId + "_" + UUID.randomUUID().toString() + extension;
            File targetFile = new File(uploadDir, uniqueFileName);

            Files.copy(fileInputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("✅ Fájl mentve: " + targetFile.getAbsolutePath());

            return URL_PREFIX + uniqueFileName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject updateUser(int id, String name, String email, String role) {
        JSONObject resp = new JSONObject();

        try {
            Users existingUser = em.find(Users.class, id);
            if (existingUser == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Felhasználó nem található");
                return resp;
            }

            String finalName = (name != null && !name.trim().isEmpty()) ? name : existingUser.getName();
            String finalEmail = (email != null && !email.trim().isEmpty()) ? email : existingUser.getEmail();
            String finalRole = (role != null && !role.trim().isEmpty()) ? role : existingUser.getRole();

            StoredProcedureQuery query = em.createStoredProcedureQuery("updateUser");
            query.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("nameIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("emailIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("roleIN", String.class, ParameterMode.IN);

            query.setParameter("userIdIN", id);
            query.setParameter("nameIN", finalName);
            query.setParameter("emailIN", finalEmail);
            query.setParameter("roleIN", finalRole);

            query.execute();

            resp.put("status", "UserUpdated");
            resp.put("statusCode", 200);
            resp.put("message", "Adatok sikeresen frissítve");

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba történt: " + e.getMessage());
        }

        return resp;
    }

    public JSONObject deleteUser(int userId) {
        JSONObject resp = new JSONObject();

        try {
            Users user = em.find(Users.class, userId);
            if (user == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Felhasználó nem található");
                return resp;
            }

            String userName  = user.getName();
            String userEmail = user.getEmail();

            StoredProcedureQuery query = em.createStoredProcedureQuery("deleteUser");
            query.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            query.setParameter("userIdIN", userId);
            query.execute();

            try {
                emailService.sendAccountDeletedEmail(userName, userEmail);
                System.out.println("✅ Fiók törlési email elküldve: " + userEmail);
            } catch (Exception emailEx) {
                System.err.println("⚠️ Törlési email hiba (nem blokkoló): " + emailEx.getMessage());
            }

            resp.put("status", "UserDeleted");
            resp.put("statusCode", 200);
            resp.put("message", "Felhasználó sikeresen törölve");

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba történt: " + e.getMessage());
        }

        return resp;
    }

    public JSONObject getUserById(int id) {
        JSONObject resp = new JSONObject();

        try {
            Users user = em.find(Users.class, id);

            if (user != null) {
                JSONObject userJson = new JSONObject();
                userJson.put("id", user.getId());
                userJson.put("name", user.getName());
                userJson.put("email", user.getEmail());
                userJson.put("role", user.getRole());
                userJson.put("createdAt", user.getCreatedAt());
                
                if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                    userJson.put("profilePicture", user.getProfilePicture());
                }

                resp.put("statusCode", 200);
                resp.put("data", userJson);
            } else {
                resp.put("statusCode", 404);
                resp.put("message", "User not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", e.getMessage());
        }

        return resp;
    }

    public JSONObject login(String email, String password) {
        JSONObject resp = new JSONObject();

        try {
            TypedQuery<Users> q = em.createQuery(
                "SELECT u FROM Users u WHERE u.email = :email", Users.class
            );
            q.setParameter("email", email);

            Users user = q.getSingleResult();

            if (!BCrypt.checkpw(password, user.getPassword())) {
                resp.put("statusCode", 401);
                resp.put("message", "Hibás email vagy jelszó");
                return resp;
            }

            String token = JwtUtil.generateToken(user.getId(), user.getEmail());

            JSONObject userJson = new JSONObject();
            userJson.put("id", user.getId());
            userJson.put("email", user.getEmail());
            userJson.put("name", user.getName());
            userJson.put("role", user.getRole());
            
            if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                userJson.put("profilePicture", user.getProfilePicture());
            }

            resp.put("statusCode", 200);
            resp.put("token", token);
            resp.put("user", userJson);

        } catch (NoResultException e) {
            resp.put("statusCode", 401);
            resp.put("message", "Hibás email vagy jelszó");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Szerver hiba: " + e.getMessage());
        }

        return resp;
    }

    public JSONObject getCurrentUser(Integer userId) {
        return getUserById(userId.intValue());
    }

    public JSONObject updateProfile(
            Integer userId, String name, String email,
            String currentPassword, String newPassword) {

        JSONObject resp = new JSONObject();

        try {
            Users user = em.find(Users.class, userId);
            if (user == null) {
                resp.put("statusCode", 404);
                resp.put("message", "Felhasználó nem található");
                return resp;
            }

            if (!BCrypt.checkpw(currentPassword, user.getPassword())) {
                resp.put("statusCode", 401);
                resp.put("message", "Hibás jelenlegi jelszó");
                return resp;
            }

            String oldName  = user.getName();
            String oldEmail = user.getEmail();

            String newNameValue  = name  != null && !name.trim().isEmpty()  ? name.trim()  : oldName;
            String newEmailValue = email != null && !email.trim().isEmpty() ? email.trim() : oldEmail;

            StoredProcedureQuery query = em.createStoredProcedureQuery("updateUser");
            query.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("nameIN",   String.class,  ParameterMode.IN);
            query.registerStoredProcedureParameter("emailIN",  String.class,  ParameterMode.IN);
            query.registerStoredProcedureParameter("roleIN",   String.class,  ParameterMode.IN);

            query.setParameter("userIdIN", userId);
            query.setParameter("nameIN",   newNameValue);
            query.setParameter("emailIN",  newEmailValue);
            query.setParameter("roleIN",   user.getRole());

            query.execute();

            boolean passwordChanged = false;
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                String hashed = BCrypt.hashpw(newPassword.trim(), BCrypt.gensalt(12));
                StoredProcedureQuery pwQuery = em.createStoredProcedureQuery("updateUserPassword");
                pwQuery.registerStoredProcedureParameter("userIdIN",  Integer.class, ParameterMode.IN);
                pwQuery.registerStoredProcedureParameter("passwordIN", String.class, ParameterMode.IN);
                pwQuery.setParameter("userIdIN",  userId);
                pwQuery.setParameter("passwordIN", hashed);
                pwQuery.execute();
                passwordChanged = true;
            }

            boolean nameChanged  = !newNameValue.equals(oldName);
            boolean emailChanged = !newEmailValue.equals(oldEmail);

            if (nameChanged || emailChanged || passwordChanged) {
                try {
                    emailService.sendProfileChangeEmail(
                        newNameValue, emailChanged ? oldEmail : newEmailValue,
                        nameChanged,  oldName,  newNameValue,
                        emailChanged, oldEmail, newEmailValue,
                        passwordChanged
                    );

                    if (emailChanged) {
                        emailService.sendEmailChangeConfirmation(newNameValue, newEmailValue, oldEmail);
                    }

                    System.out.println("✅ Profil módosítási email(ek) elküldve");
                } catch (Exception emailEx) {
                    System.err.println("⚠️ Profil email hiba (nem blokkoló): " + emailEx.getMessage());
                }
            }

            resp.put("status", "ProfileUpdated");
            resp.put("statusCode", 200);
            resp.put("message", "Sikeresen módosítva");

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba: " + e.getMessage());
        }

        return resp;
    }

    public JSONObject deleteCurrentUser(int userId) {
        return deleteUser(userId);
    }

    public JSONObject forgotPassword(String email) {
        JSONObject resp = new JSONObject();

        try {
            TypedQuery<Users> q = em.createQuery(
                "SELECT u FROM Users u WHERE u.email = :email", Users.class);
            q.setParameter("email", email.trim().toLowerCase());

            Users user;
            try {
                user = q.getSingleResult();
            } catch (NoResultException e) {
                resp.put("statusCode", 404);
                resp.put("message", "Nem található felhasználó ezzel az email címmel.");
                return resp;
            }

            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[9];
            random.nextBytes(bytes);
            String tempPassword = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(bytes)
                    .replaceAll("[^a-zA-Z0-9]", "")
                    .substring(0, 12);

            String hashedTemp = BCrypt.hashpw(tempPassword, BCrypt.gensalt(12));

            StoredProcedureQuery pwQuery = em.createStoredProcedureQuery("updateUserPassword");
            pwQuery.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            pwQuery.registerStoredProcedureParameter("passwordIN", String.class, ParameterMode.IN);
            pwQuery.setParameter("userIdIN", user.getId());
            pwQuery.setParameter("passwordIN", hashedTemp);
            pwQuery.execute();

            boolean emailSent = emailService.sendForgotPasswordEmail(
                    user.getName(),
                    user.getEmail(),
                    tempPassword
            );

            if (emailSent) {
                resp.put("statusCode", 200);
                resp.put("message", "Az egyszer használatos jelszót elküldtük.");
            } else {
                resp.put("statusCode", 500);
                resp.put("message", "Email küldési hiba.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Szerver hiba: " + e.getMessage());
        }

        return resp;
    }
    
    public static File getProfilePictureFile(String filename) {
        return new File(UPLOAD_DIR, filename);
    }
}