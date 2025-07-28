package EpicLanka.example.demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "movies")
@Schema(description = "Movie entity representing a movie in the system")
public class Movie {

    @Id
    @Column(length = 20)
    @NotBlank(message = "IMDB ID is required")
    @Schema(description = "IMDB identifier for the movie",
            example = "tt1234567",
            required = true)
    private String imdb;

    @Column(nullable = false)
    @NotBlank(message = "Title is required")
    @Schema(description = "Title of the movie",
            example = "The Dark Knight",
            required = true)
    private String title;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Detailed description of the movie",
            example = "A superhero film about Batman fighting the Joker")
    private String description;

    @DecimalMin(value = "0.0", message = "Rating must be positive")
    @DecimalMax(value = "10.0", message = "Rating must be less than or equal to 10")
    @Schema(description = "Movie rating out of 10",
            example = "8.5",
            minimum = "0.0",
            maximum = "10.0")
    private Double rating;

    @Schema(description = "Movie category/genre",
            example = "Action")
    private String category;

    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2030, message = "Year must be reasonable")
    @Schema(description = "Release year of the movie",
            example = "2008",
            minimum = "1900",
            maximum = "2030")
    private Integer year;

    @Column(name = "image_url", columnDefinition = "TEXT")
    @Schema(description = "URL of the movie poster image",
            example = "https://example.com/poster.jpg")
    private String imageUrl;

    // Constructors, getters, and setters remain the same
    public Movie() {}

    public Movie(String imdb, String title, String description, Double rating,
                 String category, Integer year, String imageUrl) {
        this.imdb = imdb;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.category = category;
        this.year = year;
        this.imageUrl = imageUrl;
    }

    public @NotBlank(message = "IMDB ID is required") String getImdb() {
        return imdb;
    }

    public void setImdb(@NotBlank(message = "IMDB ID is required") String imdb) {
        this.imdb = imdb;
    }

    public @NotBlank(message = "Title is required") String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank(message = "Title is required") String title) {
        this.title = title;
    }

    public @DecimalMin(value = "0.0", message = "Rating must be positive") @DecimalMax(value = "10.0", message = "Rating must be less than or equal to 10") Double getRating() {
        return rating;
    }

    public void setRating(@DecimalMin(value = "0.0", message = "Rating must be positive") @DecimalMax(value = "10.0", message = "Rating must be less than or equal to 10") Double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public @Min(value = 1900, message = "Year must be after 1900") @Max(value = 2030, message = "Year must be reasonable") Integer getYear() {
        return year;
    }

    public void setYear(@Min(value = 1900, message = "Year must be after 1900") @Max(value = 2030, message = "Year must be reasonable") Integer year) {
        this.year = year;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}