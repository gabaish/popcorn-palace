package com.att.tdp.popcorn_palace.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movies", uniqueConstraints = {
    @UniqueConstraint(columnNames = "title") 
})

public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Title is required")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Genre is required")
    private String genre;

    @Column(nullable = false)
    @Min(value = 0, message = "Duration must be greater than 0")
    private int duration; 

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be greater than 0")
    private double rating;

    @Column(nullable = false)
    @Min(value = 1500, message = "Release year must be greater than 1500")
    private int releaseYear;

    // Ensure all showtimes related to this movie are deleted when movie is deleted
    @OneToMany(mappedBy = "movieId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Showtime> showtimes = new ArrayList<>();
}
