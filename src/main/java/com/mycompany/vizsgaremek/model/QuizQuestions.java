/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.model;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Bagoly Donát
 */
@Entity
@Table(name = "quiz_questions")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "QuizQuestions.findAll", query = "SELECT q FROM QuizQuestions q"),
    @NamedQuery(name = "QuizQuestions.findById", query = "SELECT q FROM QuizQuestions q WHERE q.id = :id"),
    @NamedQuery(name = "QuizQuestions.findByCorrectAnswer", query = "SELECT q FROM QuizQuestions q WHERE q.correctAnswer = :correctAnswer")})
public class QuizQuestions implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Lob
    @Size(max = 65535)
    @Column(name = "question")
    private String question;
    @Size(max = 255)
    @Column(name = "correct_answer")
    private String correctAnswer;
    @JoinColumn(name = "quiz_id", referencedColumnName = "id")
    @ManyToOne
    private Quizzes quizId;

    public QuizQuestions() {
    }

    public QuizQuestions(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Quizzes getQuizId() {
        return quizId;
    }

    public void setQuizId(Quizzes quizId) {
        this.quizId = quizId;
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
        if (!(object instanceof QuizQuestions)) {
            return false;
        }
        QuizQuestions other = (QuizQuestions) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.QuizQuestions[ id=" + id + " ]";
    }
    
}
