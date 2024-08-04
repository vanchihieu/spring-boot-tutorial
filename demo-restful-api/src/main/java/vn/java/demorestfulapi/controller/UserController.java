package vn.java.demorestfulapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.java.demorestfulapi.configuration.Translator;
import vn.java.demorestfulapi.dto.request.UserRequestDTO;
import vn.java.demorestfulapi.dto.response.ResponseData;
import vn.java.demorestfulapi.dto.response.ResponseSuccess;

import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j
@Tag(name = "User Controller")
public class UserController {


    //    @RequestMapping(path = "/", method = RequestMethod.POST, headers = "apiKey=v1.0")
//    @Operation(summary = "Add user", description = "Add new user", responses = {
//            @ApiResponse(responseCode = "201", description = "Add user success",
//                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//                            examples = @ExampleObject(name = "ex name", summary = "ex summary",
//                                    value = """
//                                              {
//                                                   "status": 201,
//                                                     "message": "Add user success",
//                                                      "data": 1
//                                              }
//                                            """
//                            )
//                    )),
//            @ApiResponse(responseCode = "400", description = "Bad request")
//    })
    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    @PostMapping(value = "/")
    public ResponseData<Integer> addUser(@Valid @RequestBody UserRequestDTO userDTO) {
        log.info("Request add user, {} {}", userDTO.getFirstName(), userDTO.getLastName());
//        return new ResponseSuccess(HttpStatus.CREATED, "Add user success", userDTO);
        return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"), 1);
    }

    @Operation(summary = "Update user", description = "Send a request via this API to update user")
    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(@PathVariable int userId, @Valid @RequestBody UserRequestDTO userDTO) {
        System.out.println("Update user with id: " + userId);
        return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.upd.success"));
//        return new ResponseSuccess(HttpStatus.ACCEPTED, "Update user success");
    }
    @Operation(summary = "Change status of user", description = "Send a request via this API to change status of user")
    @PatchMapping("/{userId}")
    public ResponseData<Integer> changeStatus(@Min(value = 1, message = "userId must be greater than 0") @PathVariable int userId, @RequestParam(required = false) boolean status) {
        log.info("Request update userId={}", userId);
        return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.change.success"), userId);
//        return new ResponseSuccess(HttpStatus.ACCEPTED, "Change status user success");
    }
    @Operation(summary = "Delete user permanently", description = "Send a request via this API to delete user permanently")
    @DeleteMapping("/{userId}")
    public ResponseSuccess deleteUser(@PathVariable int userId) {
        System.out.println("Delete user with id: " + userId);
        return new ResponseSuccess(HttpStatus.NO_CONTENT, Translator.toLocale("user.del.success"));
    }

    @Operation(summary = "Get user detail", description = "Send a request via this API to get user information")
    @GetMapping("/{userId}")
    public ResponseData<UserRequestDTO> getUser(@PathVariable int userId) {
        UserRequestDTO userDTO = new UserRequestDTO();
        userDTO.setFirstName("Van");
        userDTO.setLastName("Chi Hieu");
        userDTO.setEmail("hieu@gmail.com");
        userDTO.setPhone("0123456789");

        log.info("Request get user detail, userId={}", userId);
        return new ResponseData<>(HttpStatus.OK.value(), "Get user success", userDTO);
//        return new ResponseSuccess(HttpStatus.OK, "Get user success", userDTO);
    }

    @Operation(summary = "Get list of users per pageNo", description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/list")
    public ResponseData<List<UserRequestDTO>> getAllUser(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize) {
        log.info("Request get all of users");

        return new ResponseData<>(HttpStatus.OK.value(), "Get all user success",
                List.of(
                        new UserRequestDTO("Van", "Chi Hieu", "hieu@gmail.com", "0123456789"),
                        new UserRequestDTO("Van", "Chi Tam", "tam@gmail.com", "0123456782")
                ));
    }
}
