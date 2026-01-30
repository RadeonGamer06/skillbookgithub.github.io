package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.security.JwtUtil;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

@Stateless
public class UsersService{
@PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    public JSONObject createUser(String username, String email, String password, String role) {
        JSONObject resp = new JSONObject();

        try {
            String Pw = BCrypt.hashpw(password, BCrypt.gensalt(12));

            if (role == null || role.trim().isEmpty()) {
                role = "customer";
            }

            StoredProcedureQuery query = em.createStoredProcedureQuery("createUser");

            query.registerStoredProcedureParameter("nameIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("emailIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("passwordIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("roleIN", String.class, ParameterMode.IN);

            query.setParameter("nameIN", username);
            query.setParameter("emailIN", email);
            query.setParameter("passwordIN", Pw);
            query.setParameter("roleIN", role);

            query.execute();

            resp.put("status", "UserCreated");
            resp.put("statusCode", 201);
            

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("status", "DatabaseError");
            resp.put("statusCode", 500);
        }

        return resp;
    }

    
    
    public JSONObject updateUser(int userId, String name, String email, String role) {
    JSONObject resp = new JSONObject();

    try {
        StoredProcedureQuery query = em.createStoredProcedureQuery("updateUser");

        query.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("nameIN", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("emailIN", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("roleIN", String.class, ParameterMode.IN);

        query.setParameter("userIdIN", userId);
        query.setParameter("nameIN", name);
        query.setParameter("emailIN", email);
        query.setParameter("roleIN", role);

        query.execute();

        resp.put("status", "UserUpdated");
        resp.put("statusCode", 200);

    } catch (Exception e) {
        e.printStackTrace();
        resp.put("status", "DatabaseError");
        resp.put("message", e.getMessage());
        resp.put("statusCode", 500);
    }

    return resp;
}

    
    
public JSONObject deleteUser(int userId) {
    JSONObject resp = new JSONObject();

    try {
        StoredProcedureQuery q = em.createStoredProcedureQuery("deleteUser");
        q.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        q.setParameter(1, userId);

        q.execute(); // vagy executeUpdate, de execute biztosabb SP-nél

        resp.put("status", "DeleteExecuted");
        resp.put("statusCode", 200);

    } catch (Exception e) {
        e.printStackTrace();
        resp.put("status", "DatabaseError");
        resp.put("message", e.getMessage());
        resp.put("statusCode", 500);
    }
    return resp;
}


public JSONObject getAllUsers() {
    JSONObject resp = new JSONObject();
    JSONArray arr = new JSONArray();

    try {
        StoredProcedureQuery sp = em.createStoredProcedureQuery("getAllUsers", Users.class);

        @SuppressWarnings("unchecked")
        List<Users> users = sp.getResultList();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Users u : users) {
            JSONObject o = new JSONObject();
            o.put("id", u.getId());
            o.put("name", u.getName());
            o.put("email", u.getEmail());
            o.put("role", u.getRole());

            // created_at (Date) → string, hogy Postmanben is szépen látszódjon
            o.put("created_at", u.getCreatedAt() != null ? sdf.format(u.getCreatedAt()) : JSONObject.NULL);

            // ⚠️ jelszót NE add vissza API-ban
            arr.put(o);
        }

        resp.put("status", "UsersFetched");
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


public JSONObject getUserById(int userId) {
    JSONObject resp = new JSONObject();
    try {
        StoredProcedureQuery sp = em.createStoredProcedureQuery("getUserById", Users.class);
        sp.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
        sp.setParameter("userIdIN", userId);

        Users user = (Users) sp.getSingleResult();

        JSONObject u = new JSONObject();
        u.put("id", user.getId());
        u.put("name", user.getName());
        u.put("email", user.getEmail());
        u.put("role", user.getRole());
        u.put("created_at", user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);

        resp.put("status", "UserFound");
        resp.put("statusCode", 200);
        resp.put("data", u);
    } catch (NoResultException e) {
        resp.put("status", "UserNotFound");
        resp.put("statusCode", 404);
        resp.put("message", "No user with id: " + userId);
    } catch (Exception e) {
        e.printStackTrace();
        resp.put("status", "DatabaseError");
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

        // BCrypt ellenőrzés (regisztrációnál)
        if (!BCrypt.checkpw(password, user.getPassword())) {
            resp.put("statusCode", 401);
            resp.put("message", "Hibás email vagy jelszó");
            return resp;
        }

        //JWT token
        String token = JwtUtil.generateToken(user.getId(), user.getEmail());

        JSONObject userJson = new JSONObject();
        userJson.put("id", user.getId());
        userJson.put("email", user.getEmail());
        userJson.put("name", user.getName());

        resp.put("statusCode", 200);
        resp.put("token", token);
        resp.put("user", userJson);

        return resp;

    } catch (NoResultException e) {
        resp.put("statusCode", 401);
        resp.put("message", "Hibás email vagy jelszó");
        return resp;

    } catch (Exception e) {
        e.printStackTrace();
        resp.put("statusCode", 500);
        resp.put("message", "Szerver hiba");
        return resp;
    }
}





// Saját felhasználó lekérése (token alapján)
public JSONObject getCurrentUser(int userId) {
    JSONObject resp = new JSONObject();
    try {
        JSONObject userData = getUserById(userId);
        if (userData.optInt("statusCode", 500) != 200) {
            return userData; // továbbadjuk a hibát
        }
        resp.put("status", "UserFound");
        resp.put("statusCode", 200);
        resp.put("data", userData.optJSONObject("data"));
    } catch (Exception e) {
        e.printStackTrace(); // ← logolja a szerver logba
        resp.put("statusCode", 500);
        resp.put("message", "Internal error: " + e.getMessage());
    }
    return resp;
}

// Profil frissítés – név, email, jelszó opcionálisan
public JSONObject updateProfile(int userId, String name, String email, String currentPassword, String newPassword) {
    JSONObject resp = new JSONObject();

    if (currentPassword == null || currentPassword.trim().isEmpty()) {
        resp.put("statusCode", 400);
        resp.put("message", "Jelenlegi jelszó kötelező a módosításhoz");
        return resp;
    }

    try {
        TypedQuery<Users> q = em.createQuery("SELECT u FROM Users u WHERE u.id = :id", Users.class);
        q.setParameter("id", userId);
        Users user;
        try {
            user = q.getSingleResult();
        } catch (NoResultException e) {
            resp.put("statusCode", 404);
            resp.put("message", "Felhasználó nem található");
            return resp;
        }

        if (!BCrypt.checkpw(currentPassword, user.getPassword())) {
            resp.put("statusCode", 401);
            resp.put("message", "Helytelen jelenlegi jelszó");
            return resp;
        }

        // Név és email frissítése
        StoredProcedureQuery query = em.createStoredProcedureQuery("updateUser");
        query.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("nameIN",   String.class,  ParameterMode.IN);
        query.registerStoredProcedureParameter("emailIN",  String.class,  ParameterMode.IN);
        query.registerStoredProcedureParameter("roleIN",   String.class,  ParameterMode.IN);

        query.setParameter("userIdIN", userId);
        query.setParameter("nameIN",   name != null && !name.trim().isEmpty() ? name.trim() : user.getName());
        query.setParameter("emailIN",  email != null && !email.trim().isEmpty() ? email.trim() : user.getEmail());
        query.setParameter("roleIN",   user.getRole());

        query.execute();

        // Jelszócsere csak akkor, ha megadták ÉS létezik a SP
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            String hashed = BCrypt.hashpw(newPassword.trim(), BCrypt.gensalt(12));
            try {
                StoredProcedureQuery pwQuery = em.createStoredProcedureQuery("updateUserPassword");
                pwQuery.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
                pwQuery.registerStoredProcedureParameter("passwordIN", String.class, ParameterMode.IN);
                pwQuery.setParameter("userIdIN", userId);
                pwQuery.setParameter("passwordIN", hashed);
                pwQuery.execute();
            } catch (Exception pwEx) {
                System.err.println("Jelszócsere SP hiba: " + pwEx.getMessage());
            }
        }

        resp.put("status", "ProfileUpdated");
        resp.put("statusCode", 200);
        resp.put("message", "Sikeresen módosítva");

    } catch (Exception e) {
        e.printStackTrace();
        resp.put("statusCode", 500);
        resp.put("message", "Szerveroldali hiba: " + e.getClass().getSimpleName() + " - " + e.getMessage());
    }

    return resp;
}
// Saját fiók törlése
public JSONObject deleteCurrentUser(int userId) {
    return deleteUser(userId);
}
    
}





