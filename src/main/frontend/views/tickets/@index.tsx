import { useState, useEffect } from "react";
import { useNavigate } from "react-router";
import type { ViewConfig } from "@vaadin/hilla-file-router/types.js";
import { TicketEndpoint } from "Frontend/generated/endpoints.js";
import type TicketSummary from "Frontend/generated/com/example/specdriven/endpoint/TicketEndpoint/TicketSummary.js";
import Status from "Frontend/generated/com/example/specdriven/model/Status.js";

export const config: ViewConfig = {
  title: "My Tickets",
  flowLayout: false,
};

const statusOptions = [
  { label: "All", value: "" },
  ...Object.values(Status).map((s: string) => ({
    label: s.replace("_", " "),
    value: s,
  })),
];

function statusClass(status?: string): string {
  if (!status) return "";
  return "status-" + status.toLowerCase().replace("_", "-");
}

function priorityClass(priority?: string): string {
  if (!priority) return "";
  return "priority-" + priority.toLowerCase();
}

export default function MyTicketsView() {
  const navigate = useNavigate();
  const [tickets, setTickets] = useState<TicketSummary[]>([]);
  const [statusFilter, setStatusFilter] = useState<string>("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTickets();
  }, [statusFilter]);

  const loadTickets = async () => {
    setLoading(true);
    try {
      const filter = statusFilter ? (statusFilter as Status) : undefined;
      const result = await TicketEndpoint.getMyTickets(filter);
      setTickets((result ?? []).filter((t): t is TicketSummary => t !== undefined));
    } catch (e) {
      console.error("Failed to load tickets", e);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="customer-page">
      <header className="customer-header">
        <a href="/tickets" className="logo-link">
          <img src="/icons/icon.svg" alt="re:solve" className="logo-icon" />
          <span className="logo-text">re:solve</span>
        </a>
        <nav className="customer-nav">
          <a href="/submit" className="nav-link">Submit Ticket</a>
          <a href="/tickets" className="nav-link active">My Tickets</a>
          <a href="/logout" className="nav-link logout">Log out</a>
        </nav>
      </header>
      <main className="customer-content">
        <div className="page-header">
          <h1>My Tickets</h1>
          <div className="filter-bar">
            <label htmlFor="status-filter">Filter by status:</label>
            <select
              id="status-filter"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              {statusOptions.map((opt) => (
                <option key={opt.value} value={opt.value}>
                  {opt.label}
                </option>
              ))}
            </select>
          </div>
        </div>

        {loading ? (
          <p className="loading-text">Loading...</p>
        ) : tickets.length === 0 ? (
          <div className="empty-state">
            <p>No tickets found.</p>
            <a href="/submit" className="submit-link">Submit your first ticket</a>
          </div>
        ) : (
          <div className="ticket-list">
            {tickets.map((ticket) => (
              <div
                key={ticket.id}
                className="ticket-card"
                onClick={() => navigate(`/tickets/${ticket.id}`)}
              >
                <div className="ticket-card-header">
                  <span className="ticket-title">#{ticket.id} {ticket.title}</span>
                  <span className={`status-badge ${statusClass(ticket.status)}`}>
                    {ticket.status?.replace("_", " ")}
                  </span>
                </div>
                <div className="ticket-card-meta">
                  <span className={`priority-badge ${priorityClass(ticket.priority)}`}>
                    {ticket.priority}
                  </span>
                  <span className="category-badge">{ticket.category}</span>
                  <span className="ticket-date">{ticket.createdDate}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
