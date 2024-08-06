package vn.java.demorestfulapi.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.java.demorestfulapi.util.Gender;
import vn.java.demorestfulapi.util.UserStatus;

import java.io.Serializable;
import java.util.Date;

@Builder // dùng để khởi tạo một đối tượng bằng cách sử dụng các tham số được truyền vào constructor của nó (constructor arguments) và các giá trị mặc định (default values) của các trường (fields) trong class
@Getter
@Setter
public class UserDetailResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;

    public UserDetailResponse(Long id, String firstName, String lastName, String phone, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }
}