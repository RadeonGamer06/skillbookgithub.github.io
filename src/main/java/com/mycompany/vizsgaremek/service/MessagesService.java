package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Messages;
import com.mycompany.vizsgaremek.model.Users;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class MessagesService {

    @PersistenceContext(unitName = "SkillBook")
    private EntityManager em;

    public void sendMessage(Users sender, Users receiver, String content) {
        Messages msg = new Messages(sender, receiver, content);
        em.persist(msg);
    }

    public List<Messages> getChat(int userA, int userB) {
        return em.createQuery(
                "SELECT m FROM Messages m " +
                "WHERE (m.sender.id = :a AND m.receiver.id = :b) " +
                "   OR (m.sender.id = :b AND m.receiver.id = :a) " +
                "ORDER BY m.sentAt ASC",
                Messages.class)
                .setParameter("a", userA)
                .setParameter("b", userB)
                .getResultList();
    }
}