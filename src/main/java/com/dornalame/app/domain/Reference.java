package com.dornalame.app.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Reference.
 */
@Entity
@Table(name = "reference")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "reference")
public class Reference implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "ref_name", nullable = false)
    private String ref_name;

    @Column(name = "ref_url")
    private String ref_url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRef_name() {
        return ref_name;
    }

    public void setRef_name(String ref_name) {
        this.ref_name = ref_name;
    }

    public String getRef_url() {
        return ref_url;
    }

    public void setRef_url(String ref_url) {
        this.ref_url = ref_url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reference reference = (Reference) o;
        if(reference.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, reference.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Reference{" +
            "id=" + id +
            ", ref_name='" + ref_name + "'" +
            ", ref_url='" + ref_url + "'" +
            '}';
    }
}
