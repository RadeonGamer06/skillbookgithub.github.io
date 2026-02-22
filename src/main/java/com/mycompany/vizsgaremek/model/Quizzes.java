/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
import java.util.Collection;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Bagoly Donát
 */
@Entity
@Table(name = "quizzes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Quizzes.findAll", query = "SELECT q FROM Quizzes q"),
    @NamedQuery(name = "Quizzes.findById", query = "SELECT q FROM Quizzes q WHERE q.id = :id"),
    @NamedQuery(name = "Quizzes.findByTitle", query = "SELECT q FROM Quizzes q WHERE q.title = :title")})
public class Quizzes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 255)
    @Column(name = "title")
    private String title;
    @OneToMany(mappedBy = "quizId")
    private Collection<QuizQuestions> quizQuestionsCollection;
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    @ManyToOne
    private Courses courseId;

    public Quizzes() {
    }

    public Quizzes(Integer id) {
        this.id = id;
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

    @XmlTransient
    public Collection<QuizQuestions> getQuizQuestionsCollection() {
        return quizQuestionsCollection;
    }

    public void setQuizQuestionsCollection(Collection<QuizQuestions> quizQuestionsCollection) {
        this.quizQuestionsCollection = quizQuestionsCollection;
    }

    public Courses getCourseId() {
        return courseId;
    }

    public void setCourseId(Courses courseId) {
        this.courseId = courseId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Quizzes)) {
            return false;
        }
        Quizzes other = (Quizzes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.Quizzes[ id=" + id + " ]";
    }
    
}
