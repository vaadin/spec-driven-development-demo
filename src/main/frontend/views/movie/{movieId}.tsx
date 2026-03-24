import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router';
import { MovieEndpoint, ShowEndpoint } from 'Frontend/generated/endpoints.js';
import type MovieDetail from 'Frontend/generated/com/example/specdriven/movie/MovieEndpoint/MovieDetail.js';
import type ShowtimeGroup from 'Frontend/generated/com/example/specdriven/show/ShowEndpoint/ShowtimeGroup.js';
import type ShowtimeInfo from 'Frontend/generated/com/example/specdriven/show/ShowEndpoint/ShowtimeInfo.js';

export default function MovieDetailView() {
  const { movieId } = useParams();
  const navigate = useNavigate();
  const [movie, setMovie] = useState<MovieDetail | null>(null);
  const [showtimeGroups, setShowtimeGroups] = useState<ShowtimeGroup[]>([]);

  useEffect(() => {
    const id = Number(movieId);
    MovieEndpoint.getMovieById(id).then((m) => setMovie(m ?? null));
    ShowEndpoint.getShowtimesForMovie(id).then((groups) =>
      setShowtimeGroups((groups ?? []).filter((g): g is ShowtimeGroup => g !== undefined))
    );
  }, [movieId]);

  if (!movie) {
    return (
      <div style={{ minHeight: '100vh', backgroundColor: 'var(--surface)', color: 'var(--text-primary)', padding: '2rem' }}>
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: 'var(--surface)', color: 'var(--text-primary)', padding: '2rem' }}>
      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <button
          onClick={() => navigate('/')}
          style={{
            background: 'none', border: 'none', color: 'var(--primary)',
            cursor: 'pointer', fontSize: '1rem', marginBottom: '1rem', padding: 0,
          }}
        >
          ← Back to Movies
        </button>

        <div style={{
          display: 'flex', gap: '2rem', flexWrap: 'wrap', marginBottom: '2rem',
        }}>
          {movie.posterFileName && (
            <img
              src={`/api/posters/${movie.posterFileName}`}
              alt={movie.title ?? ''}
              style={{ width: '300px', borderRadius: '8px', objectFit: 'cover' }}
            />
          )}
          <div style={{ flex: 1, minWidth: '280px' }}>
            <h2 style={{ margin: '0 0 0.5rem 0' }}>{movie.title}</h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '1rem' }}>
              {movie.durationMinutes} minutes
            </p>
            {movie.description && (
              <p style={{ lineHeight: 1.6 }}>{movie.description}</p>
            )}
          </div>
        </div>

        <h3 style={{ marginBottom: '1rem' }}>Showtimes</h3>
        {showtimeGroups.length === 0 ? (
          <p style={{ color: 'var(--text-secondary)' }}>No upcoming showtimes.</p>
        ) : (
          showtimeGroups.map((group) => (
            <div key={group.date} style={{ marginBottom: '1.5rem' }}>
              <h4 style={{ color: 'var(--text-secondary)', marginBottom: '0.75rem' }}>
                {group.date}
              </h4>
              <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
                {((group.showtimes ?? []) as ShowtimeInfo[]).filter(s => s != null).map((st) => (
                  <button
                    key={st.id}
                    disabled={st.soldOut}
                    onClick={() => !st.soldOut && navigate(`/show/${st.id}`)}
                    style={{
                      padding: '0.75rem 1.25rem',
                      borderRadius: '8px',
                      border: st.soldOut ? '1px solid var(--seat-sold)' : '1px solid var(--primary)',
                      backgroundColor: st.soldOut ? 'var(--seat-sold)' : 'transparent',
                      color: st.soldOut ? 'var(--text-secondary)' : 'var(--text-primary)',
                      cursor: st.soldOut ? 'not-allowed' : 'pointer',
                      opacity: st.soldOut ? 0.6 : 1,
                    }}
                  >
                    <div style={{ fontWeight: 600 }}>{st.time}</div>
                    <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                      {st.roomName}
                    </div>
                    <div style={{ fontSize: '0.8rem', color: st.soldOut ? 'var(--primary)' : 'var(--text-secondary)' }}>
                      {st.soldOut ? 'Sold out' : `${st.availableSeats} seats`}
                    </div>
                  </button>
                ))}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
