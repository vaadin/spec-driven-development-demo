import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router";
import type { ViewConfig } from "@vaadin/hilla-file-router/types.js";
import { TicketEndpoint } from "Frontend/generated/endpoints.js";
import type TicketDetail from "Frontend/generated/com/example/specdriven/endpoint/TicketEndpoint/TicketDetail.js";

export const config: ViewConfig = {
  title: "Ticket Detail",
  flowLayout: false,
};

function statusClass(status?: string): string {
  if (!status) return "";
  return "status-" + status.toLowerCase().replace("_", "-");
}

function priorityClass(priority?: string): string {
  if (!priority) return "";
  return "priority-" + priority.toLowerCase();
}

export default function TicketDetailView() {
  const { ticketId } = useParams();
  const navigate = useNavigate();
  const [ticket, setTicket] = useState<TicketDetail | undefined>(undefined);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (ticketId) {
      loadTicket(Number(ticketId));
    }
  }, [ticketId]);

  const loadTicket = async (id: number) => {
    setLoading(true);
    try {
      const result = await TicketEndpoint.getTicketDetail(id);
      setTicket(result);
    } catch (e) {
      console.error("Failed to load ticket", e);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
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
          <p className="loading-text">Loading...</p>
        </main>
      </div>
    );
  }

  if (!ticket) {
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
          <p>Ticket not found.</p>
        </main>
      </div>
    );
  }

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
        <button className="back-button" onClick={() => navigate("/tickets")}>
          &larr; Back to My Tickets
        </button>

        <div className="ticket-detail">
          <div className="ticket-detail-header">
            <h1>#{ticket.id} {ticket.title}</h1>
            <span className={`status-badge ${statusClass(ticket.status)}`}>
              {ticket.status?.replace("_", " ")}
            </span>
          </div>

          <div className="ticket-detail-meta">
            <div className="meta-item">
              <span className="meta-label">Category</span>
              <span className="category-badge">{ticket.category}</span>
            </div>
            <div className="meta-item">
              <span className="meta-label">Priority</span>
              <span className={`priority-badge ${priorityClass(ticket.priority)}`}>
                {ticket.priority}
              </span>
            </div>
            <div className="meta-item">
              <span className="meta-label">Created by</span>
              <span>{ticket.createdByName}</span>
            </div>
            {ticket.assignedToName && (
              <div className="meta-item">
                <span className="meta-label">Assigned to</span>
                <span>{ticket.assignedToName}</span>
              </div>
            )}
            <div className="meta-item">
              <span className="meta-label">Created</span>
              <span>{ticket.createdDate}</span>
            </div>
            <div className="meta-item">
              <span className="meta-label">Updated</span>
              <span>{ticket.updatedDate}</span>
            </div>
          </div>

          <div className="ticket-description">
            <h2>Description</h2>
            <p>{ticket.description}</p>
          </div>

          <div className="comments-section">
            <h2>Comments ({ticket.comments?.length ?? 0})</h2>
            {(!ticket.comments || ticket.comments.length === 0) ? (
              <p className="no-comments">No comments yet.</p>
            ) : (
              <div className="comments-list">
                {ticket.comments.filter((c): c is NonNullable<typeof c> => c !== undefined).map((comment) => (
                  <div key={comment.id} className="comment">
                    <div className="comment-header">
                      <span className="comment-author">{comment.authorName}</span>
                      <span className="comment-date">{comment.createdDate}</span>
                    </div>
                    <p className="comment-text">{comment.text}</p>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}
