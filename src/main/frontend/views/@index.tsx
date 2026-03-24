import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { MovieEndpoint } from 'Frontend/generated/endpoints.js';
import type MovieSummary from 'Frontend/generated/com/example/specdriven/movie/MovieEndpoint/MovieSummary.js';

export default function BrowseMoviesView() {
  const [movies, setMovies] = useState<MovieSummary[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    MovieEndpoint.getMoviesWithFutureShows().then((data) =>
      setMovies((data ?? []).filter((m): m is MovieSummary => m !== undefined))
    );
  }, []);

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: 'var(--surface)',
      color: 'var(--text-primary)',
      padding: '2rem',
    }}>
      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
          <h1 style={{ fontSize: '2rem', fontWeight: 'bold', margin: 0 }}>
            CineMax
          </h1>
          <a
            href="/login"
            style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', textDecoration: 'none' }}
          >
            Log in
          </a>
        </div>
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
          gap: '1.5rem',
        }}>
          {movies.map((movie) => (
            <div
              key={movie.id}
              onClick={() => navigate(`/movie/${movie.id}`)}
              style={{
                backgroundColor: 'var(--surface-card)',
                borderRadius: '8px',
                overflow: 'hidden',
                cursor: 'pointer',
                transition: 'transform 0.2s, box-shadow 0.2s',
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.transform = 'scale(1.03)';
                e.currentTarget.style.boxShadow = '0 8px 24px rgba(0,0,0,0.4)';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = 'scale(1)';
                e.currentTarget.style.boxShadow = 'none';
              }}
            >
              <img
                src={movie.posterFileName ? `/api/posters/${movie.posterFileName}` : ''}
                alt={movie.title ?? ''}
                style={{
                  width: '100%',
                  aspectRatio: '2/3',
                  objectFit: 'cover',
                  display: 'block',
                }}
              />
              <div style={{ padding: '1rem' }}>
                <h3 style={{
                  margin: '0 0 0.5rem 0',
                  fontSize: '1.1rem',
                  fontWeight: 600,
                }}>
                  {movie.title}
                </h3>
                <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                  {movie.durationMinutes} min
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
