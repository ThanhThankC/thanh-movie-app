# ThanhMovie - Ứng dụng Tra Cứu Phim

An Android movie search application built with Java and TMDB API.

## Features

- Browse popular & trending movies
- Search movies by title
- View movie details (poster, overview, rating, genres)
- Save favorite movies (offline with Room Database)
- Multi-language support (Vietnamese / English)
- Portrait & landscape screen support
- Filter movies by genre

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java |
| IDE | Android Studio |
| API | TMDB (The Movie Database) |
| Networking | Retrofit + Gson |
| Image Loading | Glide |
| Local Database | Room Database |
| Navigation | Bottom Navigation + Fragment |

## Screenshots

> Coming soon...

## Getting Started

1. Clone the repository
```bash
   git clone https://github.com/ThanhThankC/ThanhMovie-Android.git
```
2. Open in Android Studio
3. Add your TMDB API key in `utils/Constants.java`
```java
   public static final String API_KEY = "your_api_key_here";
```
4. Run on device or emulator

## API Key

This project uses [TMDB API](https://www.themoviedb.org/documentation/api).  
Register for a free API key at: https://www.themoviedb.org/settings/api

## Project Structure
```
com.example.thanhmovie
├── model        # Data classes (Movie, MovieDetail...)
├── api          # Retrofit interface & client
├── adapter      # RecyclerView adapters
├── activity     # Activities
├── fragment     # Fragments
├── database     # Room Entity, DAO, Database
└── utils        # Constants, helper functions
```

## Author

**ThanhThankC**  
GitHub: [@ThanhThankC](https://github.com/ThanhThankC)

---
*Đồ án môn Phát Triển Ứng Dụng Di Động*
```

---
