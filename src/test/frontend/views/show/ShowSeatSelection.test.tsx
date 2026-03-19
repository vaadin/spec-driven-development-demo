import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { MemoryRouter, Route, Routes } from 'react-router';
import ShowSeatSelectionView from 'Frontend/views/show/{showId}';

// Mock the endpoint module
vi.mock('Frontend/generated/endpoints', () => ({
  ShowEndpoint: {
    getShowDetail: vi.fn(),
    purchaseTickets: vi.fn(),
  },
}));

import { ShowEndpoint } from 'Frontend/generated/endpoints';

const mockGetShowDetail = vi.mocked(ShowEndpoint.getShowDetail);
const mockPurchaseTickets = vi.mocked(ShowEndpoint.purchaseTickets);

function renderWithRoute(showId = '1') {
  return render(
    <MemoryRouter initialEntries={[`/show/${showId}`]}>
      <Routes>
        <Route path="/show/:showId" element={<ShowSeatSelectionView />} />
      </Routes>
    </MemoryRouter>
  );
}

const defaultShowDetail = {
  id: 1,
  movieTitle: 'Inception',
  dateTime: '2026-03-20T19:00:00',
  roomName: 'Room A',
  rows: 3,
  seatsPerRow: 4,
  soldSeats: [{ row: 1, seat: 2 }],
};

