package vn.java.demorestfulapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_group")
public class Group extends AbstractEntity<Integer> {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "group")
    private Set<GroupHasUser> groupHasUsers = new HashSet<>();
}