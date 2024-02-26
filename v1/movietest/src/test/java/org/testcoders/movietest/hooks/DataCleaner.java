package org.testcoders.movietest.hooks;

import io.cucumber.java.Before;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.testcoders.movietest.model.Movie;
import org.testcoders.movietest.model.User;
import org.testcoders.movietest.repository.MovieRepository;
import org.testcoders.movietest.repository.UserRepository;

@AllArgsConstructor
public class DataCleaner {

  private final UserRepository userRepository;
  private final MovieRepository movieRepository;

  @Before
  public void cleanUserData() {
    try {
      List<User> users = new ArrayList<>();
      userRepository.findAll().forEach(users::add);

      // Wait until exactly 2 users are found
      while (users.size() < 2) {
          Thread.sleep(1000);

          // Refresh the list of users
          users.clear();
          userRepository.findAll().forEach(users::add);
      }
      // Find the default Admin and User and keep them
      User admin = userRepository.findById(1).orElseThrow(() -> new RuntimeException("Admin user not found"));
      User user = userRepository.findById(2).orElseThrow(() -> new RuntimeException("User not found"));
      userRepository.emptyDatabase();
      userRepository.save(admin);
      userRepository.save(user);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread was interrupted", e);
    }
  }

  @Before
  public void cleanMovieData() {
    try {
      List<Movie> movies = new ArrayList<>(movieRepository.findAll());

      while (movies.size() < 33) {
        Thread.sleep(1000);

        // Refresh the list of movies
        movies.clear();
        movies.addAll(movieRepository.findAll());
      }
      
      // Assuming you want to keep the first 34 movies
      List<Movie> moviesToKeep = movies.stream().limit(34).toList();
      movieRepository.deleteAll();
      movieRepository.saveAll(moviesToKeep);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread was interrupted", e);
    }
  }
}