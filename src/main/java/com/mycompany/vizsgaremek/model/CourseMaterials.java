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
@Table(name = "course_materials")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CourseMaterials.findAll", query = "SELECT c FROM CourseMaterials c"),
    @NamedQuery(name = "CourseMaterials.findById", query = "SELECT c FROM CourseMaterials c WHERE c.id = :id"),
    @NamedQuery(name = "CourseMaterials.findByTitle", query = "SELECT c FROM CourseMaterials c WHERE c.title = :title"),
    @NamedQuery(name = "CourseMaterials.findByFileUrl", query = "SELECT c FROM CourseMaterials c WHERE c.fileUrl = :fileUrl")})
public class CourseMaterials implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 255)
    @Column(name = "title")
    private String title;
    @Size(max = 255)
    @Column(name = "file_url")
    private String fileUrl;
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    @ManyToOne
    private Courses courseId;

    public CourseMaterials() {
    }

    public CourseMaterials(Integer id) {
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CourseMaterials)) {
            return false;
        }
        CourseMaterials other = (CourseMaterials) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.vizsgaremek.model.CourseMaterials[ id=" + id + " ]";
    }
    
}
