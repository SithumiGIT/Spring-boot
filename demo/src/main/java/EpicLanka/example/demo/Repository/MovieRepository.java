package EpicLanka.example.demo.Repository;

import EpicLanka.example.demo.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {
    Optional<Movie> findByImdb(String imdb);
    boolean existsByImdb(String imdb);
    void deleteByImdb(String imdb);
}