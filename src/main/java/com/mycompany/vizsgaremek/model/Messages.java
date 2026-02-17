package com.mycompany.vizsgaremek.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // sender_id FK → users.id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    private Users sender;

    // receiver_id FK → users.id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Users receiver;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // Az adatbázisban "sentAt" az oszlop neve (DATETIME DEFAULT CURRENT_TIMESTAMP)
    @Column(name = "sentAt")
    private LocalDateTime sentAt;

    public Messages() {
        this.sentAt = LocalDateTime.now();
    }

    public Messages(Users sender, Users receiver, String content) {
        this.sender   = sender;
        this.receiver = receiver;
        this.content  = content;
        this.sentAt   = LocalDateTime.now();
    }

    // ════════════════════════════════════════════════════════════════
    // GETTEREK
    // ════════════════════════════════════════════════════════════════
    public int getId()              { return id; }
    public Users getSender()        { return sender; }
    public Users getReceiver()      { return receiver; }
    public String getContent()      { return content; }
    public LocalDateTime getSentAt(){ return sentAt; }
}