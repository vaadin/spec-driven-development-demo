# Data Model

> Entity definitions and relationships. Evolves as features are added.

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| Movie | id (Long), title (String), description (String), posterFileName (String), durationMinutes (int) | Has many Screening |
| Screening | id (Long), startTime (LocalDateTime), availableSeats (int) | ManyToOne → Movie, Has many Booking |
| Booking | id (Long), confirmationCode (String), customerName (String), customerEmail (String), ticketCount (int) | ManyToOne → Screening |
