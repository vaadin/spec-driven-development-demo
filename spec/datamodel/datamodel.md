# Data Model

> Entity definitions and relationships. Evolves as features are added.

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| Show | id, title, description, coverImageUrl, dateTime, price, availableSeats | Has many Purchase |
| Purchase | id, confirmationCode (UUID), quantity, totalPrice, cardLastFour, purchasedAt | Belongs to Show |
