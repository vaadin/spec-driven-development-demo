import { useEffect, useState } from 'react';
import { MovieEndpoint } from 'Frontend/generated/endpoints';
import type MovieSummary from 'Frontend/generated/com/example/specdriven/movie/MovieEndpoint/MovieSummary';

export default function MoviesView() {
  const [movies, setMovies] = useState<MovieSummary[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    MovieEndpoint.getMoviesWithFutureShows().then((data) => {
      setMovies(data?.filter((m): m is MovieSummary => m !== undefined) ?? []);
      setLoading(false);
    });
  }, []);

  if (loading) {
    return <div className="movies-loading">Loading movies...</div>;
  }

  return (
    <div className="movies-container">
      <h1>Now Showing</h1>
      <div className="movies-grid">
        {movies.map((movie) => (
          <a key={movie.id} href={`/movie/${movie.id}`} className="movie-card">
            <div className="movie-poster">
              {movie.posterFileName ? (
                <img
                  src={`/api/posters/${movie.posterFileName}`}
                  alt={movie.title ?? ''}
                  loading="lazy"
                />
              ) : (
                <div className="movie-poster-placeholder">No Poster</div>
              )}
            </div>
            <div className="movie-info">
              <h2 className="movie-title">{movie.title}</h2>
              <span className="movie-duration">{movie.durationMinutes} min</span>
            </div>
          </a>
        ))}
      </div>
    </div>
  );
}
