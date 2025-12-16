/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Bagoly Donát
 */
@Entity
@Table(name = "courses")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Courses.findAll", query = "SELECT c FROM Courses c"),
    @NamedQuery(name = "Courses.findById", query = "SELECT c FROM Courses c WHERE c.id = :id"),
    @NamedQuery(name = "Courses.findByTitle", query = "SELECT c FROM Courses c WHERE c.title = :title"),
    @NamedQuery(name = "Courses.findByPrice", query = "SELECT c FROM Courses c WHERE c.price = :price"),
    @NamedQuery(name = "Courses.findByMaxParticipants", query = "SELECT c FROM Courses c WHERE c.maxParticipants = :maxParticipants"),
    @NamedQuery(name = "Courses.findByCreatedAt", query = "SELECT c FROM Courses c WHERE c.createdAt = :createdAt")})
public class Courses implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 255)
    @Column(name = "title")
    private String title;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private Integer price;
    @Column(name = "max_participants")
    private Integer maxParticipants;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinColumn(name = "instructor_id", referencedColumnName = "id")
    @ManyToOne
    private Users instructorId;
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @ManyToOne
    private Categories categoryId;
    @OneToMany(mappedBy = "courseId")
    private Collection<Messages> messagesCollection;
    @OneToMany(mappedBy = "courseId")
    private Collection<CourseMaterials> courseMaterialsCollection;
    @OneToMany(mappedBy = "courseId")
    private Collection<Quizzes> quizzesCollection;
    @OneToMany(mappedBy = "courseId")
    private Collection<CourseSessions> courseSessionsCollection;
    @OneToMany(mappedBy = "courseId")
    private Collection<Enrollments> enrollmentsCollection;
    
    

    public Courses() {
    }

    public Courses(Integer id) {
        this.id = id;
    }

    public Courses(Integer id, Date createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }
    

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Users getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Users instructorId) {
        this.instructorId = instructorId;
    }

    public Categories getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Categories categoryId) {
        this.categoryId = categoryId;
    }

    @XmlTransient
    public Collection<Messages> getMessagesCollection() {
        return messagesCollection;
    }

    public void setMessagesCollection(Collection<Messages> messagesCollection) {
        this.messagesCollection = messagesCollection;
    }

    @XmlTransient
    public Collection<CourseMaterials> getCourseMaterialsCollection() {
        return courseMaterialsCollection;
    }

    public void setCourseMaterialsCollection(Collection<CourseMaterials> courseMaterialsCollection) {
        this.courseMaterialsCollection = courseMaterialsCollection;
    }

    @XmlTransient
    public Collection<Quizzes> getQuizzesCollection() {
        return quizzesCollection;
    }

    public void setQuizzesCollection(Collection<Quizzes> quizzesCollection) {
        this.quizzesCollection = quizzesCollection;
    }

    @XmlTransient
    public Collection<CourseSessions> getCourseSessionsCollection() {
        return courseSessionsCollection;
    }

    public void setCourseSessionsCollection(Collection<CourseSessions> courseSessionsCollection) {
        this.courseSessionsCollection = courseSessionsCollection;
    }

    @XmlTransient
    public Collection<Enrollments> getEnrollmentsCollection() {
        return enrollmentsCollection;
    }

    public void setEnrollmentsCollection(Collection<Enrollments> enrollmentsCollection) {
        this.enrollmentsCollection = enrollmentsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Courses)) {
            return false;
        }
        Courses other = (Courses) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Courses[ id=" + id + " ]";
    }
    
}
