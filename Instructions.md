# Popcorn Palace – Instructions

This guide will help you set up, run, and test the project locally using Spring Boot, PostgreSQL (via Docker), and Maven.

## How to build and run the project
### from the root of the project type the following commands
- "docker-compose up -d"
this will start a PostgreSQL container, expose port 5432 and setup the initial database as configured in application.yaml
- "mvn clean install"
this will build the code
- "mvn spring-boot:run"
this will start the project, that will be available at http://localhost:8080

## How to test the project
- from the root of the project type the command "mvn test"
this will test both the services nd endpoints

## Available enpoints (as described in the assignment)
### movies
- GET/movies/all - List all movies
- POST/movies – Add a new movie
- POST/movies/{title} – Update movie by title
- DELETE/movies/{title} – Delete movie by title
### showtimes
- POST/showtimes – Add new showtime
- POST/showtimes/{id} – Update existing showtime
- GET/showtimes/{id} – Get showtime by ID
- DELETE/showtimes/{id} – Delete showtime
### bookings
- POST/bookings - Book a seat

## cleanup
to stop and remove the data base container "docker-compose down"

## Prerequisites
- **Java 21**
- **Maven**
- **Docker Desktop**

## Author
Shay Gabai
built as part of a home assignment for the TDP 2025 program
