package vn.java.demorestfulapi.model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_token")
public class Token extends AbstractEntity<Integer> {

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

}