package vn.java.demorestfulapi.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import vn.java.demorestfulapi.dto.validator.EnumPattern;
import vn.java.demorestfulapi.dto.validator.EnumValue;
import vn.java.demorestfulapi.dto.validator.GenderSubset;
import vn.java.demorestfulapi.dto.validator.PhoneNumber;
import vn.java.demorestfulapi.util.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class UserRequestDTO implements Serializable {
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotNull(message = "Last name is required")
    private String lastName;
    @Email(message = "Email is invalid")
    private String email;
//    @Pattern(regexp = "^\\d{9,10}$", message = "Phone is invalid")
    @PhoneNumber
    private String phone;

    @NotNull(message = "Date of birth is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date dateOfBirth;

    /**
     * – Ưu điểm của phương pháp này là chúng ta có thể áp dụng chung cho tất cả các enum khác nhau:
     */
    @EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;

    /**
     * – Ưu điểm của phương pháp này là chúng ta có thể chỉ định cụ thể một vài giá trị cần validate trong enum thay vì tất cả.
     */
    @GenderSubset(anyOf = {Gender.MALE, Gender.FEMALE, Gender.OTHER})
    private Gender gender;

    /**
     * – Ưu điểm của phương pháp này là có thể áp dụng chung cho tất cả enum và dễ dàng handle exception.
     */
    @NotNull(message = "type must be not null")
    @EnumValue(name = "type", enumClass = UserType.class)
    private String type;

    @NotNull(message = "username must be not null")
    private String username;

    @NotNull(message = "password must be not null")
    private String password;

    @NotEmpty(message = "Address must be not empty")
    private Set<Address> addresses;
    public UserRequestDTO() {
    }

    public UserRequestDTO(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }



    @Getter
    public static class Address {
        private String apartmentNumber;
        private String floor;
        private String building;
        private String streetNumber;
        private String street;
        private String city;
        private String country;
        private Integer addressType;


    }
}
