package EpicLanka.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @Column(length = 20)
    @NotBlank(message = "IMDB ID is required")
    private String imdb;

    @Column(nullable = false)
    @NotBlank(message = "Title is required")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @DecimalMin(value = "0.0", message = "Rating must be positive")
    @DecimalMax(value = "10.0", message = "Rating must be less than or equal to 10")
    private Double rating;

    private String category;

    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2030, message = "Year must be reasonable")
    private Integer year;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

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

    // Getters and Setters
    public String getImdb() {
        return imdb;
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}