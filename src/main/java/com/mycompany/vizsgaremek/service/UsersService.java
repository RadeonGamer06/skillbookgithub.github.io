package com.mycompany.vizsgaremek.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

@Stateless
public class UsersService{
@PersistenceContext(unitName = "com.mycompany_vizsgaremek_war_1.0-SNAPSHOTPU")
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
        
        // Paraméterek regisztrálása
        query.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("nameIN", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("emailIN", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("roleIN", String.class, ParameterMode.IN);
        
        // Paraméterek beállítása
        query.setParameter("userIdIN", userId);
        query.setParameter("nameIN", name);
        query.setParameter("emailIN", email);
        query.setParameter("roleIN", role);
        
        // Eljárás végrehajtása
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
        StoredProcedureQuery query = em.createStoredProcedureQuery("deleteUser");
        
        // Paraméter regisztrálása
        query.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
        
        // Paraméter beállítása
        query.setParameter("userIdIN", userId);
        
        // Eljárás végrehajtása
        query.execute();
        
        resp.put("status", "UserDeleted");
        resp.put("statusCode", 200);
        
    } catch (Exception e) {
        e.printStackTrace();
        resp.put("status", "DatabaseError");
        resp.put("message", e.getMessage());
        resp.put("statusCode", 500);
    }
    return resp;
}
    
}



