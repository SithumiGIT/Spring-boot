package EpicLanka.example.demo.Controller;

import EpicLanka.example.demo.Repository.MovieRepository;
import EpicLanka.example.demo.Service.MovieService;
import EpicLanka.example.demo.entity.Movie;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Movie Management", description = "APIs for managing movies in the Epic Lanka system")
@SecurityRequirement(name = "Bearer Authentication")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieService movieService;

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser");
    }

    @GetMapping
    @Operation(
            summary = "Get all movies",
            description = "Retrieves a list of all movies in the system. Requires JWT authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved movies",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "responseCode": "00",
                                        "responseMsg": "Success",
                                        "content": [
                                            {
                                                "imdb": "tt1234567",
                                                "title": "Sample Movie",
                                                "description": "A great movie",
                                                "rating": 8.5,
                                                "category": "Action",
                                                "year": 2023,
                                                "imageUrl": "https://example.com/image.jpg"
                                            }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<Map<String, Object>> getAllMovies() {
        Map<String, Object> response = new HashMap<>();

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
    @Operation(
            summary = "Get movie by IMDB ID",
            description = "Retrieves a specific movie by its IMDB identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie found successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid IMDB ID format")
    })
    public ResponseEntity<Map<String, Object>> getMovieByImdb(
            @Parameter(description = "IMDB ID of the movie", example = "tt1234567")
            @PathVariable String imdb) {
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
    @Operation(
            summary = "Add a new movie",
            description = "Creates a new movie entry in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie created successfully"),
            @ApiResponse(responseCode = "409", description = "Movie already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid movie data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Map<String, Object>> addMovie(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Movie object to be created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Movie.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "imdb": "tt1234567",
                                        "title": "Sample Movie",
                                        "description": "A great action movie",
                                        "rating": 8.5,
                                        "category": "Action",
                                        "year": 2023,
                                        "imageUrl": "https://example.com/image.jpg"
                                    }
                                    """)
                    )
            )
            @Valid @RequestBody Movie movie, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (!isAuthenticated()) {
            response.put("responseCode", "03");
            response.put("responseMsg", "Not Authorized");
            response.put("content", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (result.hasErrors()) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

        if (movie.getImdb() == null || movie.getTitle() == null ||
                movie.getDescription() == null || movie.getCategory() == null ||
                movie.getImageUrl() == null || movie.getRating() == null ||
                movie.getYear() == null || movie.getRating() < 0 || movie.getYear() <= 0) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

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
    @Operation(
            summary = "Update an existing movie",
            description = "Updates movie information for an existing movie"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "400", description = "Invalid movie data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Map<String, Object>> updateMovie(@Valid @RequestBody Movie movie, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (!isAuthenticated()) {
            response.put("responseCode", "03");
            response.put("responseMsg", "Not Authorized");
            response.put("content", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (result.hasErrors()) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

        if (movie.getImdb() == null || movie.getTitle() == null ||
                movie.getDescription() == null || movie.getCategory() == null ||
                movie.getImageUrl() == null || movie.getRating() == null ||
                movie.getYear() == null || movie.getRating() < 0 || movie.getYear() <= 0) {
            response.put("responseCode", "06");
            response.put("responseMsg", "Bad Request");
            response.put("content", null);
            return ResponseEntity.badRequest().body(response);
        }

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

    @DeleteMapping("/{imdb}")
    @Operation(
            summary = "Delete a movie",
            description = "Removes a movie from the system by IMDB ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "400", description = "Invalid IMDB ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Map<String, Object>> deleteMovie(
            @Parameter(description = "IMDB ID of the movie to delete", example = "tt1234567")
            @PathVariable String imdb) {
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