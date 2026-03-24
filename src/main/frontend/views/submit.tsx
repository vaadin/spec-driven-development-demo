import { useState } from "react";
import { useNavigate } from "react-router";
import type { ViewConfig } from "@vaadin/hilla-file-router/types.js";
import { TicketEndpoint } from "Frontend/generated/endpoints.js";
import Category from "Frontend/generated/com/example/specdriven/model/Category.js";
import Priority from "Frontend/generated/com/example/specdriven/model/Priority.js";

export const config: ViewConfig = {
  title: "Submit Ticket",
  flowLayout: false,
};

const categoryOptions = Object.values(Category).map((c: string) => ({
  label: c.charAt(0) + c.slice(1).toLowerCase(),
  value: c,
}));

const priorityOptions = Object.values(Priority).map((p: string) => ({
  label: p.charAt(0) + p.slice(1).toLowerCase(),
  value: p,
}));

export default function SubmitTicketView() {
  const navigate = useNavigate();
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [category, setCategory] = useState<Category>(Category.GENERAL);
  const [priority, setPriority] = useState<Priority>(Priority.MEDIUM);
  const [titleError, setTitleError] = useState("");
  const [descriptionError, setDescriptionError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const validate = (): boolean => {
    let valid = true;
    if (!title.trim()) {
      setTitleError("Title is required");
      valid = false;
    } else {
      setTitleError("");
    }
    if (!description.trim()) {
      setDescriptionError("Description is required");
      valid = false;
    } else {
      setDescriptionError("");
    }
    return valid;
  };

  const handleSubmit = async () => {
    if (!validate()) return;
    setSubmitting(true);
    try {
      const ticket = await TicketEndpoint.submitTicket(title, description, category, priority);
      if (ticket?.id) {
        navigate("/tickets");
      }
    } catch (e) {
      console.error("Failed to submit ticket", e);
    } finally {
      setSubmitting(false);
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
          <a href="/submit" className="nav-link active">Submit Ticket</a>
          <a href="/tickets" className="nav-link">My Tickets</a>
          <a href="/logout" className="nav-link logout">Log out</a>
        </nav>
      </header>
      <main className="customer-content">
        <h1>Submit a Ticket</h1>
        <div className="form-container">
          <div className="form-field">
            <label htmlFor="title">Title *</label>
            <input
              id="title"
              type="text"
              placeholder="Brief summary of your issue"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className={titleError ? "field-error" : ""}
            />
            {titleError && <span className="error-message">{titleError}</span>}
          </div>

          <div className="form-row">
            <div className="form-field">
              <label htmlFor="category">Category</label>
              <select
                id="category"
                value={category}
                onChange={(e) => setCategory(e.target.value as Category)}
              >
                {categoryOptions.map((opt) => (
                  <option key={opt.value} value={opt.value}>
                    {opt.label}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-field">
              <label htmlFor="priority">Priority</label>
              <select
                id="priority"
                value={priority}
                onChange={(e) => setPriority(e.target.value as Priority)}
              >
                {priorityOptions.map((opt) => (
                  <option key={opt.value} value={opt.value}>
                    {opt.label}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-field">
            <label htmlFor="description">Description *</label>
            <textarea
              id="description"
              placeholder="Describe your issue in detail"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={6}
              className={descriptionError ? "field-error" : ""}
            />
            {descriptionError && <span className="error-message">{descriptionError}</span>}
          </div>

          <button
            className="submit-button"
            onClick={handleSubmit}
            disabled={submitting}
          >
            {submitting ? "Submitting..." : "Submit"}
          </button>
        </div>
      </main>
    </div>
  );
}
