package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Categories;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CategoriesService {

    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    public JSONObject getAllCategories() {
        JSONObject resp = new JSONObject();
        JSONArray arr = new JSONArray();

        try {
            List<Categories> categories = em.createQuery(
                    "SELECT c FROM Categories c ORDER BY c.name", Categories.class
            ).getResultList();

            for (Categories c : categories) {
                JSONObject o = new JSONObject();
                o.put("id", c.getId());
                o.put("name", c.getName());
                o.put("slug", c.getSlug());
                arr.put(o);
            }

            resp.put("statusCode", 200);
            resp.put("data", arr);

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("statusCode", 500);
            resp.put("message", "Hiba a kategóriák lekérésekor");
        }

        return resp;
    }
}
