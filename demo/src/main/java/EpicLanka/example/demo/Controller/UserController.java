package EpicLanka.example.demo.Controller;

import EpicLanka.example.demo.Repository.UserRepository;
import EpicLanka.example.demo.Service.UserService;
import EpicLanka.example.demo.dto.loginDto;
import EpicLanka.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")  // ✅ Fixed URL mapping
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody User newUser, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        // Check for validation errors
        if (result.hasErrors()) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

        // Check for null values (additional validation)
        if (newUser.getEmail() == null || newUser.getFirstName() == null ||
                newUser.getLastName() == null || newUser.getPassword() == null) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

        // Check if user already exists
        if (userService.userExists(newUser.getEmail())) {
            response.put("responseCode", "04");
            response.put("responseMsg", "User Already Exists");
            response.put("content", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        // Create new user
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
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody loginDto user, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        // Check for validation errors
        if (result.hasErrors()) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

        // Check for null values
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