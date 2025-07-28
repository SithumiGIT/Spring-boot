package EpicLanka.example.demo.Controller;

import EpicLanka.example.demo.Repository.UserRepository;
import EpicLanka.example.demo.Service.UserService;
import EpicLanka.example.demo.dto.loginDto;
import EpicLanka.example.demo.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Authentication", description = "APIs for user registration and authentication")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    @Operation(
            summary = "User Registration",
            description = "Register a new user account in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid user data")
    })
    public ResponseEntity<Map<String, Object>> signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "email": "user@example.com",
                                        "firstName": "John",
                                        "lastName": "Doe",
                                        "password": "securePassword123"
                                    }
                                    """)
                    )
            )
            @Valid @RequestBody User newUser, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

        if (newUser.getEmail() == null || newUser.getFirstName() == null ||
                newUser.getLastName() == null || newUser.getPassword() == null) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.userExists(newUser.getEmail())) {
            response.put("responseCode", "04");
            response.put("responseMsg", "User Already Exists");
            response.put("content", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        try {
            userService.signupUser(newUser.getEmail(), newUser.getFirstName(),
                    newUser.getLastName(), newUser.getPassword());
            response.put("responseCode", "00");
            response.put("responseMsg", "Success");
            response.put("content", null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    @Operation(
            summary = "User Login",
            description = "Authenticate user and receive JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "responseCode": "00",
                                        "responseMsg": "Success",
                                        "content": {
                                            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                        }
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = loginDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "email": "user@example.com",
                                        "password": "securePassword123"
                                    }
                                    """)
                    )
            )
            @Valid @RequestBody loginDto user, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

        if (user.getEmail() == null || user.getPassword() == null) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

        if (!userRepository.existsByEmail(user.getEmail())) {
            response.put("responseCode", "02");
            response.put("responseMsg", "No such user exists!");
            response.put("content", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = userService.loginUser(user.getEmail(), user.getPassword());

        if (token != null) {
            Map<String, String> content = new HashMap<>();
            content.put("token", token);

            response.put("responseCode", "00");
            response.put("responseMsg", "Success");
            response.put("content", content);
            return ResponseEntity.ok().body(response);
        } else {
            response.put("responseCode", "03");
            response.put("responseMsg", "Invalid Credentials");
            response.put("content", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}