import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router';
import { Button } from '@vaadin/react-components/Button.js';
import { TextField } from '@vaadin/react-components/TextField.js';
import { EmailField } from '@vaadin/react-components/EmailField.js';
import { IntegerField } from '@vaadin/react-components/IntegerField.js';
import { Select } from '@vaadin/react-components/Select.js';
import { getMovieDetails, purchaseTickets } from 'Frontend/generated/TicketPurchaseService';
import type MovieDetailsDTO
  from 'Frontend/generated/com/example/specdriven/movie/TicketPurchaseService/MovieDetailsDTO';
import type PurchaseConfirmationDTO
  from 'Frontend/generated/com/example/specdriven/movie/TicketPurchaseService/PurchaseConfirmationDTO';

export default function MovieDetailView() {
  const { movieId } = useParams();
  const navigate = useNavigate();
  const [movie, setMovie] = useState<MovieDetailsDTO | undefined>();
  const [selectedScreeningId, setSelectedScreeningId] = useState<string>('');
  const [ticketCount, setTicketCount] = useState<number>(1);
  const [customerName, setCustomerName] = useState('');
  const [customerEmail, setCustomerEmail] = useState('');
  const [error, setError] = useState('');
  const [confirmation, setConfirmation] = useState<PurchaseConfirmationDTO | undefined>();

  useEffect(() => {
    if (movieId) {
      getMovieDetails(parseInt(movieId)).then(setMovie);
    }
  }, [movieId]);

  if (!movie) {
    return <div style={{ padding: '2rem' }}>Loading...</div>;
  }

  if (confirmation) {
    return (
      <div style={{ padding: '2rem', maxWidth: '600px' }}>
        <h1>Booking Confirmed!</h1>
        <div style={{
          background: 'var(--lumo-contrast-5pct)',
          borderRadius: 'var(--lumo-border-radius-l)',
          padding: '2rem',
          display: 'flex',
          flexDirection: 'column',
          gap: '1rem',
        }}>
          <div style={{ fontSize: '2rem', fontWeight: 'bold', textAlign: 'center' }}>
            {confirmation.confirmationCode}
          </div>
          <div><strong>Movie:</strong> {confirmation.movieTitle}</div>
          <div><strong>Show time:</strong> {confirmation.showTime?.substring(0, 5)}</div>
          <div><strong>Tickets:</strong> {confirmation.ticketCount}</div>
          <div style={{
            marginTop: '1rem',
            padding: '1rem',
            background: 'var(--lumo-primary-color-10pct)',
            borderRadius: 'var(--lumo-border-radius-m)',
          }}>
            Payment is collected at the cinema when you pick up your tickets.
            Please present your confirmation code.
          </div>
        </div>
        <Button style={{ marginTop: '1.5rem' }} onClick={() => navigate('/')}>
          Back to Movies
        </Button>
      </div>
    );
  }

  const futureScreenings = movie.screenings
    ?.filter((s) => s !== undefined && !s.past) ?? [];

  const selectItems = futureScreenings.map((s) => ({
    label: `${s.time?.substring(0, 5)} (${s.availableSeats} seats available)`,
    value: String(s.id),
  }));

  async function handlePurchase() {
    setError('');
    if (!selectedScreeningId) {
      setError('Please select a show time');
      return;
    }
    try {
      const result = await purchaseTickets(
        parseInt(selectedScreeningId), ticketCount, customerName, customerEmail
      );
      if (result) {
        setConfirmation(result);
      }
    } catch (e: any) {
      setError(e?.message ?? 'Purchase failed');
    }
  }

  return (
    <div style={{ padding: '2rem', maxWidth: '900px' }}>
      <Button theme="tertiary" onClick={() => navigate('/')}>
        ← Back to Movies
      </Button>

      <div style={{
        display: 'grid',
        gridTemplateColumns: 'minmax(200px, 300px) 1fr',
        gap: '2rem',
        marginTop: '1rem',
      }}>
        <img
          src={`/images/posters/${movie.posterFileName}`}
          alt={movie.title ?? ''}
          style={{
            width: '100%',
            borderRadius: 'var(--lumo-border-radius-l)',
            aspectRatio: '2/3',
            objectFit: 'cover',
          }}
        />

        <div>
          <h1 style={{ marginTop: 0 }}>{movie.title}</h1>
          <p style={{ color: 'var(--lumo-secondary-text-color)' }}>
            {movie.durationMinutes} min
          </p>
          <p>{movie.description}</p>

          <h2>Purchase Tickets</h2>

          {futureScreenings.length === 0 ? (
            <p>No upcoming screenings available today.</p>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', maxWidth: '400px' }}>
              <Select
                label="Show time"
                items={selectItems}
                value={selectedScreeningId}
                onValueChanged={(e) => setSelectedScreeningId(e.detail.value)}
              />
              <IntegerField
                label="Number of tickets"
                min={1}
                max={10}
                stepButtonsVisible
                value={String(ticketCount)}
                onValueChanged={(e) => setTicketCount(parseInt(e.detail.value) || 1)}
              />
              <TextField
                label="Your name"
                required
                value={customerName}
                onValueChanged={(e) => setCustomerName(e.detail.value)}
              />
              <EmailField
                label="Email"
                required
                value={customerEmail}
                onValueChanged={(e) => setCustomerEmail(e.detail.value)}
              />

              {error && (
                <div style={{ color: 'var(--lumo-error-text-color)' }}>{error}</div>
              )}

              <Button theme="primary" onClick={handlePurchase}>
                Purchase
              </Button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
