package vn.tayjava.demorestfulapi.controller;

import org.springframework.web.bind.annotation.*;
import vn.tayjava.demorestfulapi.dto.UserRequestDTO;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/")
    public String addUser(@RequestBody UserRequestDTO userDTO) {
        return "Add user";
    }

    @PutMapping("/{userId}")
    public String updateUser(@PathVariable int userId, @RequestBody UserRequestDTO userDTO) {
        System.out.println("Update user with id: " + userId);
        return "Update user";
    }

    @PatchMapping("/{userId}")
    public String changeStatus(@PathVariable int userId, @RequestParam boolean status) {
        System.out.println("Change status user with id: " + userId);
        return "Change status user";
    }
}
