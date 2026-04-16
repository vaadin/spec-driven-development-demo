# Data Model

> Entity definitions and relationships. Evolves as features are added.

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| Product | id, name, sku, category, unitPrice, reorderPoint, currentStock, refillDate | Has many StockEvent |
| StockEvent | id, type (RECEIVED, ADJUSTMENT), quantity, reason, timestamp | Belongs to Product, optionally belongs to Shipment |
| Shipment | id, shipmentNumber, date, supplierName, status (PENDING, RECEIVED), productCount, itemCount | Has many StockEvent |

---

## Field Notes

**Product.refillDate**: Expected date when the next shipment/reorder for this product will arrive. Used for prioritizing inventory alerts (products with distant refill dates and low/negative stock are highest priority).

**Shipment.status**: 
- `PENDING` — Shipment created but not yet confirmed/signed off by warehouse staff
- `RECEIVED` — Shipment confirmed as complete and stock levels updated

**Shipment.productCount**: Number of distinct products in the shipment

**Shipment.itemCount**: Total quantity of all items in the shipment (sum of quantities across all products)
