import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { Card } from '@vaadin/react-components/Card.js';
import { getTodaysMovies } from 'Frontend/generated/MovieBrowseService';
import type MovieWithScreeningsDTO
  from 'Frontend/generated/com/example/specdriven/movie/MovieBrowseService/MovieWithScreeningsDTO';

export default function IndexView() {
  const [movies, setMovies] = useState<MovieWithScreeningsDTO[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    getTodaysMovies().then((result) => {
      setMovies(result?.filter((m): m is MovieWithScreeningsDTO => m !== undefined) ?? []);
    });
  }, []);

  return (
    <div style={{ padding: '2rem' }}>
      <h1 style={{ marginTop: 0 }}>Today&apos;s Movies</h1>
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
        gap: '1.5rem',
      }}>
        {movies.map((movie) => (
          <Card
            key={movie.id}
            theme="outlined cover-media"
            style={{ cursor: 'pointer' }}
            onClick={() => navigate(`/movie/${movie.id}`)}
          >
            <img
              slot="media"
              src={`/images/posters/${movie.posterFileName}`}
              alt={movie.title ?? ''}
              style={{ width: '100%', aspectRatio: '2/3', objectFit: 'cover' }}
            />
            <div slot="title">{movie.title}</div>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
              {movie.screenings?.filter((s) => s !== undefined).map((s) => (
                <span
                  key={s.id}
                  {...{ theme: `badge ${s.past ? '' : 'success'}` }}
                  style={s.past ? { opacity: 0.5 } : undefined}
                >
                  {s.time?.substring(0, 5)}
                </span>
              ))}
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}
