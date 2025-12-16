package com.mycompany.vizsgaremek.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "course_reviews")
public class CourseReviews implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Courses courseId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId;

    @Column(nullable = false)
    private Integer rating; // 1-5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    // Konstruktorok, getterek, setterek
    public CourseReviews() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Courses getCourseId() { return courseId; }
    public void setCourseId(Courses courseId) { this.courseId = courseId; }

    public Users getUserId() { return userId; }
    public void setUserId(Users userId) { this.userId = userId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Date getCreatedAt() { return createdAt; }
}