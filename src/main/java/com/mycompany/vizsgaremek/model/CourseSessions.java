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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Bagoly Donát
 */
@Entity
@Table(name = "course_sessions")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CourseSessions.findAll", query = "SELECT c FROM CourseSessions c"),
    @NamedQuery(name = "CourseSessions.findById", query = "SELECT c FROM CourseSessions c WHERE c.id = :id"),
    @NamedQuery(name = "CourseSessions.findByStartAt", query = "SELECT c FROM CourseSessions c WHERE c.startAt = :startAt"),
    @NamedQuery(name = "CourseSessions.findByEndAt", query = "SELECT c FROM CourseSessions c WHERE c.endAt = :endAt")})
public class CourseSessions implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "start_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startAt;
    @Column(name = "end_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endAt;
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    @ManyToOne
    private Courses courseId;
    @OneToMany(mappedBy = "sessionId")
    private Collection<Enrollments> enrollmentsCollection;

    public CourseSessions() {
    }

    public CourseSessions(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }

    public Courses getCourseId() {
        return courseId;
    }

    public void setCourseId(Courses courseId) {
        this.courseId = courseId;
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
        if (!(object instanceof CourseSessions)) {
            return false;
        }
        CourseSessions other = (CourseSessions) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.CourseSessions[ id=" + id + " ]";
    }
    
}
