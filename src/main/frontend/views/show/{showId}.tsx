import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router';
import { ShowEndpoint, TicketEndpoint } from 'Frontend/generated/endpoints.js';
import type ShowDetail from 'Frontend/generated/com/example/specdriven/show/ShowEndpoint/ShowDetail.js';
import type SeatStatus from 'Frontend/generated/com/example/specdriven/ticket/TicketEndpoint/SeatStatus.js';
import type PurchaseResult from 'Frontend/generated/com/example/specdriven/ticket/TicketEndpoint/PurchaseResult.js';

export default function SeatSelectionView() {
  const { showId } = useParams();
  const navigate = useNavigate();
  const [show, setShow] = useState<ShowDetail | null>(null);
  const [soldSeats, setSoldSeats] = useState<Set<string>>(new Set());
  const [selectedSeats, setSelectedSeats] = useState<Set<string>>(new Set());
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [result, setResult] = useState<PurchaseResult | null>(null);
  const [error, setError] = useState('');

  const loadData = () => {
    const id = Number(showId);
    ShowEndpoint.getShowDetail(id).then((s) => setShow(s ?? null));
    TicketEndpoint.getSeatStatuses(id).then((statuses) => {
      const sold = new Set<string>();
      (statuses ?? []).forEach((s) => { if (s) sold.add(`${s.row}-${s.number}`); });
      setSoldSeats(sold);
    });
  };

  useEffect(() => { loadData(); }, [showId]);

  const toggleSeat = (row: number, seat: number) => {
    const key = `${row}-${seat}`;
    if (soldSeats.has(key)) return;
    const next = new Set(selectedSeats);
    if (next.has(key)) {
      next.delete(key);
    } else {
      if (next.size >= 6) {
        setError('Maximum 6 tickets per transaction');
        return;
      }
      next.add(key);
    }
    setError('');
    setSelectedSeats(next);
  };

  const handlePurchase = async () => {
    setError('');
    if (selectedSeats.size === 0) { setError('Please select at least one seat'); return; }
    if (!name.trim()) { setError('Name is required'); return; }
    if (!email.trim() || !email.includes('@')) { setError('Valid email is required'); return; }

    const seats = Array.from(selectedSeats).map((key) => {
      const [r, s] = key.split('-').map(Number);
      return [r, s];
    });

    const res = await TicketEndpoint.purchaseTickets(Number(showId), seats, name, email);
    if (res?.success) {
      setResult(res);
      setSelectedSeats(new Set());
      loadData();
    } else {
      setError(res?.message ?? 'Purchase failed');
      loadData();
    }
  };

  if (!show) {
    return (
      <div style={{ minHeight: '100vh', backgroundColor: 'var(--surface)', color: 'var(--text-primary)', padding: '2rem' }}>
        <p>Loading...</p>
      </div>
    );
  }

  if (result?.success) {
    return (
      <div style={{ minHeight: '100vh', backgroundColor: 'var(--surface)', color: 'var(--text-primary)', padding: '2rem' }}>
        <div style={{ maxWidth: '600px', margin: '0 auto', textAlign: 'center' }}>
          <h2 style={{ color: 'var(--seat-available)' }}>Purchase Confirmed!</h2>
          <p style={{ fontSize: '1.2rem', marginBottom: '1rem' }}>{show.movieTitle}</p>
          <p style={{ color: 'var(--text-secondary)' }}>{show.dateTime} — {show.roomName}</p>
          <div style={{ margin: '1.5rem 0', padding: '1rem', backgroundColor: 'var(--surface-card)', borderRadius: '8px' }}>
            <h4 style={{ marginBottom: '0.5rem' }}>Your Tickets</h4>
            {(result.tickets ?? []).filter(t => t != null).map((t, i) => (
              <p key={i} style={{ color: 'var(--text-secondary)' }}>
                Seat {String.fromCharCode(64 + (t!.seatRow ?? 0))}{t!.seatNumber} — {t!.customerName}
              </p>
            ))}
          </div>
          <button
            onClick={() => navigate(`/movie/${show.movieId}`)}
            style={{
              padding: '0.75rem 2rem', borderRadius: '8px', border: 'none',
              backgroundColor: 'var(--primary)', color: 'white', cursor: 'pointer',
              fontSize: '1rem', fontWeight: 600,
            }}
          >
            Back to Movie
          </button>
        </div>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: 'var(--surface)', color: 'var(--text-primary)', padding: '2rem' }}>
      <div style={{ maxWidth: '800px', margin: '0 auto' }}>
        <button
          onClick={() => navigate(`/movie/${show.movieId}`)}
          style={{ background: 'none', border: 'none', color: 'var(--primary)', cursor: 'pointer', fontSize: '1rem', marginBottom: '1rem', padding: 0 }}
        >
          ← Back to Movie
        </button>

        <h2 style={{ margin: '0 0 0.25rem 0' }}>{show.movieTitle}</h2>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>
          {show.dateTime} — {show.roomName}
        </p>

        {/* Screen indicator */}
        <div style={{
          width: '60%', height: '4px', margin: '0 auto 0.5rem auto',
          backgroundColor: 'var(--text-secondary)', borderRadius: '50%/100% 100% 0 0',
        }} />
        <p style={{ textAlign: 'center', color: 'var(--text-secondary)', fontSize: '0.85rem', marginBottom: '1.5rem' }}>
          Screen
        </p>

        {/* Seat map */}
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '0.4rem', marginBottom: '1.5rem' }}>
          {Array.from({ length: show.rows ?? 0 }, (_, r) => r + 1).map((row) => (
            <div key={row} style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
              <span style={{ width: '1.5rem', textAlign: 'center', color: 'var(--text-secondary)', fontSize: '0.8rem' }}>
                {String.fromCharCode(64 + row)}
              </span>
              {Array.from({ length: show.seatsPerRow ?? 0 }, (_, s) => s + 1).map((seat) => {
                const key = `${row}-${seat}`;
                const isSold = soldSeats.has(key);
                const isSelected = selectedSeats.has(key);
                let bg = 'var(--seat-available)';
                if (isSold) bg = 'var(--seat-sold)';
                else if (isSelected) bg = 'var(--seat-selected)';

                return (
                  <button
                    key={seat}
                    disabled={isSold}
                    onClick={() => toggleSeat(row, seat)}
                    title={`${String.fromCharCode(64 + row)}${seat}`}
                    style={{
                      width: '2rem', height: '2rem', borderRadius: '4px 4px 8px 8px',
                      border: 'none', backgroundColor: bg,
                      cursor: isSold ? 'not-allowed' : 'pointer',
                      color: 'white', fontSize: '0.65rem', fontWeight: 600,
                    }}
                  >
                    {seat}
                  </button>
                );
              })}
            </div>
          ))}
        </div>

        {/* Legend */}
        <div style={{ display: 'flex', gap: '1.5rem', justifyContent: 'center', marginBottom: '1.5rem' }}>
          {[
            { color: 'var(--seat-available)', label: 'Available' },
            { color: 'var(--seat-selected)', label: 'Selected' },
            { color: 'var(--seat-sold)', label: 'Sold' },
          ].map(({ color, label }) => (
            <div key={label} style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
              <div style={{ width: '1rem', height: '1rem', borderRadius: '3px', backgroundColor: color }} />
              <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{label}</span>
            </div>
          ))}
        </div>

        {/* Purchase form */}
        <div style={{ backgroundColor: 'var(--surface-card)', borderRadius: '8px', padding: '1.5rem' }}>
          <p style={{ marginBottom: '1rem' }}>
            Selected: <strong>{selectedSeats.size}</strong> seat{selectedSeats.size !== 1 ? 's' : ''}
          </p>

          <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', marginBottom: '1rem' }}>
            <input
              type="text" placeholder="Your name" value={name}
              onChange={(e) => setName(e.target.value)}
              style={{
                flex: 1, minWidth: '200px', padding: '0.75rem', borderRadius: '6px',
                border: '1px solid var(--text-secondary)', backgroundColor: 'var(--surface)',
                color: 'var(--text-primary)',
              }}
            />
            <input
              type="email" placeholder="Your email" value={email}
              onChange={(e) => setEmail(e.target.value)}
              style={{
                flex: 1, minWidth: '200px', padding: '0.75rem', borderRadius: '6px',
                border: '1px solid var(--text-secondary)', backgroundColor: 'var(--surface)',
                color: 'var(--text-primary)',
              }}
            />
          </div>

          {error && (
            <p style={{ color: 'var(--primary)', marginBottom: '1rem' }}>{error}</p>
          )}

          <button
            onClick={handlePurchase}
            disabled={selectedSeats.size === 0}
            style={{
              padding: '0.75rem 2rem', borderRadius: '8px', border: 'none',
              backgroundColor: selectedSeats.size > 0 ? 'var(--primary)' : 'var(--seat-sold)',
              color: 'white', cursor: selectedSeats.size > 0 ? 'pointer' : 'not-allowed',
              fontSize: '1rem', fontWeight: 600, width: '100%',
            }}
          >
            Purchase {selectedSeats.size > 0 ? `${selectedSeats.size} Ticket${selectedSeats.size !== 1 ? 's' : ''}` : ''}
          </button>
        </div>
      </div>
    </div>
  );
}
