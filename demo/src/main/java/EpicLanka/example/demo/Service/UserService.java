package EpicLanka.example.demo.Service;

import EpicLanka.example.demo.Repository.UserRepository;
import EpicLanka.example.demo.entity.User;
import EpicLanka.example.demo.Service.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void signupUser(String userEmail, String userFirstname, String userLastname, String rawPassword) {
        User user = new User();
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        user.setFirstName(userFirstname);
        user.setLastName(userLastname);
        user.setEmail(userEmail);
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public String loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPassword())) {
                return jwtUtil.generateToken(email);
            }
        }
        return null;
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}