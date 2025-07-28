package EpicLanka.example.demo.Controller;

import EpicLanka.example.demo.Repository.MovieRepository;
import EpicLanka.example.demo.Service.MovieService;
import EpicLanka.example.demo.entity.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieService movieService;

    // Helper method to check if user is authenticated
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser");
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMovies() {
        Map<String, Object> response = new HashMap<>();

        // Check authentication
        if (!isAuthenticated()) {
            response.put("responseCode", "03");
            response.put("responseMsg", "Not Authorized");
            response.put("content", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            List<Movie> movies = movieService.getAllMovies();

            if (movies.isEmpty()) {
                response.put("responseCode", "02");
                response.put("responseMsg", "No Movies Found");
                response.put("content", null);
                return ResponseEntity.ok().body(response);
            }

            response.put("responseCode", "00");
            response.put("responseMsg", "Success");
            response.put("content", movies);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{imdb}")
    public ResponseEntity<Map<String, Object>> getMovieByImdb(@PathVariable String imdb) {
        Map<String, Object> response = new HashMap<>();

        // Check authentication
        if (!isAuthenticated()) {
            response.put("responseCode", "03");
            response.put("responseMsg", "Not Authorized");
            response.put("content", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Validate IMDB parameter
        if (imdb == null || imdb.trim().isEmpty()) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Optional<Movie> movieOpt = movieService.getMovieByImdb(imdb);

            if (movieOpt.isPresent()) {
                response.put("responseCode", "00");
                response.put("responseMsg", "Success");
                response.put("content", movieOpt.get());
                return ResponseEntity.ok().body(response);
            } else {
                response.put("responseCode", "02");
                response.put("responseMsg", "No Such Movie Found");
                response.put("content", null);
                return ResponseEntity.ok().body(response);
            }

        } catch (Exception e) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addMovie(@Valid @RequestBody Movie movie, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (!isAuthenticated()) {
            response.put("responseCode", "03");
            response.put("responseMsg", "Not Authorized");
            response.put("content", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Check for validation errors
        if (result.hasErrors()) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

        // Additional validation
        if (movie.getImdb() == null || movie.getTitle() == null ||
                movie.getDescription() == null || movie.getCategory() == null ||
                movie.getImageUrl() == null || movie.getRating() == null ||
                movie.getYear() == null || movie.getRating() < 0 || movie.getYear() <= 0) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

        // Check if movie already exists
        if (movieService.movieExists(movie.getImdb())) {
            response.put("responseCode", "04");
            response.put("responseMsg", "Movie Already Exists");
            response.put("content", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        try {
            movieService.addMovie(movie.getImdb(), movie.getTitle(), movie.getDescription(),
                    movie.getRating(), movie.getCategory(), movie.getYear(), movie.getImageUrl());

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

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateMovie(@Valid @RequestBody Movie movie, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        // Check authentication
        if (!isAuthenticated()) {
            response.put("responseCode", "03");
            response.put("responseMsg", "Not Authorized");
            response.put("content", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Check for validation errors
        if (result.hasErrors()) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

        // Additional validation
        if (movie.getImdb() == null || movie.getTitle() == null ||
                movie.getDescription() == null || movie.getCategory() == null ||
                movie.getImageUrl() == null || movie.getRating() == null ||
                movie.getYear() == null || movie.getRating() < 0 || movie.getYear() <= 0) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

        // Check if movie exists
        if (!movieService.movieExists(movie.getImdb())) {
            response.put("responseCode", "02");
            response.put("responseMsg", "No Such Movie Exists");
            response.put("content", null);
            return ResponseEntity.ok().body(response);
        }

        try {
            movieService.updateMovie(movie);

            response.put("responseCode", "00");
            response.put("responseMsg", "Success");
            response.put("content", null);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{imdb}")  // ✅ Fixed - added path variable
    public ResponseEntity<Map<String, Object>> deleteMovie(@PathVariable String imdb) {
        Map<String, Object> response = new HashMap<>();


        if (!isAuthenticated()) {
            response.put("responseCode", "03");
            response.put("responseMsg", "Not Authorized");
            response.put("content", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }


        if (imdb == null || imdb.trim().isEmpty()) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }


        if (!movieService.movieExists(imdb)) {
            response.put("responseCode", "02");
            response.put("responseMsg", "No Such Movie Exists");
            response.put("content", null);
            return ResponseEntity.ok().body(response);
        }

        try {
            movieService.deleteMovie(imdb);

            response.put("responseCode", "00");
            response.put("responseMsg", "Success");
            response.put("content", null);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }
    }
}