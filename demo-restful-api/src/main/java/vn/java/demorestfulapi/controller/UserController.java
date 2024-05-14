package vn.java.demorestfulapi.controller;

import org.springframework.web.bind.annotation.*;
import vn.java.demorestfulapi.dto.UserRequestDTO;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping(value = "/")
//    @RequestMapping(path = "/", method = RequestMethod.POST, headers = "apiKey=v1.0")
    public String addUser(@RequestBody UserRequestDTO userDTO) {
        return "Add user";
    }

    @PutMapping("/{userId}")
    public String updateUser(@PathVariable int userId, @RequestBody UserRequestDTO userDTO) {
        System.out.println("Update user with id: " + userId);
        return "Update user";
    }

    @PatchMapping("/{userId}")
    public String changeStatus(@PathVariable int userId, @RequestParam(required = false) boolean status) {
        System.out.println("Change status user with id: " + userId);
        return "Change status user";
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable int userId) {
        System.out.println("Delete user with id: " + userId);
        return "Delete user";
    }

    @GetMapping("/{userId}")
    public UserRequestDTO getUser(@PathVariable int userId) {
        UserRequestDTO userDTO = new UserRequestDTO();
        userDTO.setFirstName("Van");
        userDTO.setLastName("Chi Hieu");
        userDTO.setEmail("hieu@gmail.com");
        userDTO.setPhone("0123456789");
        System.out.println("Get user with id: " + userId);
        return userDTO;
    }

    @GetMapping("/list")
    public List<UserRequestDTO> getAllUser(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        System.out.println("Get all user");
        return List.of(
                new UserRequestDTO("Van", "Chi Hieu", "hieu@gmail.com", "0123456789"),
                new UserRequestDTO("Van", "Chi Tam", "tam@gmail.com", "0123456782"));
    }
}
