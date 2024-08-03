package vn.java.demorestfulapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.java.demorestfulapi.dto.request.UserRequestDTO;
import vn.java.demorestfulapi.dto.response.ResponseData;
import vn.java.demorestfulapi.dto.response.ResponseSuccess;

import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
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
    @PostMapping(value = "/")
    public ResponseData<Integer> addUser(@Valid @RequestBody UserRequestDTO userDTO) {
        System.out.println("request add user " + userDTO.getFirstName());
//        return new ResponseSuccess(HttpStatus.CREATED, "Add user success", userDTO);
        return new ResponseData<>(HttpStatus.CREATED.value(), "Add user success", 1);
    }


    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(@PathVariable int userId, @Valid @RequestBody UserRequestDTO userDTO) {
        System.out.println("Update user with id: " + userId);
        return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Update user success");
//        return new ResponseSuccess(HttpStatus.ACCEPTED, "Update user success");
    }

    @PatchMapping("/{userId}")
    public ResponseData<Integer> changeStatus(@Min(value = 1, message = "userId must be greater than 0") @PathVariable int userId, @RequestParam(required = false) boolean status) {
        System.out.println("Change status user with id: " + userId);
        return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Change status user success", userId);
//        return new ResponseSuccess(HttpStatus.ACCEPTED, "Change status user success");
    }

    @DeleteMapping("/{userId}")
    public ResponseSuccess deleteUser(@PathVariable int userId) {
        System.out.println("Delete user with id: " + userId);
        return new ResponseSuccess(HttpStatus.NO_CONTENT, "Delete user success");
    }

    @GetMapping("/{userId}")
    public ResponseData<UserRequestDTO> getUser(@PathVariable int userId) {
        UserRequestDTO userDTO = new UserRequestDTO();
        userDTO.setFirstName("Van");
        userDTO.setLastName("Chi Hieu");
        userDTO.setEmail("hieu@gmail.com");
        userDTO.setPhone("0123456789");
        System.out.println("Get user with id: " + userId);
        return new ResponseData<>(HttpStatus.OK.value(), "Get user success", userDTO);
//        return new ResponseSuccess(HttpStatus.OK, "Get user success", userDTO);
    }

    @GetMapping("/list")
    public ResponseData<List<UserRequestDTO>> getAllUser(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize) {
        System.out.println("Get all user");

        return new ResponseData<>(HttpStatus.OK.value(), "Get all user success",
                List.of(
                        new UserRequestDTO("Van", "Chi Hieu", "hieu@gmail.com", "0123456789"),
                        new UserRequestDTO("Van", "Chi Tam", "tam@gmail.com", "0123456782")
                ));
    }
}