describe('UC-003: ShowSeatSelectionView', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockGetShowDetail.mockResolvedValue(defaultShowDetail);
  });

  it('displays movie title, showtime, and room name', async () => {
    renderWithRoute();

    expect(await screen.findByText('Inception')).toBeInTheDocument();
    expect(screen.getByText(/Room A/)).toBeInTheDocument();
  });

  it('displays seat map with correct rows and columns', async () => {
    renderWithRoute();

    await screen.findByText('Inception');

    // 3 rows, each with seat buttons
    const rows = document.querySelectorAll('.seat-row');
    expect(rows.length).toBe(3);

    // Row labels A, B, C
    expect(screen.getAllByText('A').length).toBeGreaterThanOrEqual(1);
    expect(screen.getAllByText('B').length).toBeGreaterThanOrEqual(1);
    expect(screen.getAllByText('C').length).toBeGreaterThanOrEqual(1);
  });

  it('displays screen indicator', async () => {
    renderWithRoute();

    await screen.findByText('Inception');
    expect(screen.getByText('Screen')).toBeInTheDocument();
  });

  it('displays seat legend with available, selected, and sold', async () => {
    renderWithRoute();

    await screen.findByText('Inception');
    expect(screen.getByText('Available')).toBeInTheDocument();
    expect(screen.getByText('Selected')).toBeInTheDocument();
    expect(screen.getByText('Sold')).toBeInTheDocument();
  });

  it('sold seats are visually distinct and disabled', async () => {
    renderWithRoute();

    await screen.findByText('Inception');

    // Seat A2 is sold (row 1, seat 2)
    const soldButton = screen.getByTitle('A2 (Sold)');
    expect(soldButton).toBeDisabled();
    expect(soldButton).toHaveClass('seat-sold');
  });

  it('clicking an available seat selects it', async () => {
    const user = userEvent.setup();
    renderWithRoute();

    await screen.findByText('Inception');

    const seatButton = screen.getByTitle('A1');
    await user.click(seatButton);

    expect(seatButton).toHaveClass('seat-selected');
  });

  it('clicking a selected seat deselects it', async () => {
    const user = userEvent.setup();
    renderWithRoute();

    await screen.findByText('Inception');

    const seatButton = screen.getByTitle('A1');
    await user.click(seatButton); // select
    expect(seatButton).toHaveClass('seat-selected');

    await user.click(seatButton); // deselect
    expect(seatButton).toHaveClass('seat-available');
  });

  it('shows number of selected seats in summary', async () => {
    const user = userEvent.setup();
    renderWithRoute();

    await screen.findByText('Inception');

    expect(screen.getByText('No seats selected')).toBeInTheDocument();

    await user.click(screen.getByTitle('A1'));
    expect(screen.getByText(/1 seat\(s\) selected/)).toBeInTheDocument();

    await user.click(screen.getByTitle('B1'));
    expect(screen.getByText(/2 seat\(s\) selected/)).toBeInTheDocument();
  });

  it('maximum 6 seats can be selected', async () => {
    mockGetShowDetail.mockResolvedValue({
      ...defaultShowDetail,
      rows: 2,
      seatsPerRow: 8,
      soldSeats: [],
    });
    const user = userEvent.setup();
    renderWithRoute();

    await screen.findByText('Inception');

    // Select 6 seats
    for (let i = 1; i <= 6; i++) {
      await user.click(screen.getByTitle(`A${i}`));
    }

    expect(screen.getByText(/6 seat\(s\) selected/)).toBeInTheDocument();

    // Try to select 7th
    await user.click(screen.getByTitle('A7'));
    expect(screen.getByText('Maximum 6 seats per transaction')).toBeInTheDocument();
    // Still only 6 selected
    expect(screen.getByText(/6 seat\(s\) selected/)).toBeInTheDocument();
  });

  it('purchase button is disabled when no seats selected', async () => {
    renderWithRoute();

    await screen.findByText('Inception');

    const purchaseBtn = screen.getByRole('button', { name: /Purchase 0 Ticket/ });
    expect(purchaseBtn).toBeDisabled();
  });

  it('shows error when name is empty on purchase', async () => {
    const user = userEvent.setup();
    renderWithRoute();

    await screen.findByText('Inception');

    await user.click(screen.getByTitle('A1'));
    await user.type(screen.getByPlaceholderText('Your email'), 'alice@example.com');
    await user.click(screen.getByRole('button', { name: /Purchase/ }));

    expect(screen.getByText('Name is required')).toBeInTheDocument();
    expect(mockPurchaseTickets).not.toHaveBeenCalled();
  });

  it('shows error when email is invalid on purchase', async () => {
    const user = userEvent.setup();
    renderWithRoute();

    await screen.findByText('Inception');

    await user.click(screen.getByTitle('A1'));
    await user.type(screen.getByPlaceholderText('Your name'), 'Alice');
    await user.type(screen.getByPlaceholderText('Your email'), 'not-an-email');
    await user.click(screen.getByRole('button', { name: /Purchase/ }));

    expect(screen.getByText('Valid email is required')).toBeInTheDocument();
    expect(mockPurchaseTickets).not.toHaveBeenCalled();
  });

  it('successful purchase shows confirmation', async () => {
    mockPurchaseTickets.mockResolvedValue({
      success: true,
      message: 'Successfully purchased 1 ticket(s)!',
      tickets: [{ row: 1, seat: 1 }],
    });
    const user = userEvent.setup();
    renderWithRoute();

    await screen.findByText('Inception');

    await user.click(screen.getByTitle('A1'));
    await user.type(screen.getByPlaceholderText('Your name'), 'Alice');
    await user.type(screen.getByPlaceholderText('Your email'), 'alice@example.com');
    await user.click(screen.getByRole('button', { name: /Purchase/ }));

    expect(await screen.findByText('Booking Confirmed!')).toBeInTheDocument();
    expect(screen.getByText('Successfully purchased 1 ticket(s)!')).toBeInTheDocument();
    expect(screen.getByText(/A1/)).toBeInTheDocument();
  });

  it('Back to Movie link points to the movie, not the show', async () => {
    // Use show ID 42 with movie ID 7 to ensure they are different
    mockGetShowDetail.mockResolvedValue({
      ...defaultShowDetail,
      id: 42,
      movieId: 7,
    });
    mockPurchaseTickets.mockResolvedValue({
      success: true,
      message: 'Successfully purchased 1 ticket(s)!',
      tickets: [{ row: 1, seat: 1 }],
    });
    const user = userEvent.setup();
    renderWithRoute('42');

    await screen.findByText('Inception');

    await user.click(screen.getByTitle('A1'));
    await user.type(screen.getByPlaceholderText('Your name'), 'Alice');
    await user.type(screen.getByPlaceholderText('Your email'), 'alice@example.com');
    await user.click(screen.getByRole('button', { name: /Purchase/ }));

    const backLink = await screen.findByText('Back to Movie');
    expect(backLink).toBeInTheDocument();
    // Must link to the MOVIE id, not the show id
    expect(backLink.closest('a')).toHaveAttribute('href', '/movie/7');
  });

  it('failed purchase shows error and refreshes seat map', async () => {
    mockPurchaseTickets.mockResolvedValue({
      success: false,
      message: 'Some seats were already taken. Please refresh and try again.',
      tickets: [],
    });
    const user = userEvent.setup();
    renderWithRoute();

    await screen.findByText('Inception');

    await user.click(screen.getByTitle('A1'));
    await user.type(screen.getByPlaceholderText('Your name'), 'Alice');
    await user.type(screen.getByPlaceholderText('Your email'), 'alice@example.com');
    await user.click(screen.getByRole('button', { name: /Purchase/ }));

    expect(await screen.findByText(/already taken/)).toBeInTheDocument();
    // getShowDetail called twice: initial load + refresh after failure
    expect(mockGetShowDetail).toHaveBeenCalledTimes(2);
  });

  it('shows loading state initially', () => {
    // Never resolve the promise to keep loading state
    mockGetShowDetail.mockReturnValue(new Promise(() => {}));
    renderWithRoute();

    expect(screen.getByText('Loading...')).toBeInTheDocument();
  });

  it('shows not found for non-existent show', async () => {
    mockGetShowDetail.mockResolvedValue(undefined);
    renderWithRoute('999');

    expect(await screen.findByText('Show not found')).toBeInTheDocument();
  });
});
