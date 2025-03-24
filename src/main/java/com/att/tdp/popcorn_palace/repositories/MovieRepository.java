package com.att.tdp.popcorn_palace.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.att.tdp.popcorn_palace.entities.Movie;
import java.util.Optional;

@Repository
public interface  MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTitle(String title);
    void deleteByTitle(String title);
    boolean existsByTitle(String title);
}
