package vn.java.demorestfulapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
// This annotation is used to declare that a class is an entity class that should be mapped to a table in the database. It is used to annotate a superclass of a entity class. It is used to inherit properties from a superclass.
public abstract class AbstractEntity<T extends Serializable> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    T id;

//    @CreatedBy
//    @Column(name = "create_by")
//    T createBy;
//
//    @LastModifiedBy
//    @Column(name = "updated_by")
//    T updatedBy;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;
}