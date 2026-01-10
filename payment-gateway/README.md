# Payment Gateway with Multi-Method Processing

A full-stack **Payment Gateway simulation** supporting **Orders, Payments, Merchant Dashboard, and Public Checkout**, built using **Spring Boot, PostgreSQL, Redis, and React**, and fully **Dockerized**.

---

## 🚀 Features

### Backend (Spring Boot)
- ✅ Merchant authentication using **API Key & Secret**
- ✅ Create and fetch **Orders**
- ✅ Create and poll **Payments**
- ✅ Supports **UPI** and **Card** payment methods
- ✅ Simulated payment processing (success/failure)
- ✅ **Public checkout APIs** (no authentication)
- ✅ Redis-based background worker
- ✅ Health checks for DB, Redis, and worker

### Merchant Dashboard (Port `3000`)
- 📊 Displays API credentials
- 💰 Total transactions
- ✅ Total successful amount
- 📈 Success rate
- 📋 Transactions list

### Checkout Page (Port `3001`)
- 🔗 Accepts `order_id` as query parameter
- 📦 Fetches order details
- 💳 Supports UPI and Card payments
- 📱 Shows:
  - Processing state
  - Success state
  - Failure state
- 🔄 Polls payment status every 2 seconds

### Dockerized Setup
- 🐘 PostgreSQL
- 🔴 Redis
- ⚙️ Backend API
- 📊 Dashboard UI
- 💳 Checkout UI

---

## 🧱 Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot |
| Database | PostgreSQL |
| Cache / Worker | Redis |
| Frontend | React (Vite) |
| Web Server | Nginx |
| Containerization | Docker & Docker Compose |

---

## 📁 Project Structure

```
payment-gateway/
├── backend/              # Spring Boot API
├── frontend/             # Merchant Dashboard (React)
├── checkout-page/        # Checkout UI (React)
├── docker-compose.yml
├── .env.example
├── .env
└── README.md
```

---

## ⚙️ Environment Variables

Create a `.env` file using `.env.example`:

```env
# Database Configuration
DATABASE_URL=jdbc:postgresql://postgres:5432/payment_gateway
DATABASE_USERNAME=gateway_user
DATABASE_PASSWORD=gateway_pass

# Test Merchant Credentials
TEST_MERCHANT_EMAIL=test@example.com
TEST_API_KEY=key_test_abc123
TEST_API_SECRET=secret_test_xyz789

# Payment Simulation Settings
TEST_MODE=true
TEST_PAYMENT_SUCCESS=true
TEST_PROCESSING_DELAY=1000
```

---

## 🐳 Running the Project (Docker)

From the project root directory:

```bash
# Clean up any existing containers
docker compose down -v

# Build and start all services
docker compose up --build -d
```

### Running Services

| Service | Port |
|---------|------|
| Backend API | 8000 |
| Merchant Dashboard | 3000 |
| Checkout Page | 3001 |
| PostgreSQL | 5432 |
| Redis | 6379 |

---

## 🔐 Authentication

Merchant APIs require the following headers:

```
X-Api-Key: key_test_abc123
X-Api-Secret: secret_test_xyz789
```

---

## 📡 API Endpoints

### 🩺 Health Check

```http
GET /health
```

### 📦 Orders

#### Create Order (Merchant)

```http
POST /api/v1/orders
Headers:
  X-Api-Key: key_test_abc123
  X-Api-Secret: secret_test_xyz789
```

**Request Body:**
```json
{
  "amount": 10000,
  "currency": "INR",
  "receipt": "receipt_001"
}
```

#### Get Order (Public – Checkout)

```http
GET /api/v1/orders/public/{order_id}
```

### 💸 Payments

#### Create Payment (Public – Checkout)

```http
POST /api/v1/payments/public
```

**UPI Example:**
```json
{
  "order_id": "order_xxx",
  "method": "upi",
  "vpa": "user@bank"
}
```

**Card Example:**
```json
{
  "order_id": "order_xxx",
  "method": "card",
  "card": {
    "number": "4111111111111111",
    "expiryMonth": "12",
    "expiryYear": "26",
    "cvv": "123"
  }
}
```

#### Poll Payment Status

```http
GET /api/v1/payments/public/{payment_id}
```

#### List Payments (Merchant Dashboard)

```http
GET /api/v1/payments
Headers:
  X-Api-Key: key_test_abc123
  X-Api-Secret: secret_test_xyz789
```

### 📊 Dashboard Stats

```http
GET /api/v1/dashboard
Headers:
  X-Api-Key: key_test_abc123
  X-Api-Secret: secret_test_xyz789
```

---

## 🧾 Checkout Flow

1. **Merchant creates an order** using the Orders API
2. **User opens checkout page:**
   ```
   http://localhost:3001/?order_id=order_xxx
   ```
3. **User selects UPI or Card**
4. **Payment is created** via public API
5. **Frontend polls payment status**
6. **Displays success or failure**
7. **Payment appears in merchant dashboard**

---

## 🧪 Test Merchant Credentials

| Field | Value |
|-------|-------|
| Email | test@example.com |
| API Key | key_test_abc123 |
| API Secret | secret_test_xyz789 |

---

## 🧠 Payment Simulation Logic

- Payments are initially created with `processing` status
- After a configurable delay, status changes to: `success` or `failed`
- Controlled using environment variables:
  ```env
  TEST_MODE=true
  TEST_PAYMENT_SUCCESS=true
  TEST_PROCESSING_DELAY=1000
  ```

---

## 🐞 Common Issues & Notes

| Issue | Solution |
|-------|----------|
| 401 Unauthorized | Missing or invalid API headers |
| 403 Forbidden on checkout polling | Ensure public endpoints are used |
| All payments succeed | Expected in test mode (deterministic behavior) |

---

## 📦 Frontend Dockerization

Both frontend applications:
- Built using `npm run build`
- Served using **Nginx**
- Exposed on ports **3000** and **3001**

---

## 📝 License

This project is for educational and demonstration purposes.

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

---

## 👨‍💻 Author

Built with ❤️ using Spring Boot, React, and Docker