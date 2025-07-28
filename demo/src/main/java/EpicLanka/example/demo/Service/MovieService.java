package EpicLanka.example.demo.Service;

import EpicLanka.example.demo.Repository.MovieRepository;
import EpicLanka.example.demo.entity.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public void addMovie(String imdb, String title, String description, Double rating,
                         String category, Integer year, String imageUrl) {
        Movie movie = new Movie();
        movie.setImdb(imdb);
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setRating(rating);
        movie.setCategory(category);
        movie.setYear(year);
        movie.setImageUrl(imageUrl);

        movieRepository.save(movie);
    }

    public void updateMovie(Movie movie) {
        movieRepository.save(movie);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieByImdb(String imdb) {
        return movieRepository.findByImdb(imdb);
    }

    public boolean movieExists(String imdb) {
        return movieRepository.existsByImdb(imdb);
    }

    public void deleteMovie(String imdb) {
        movieRepository.deleteByImdb(imdb);
    }
}