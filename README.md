# Invoice Processing System

A full-stack invoice processing and management system built with **React.js** and **Spring Boot**. The application provides secure user authentication, customer and product management, invoice creation, GST and discount calculations, and dashboard statistics.

## 🚀 Tech Stack

### Frontend

* React.js
* JavaScript
* React Router
* Axios
* HTML5
* CSS3
* Vite

### Backend

* Java 21
* Spring Boot
* Spring Security
* JWT Authentication
* Spring Data JPA
* Hibernate
* Maven

### Database

* MySQL

### Tools & DevOps

* Docker
* Docker Compose
* Git
* GitHub
* Postman

## ✨ Features

* 🔐 JWT-based user authentication
* 👤 Customer management
* 📦 Product management
* 🧾 Invoice creation and management
* 💰 GST and discount calculation
* 📊 Dashboard statistics
* 📉 Automatic stock deduction when invoices are created
* 🔒 Secured REST APIs using Spring Security
* 🐳 Docker-based application setup
* 🗄️ Persistent MySQL database

## 🏗️ Architecture

The application follows a full-stack architecture:

```text
React.js Frontend
       │
       │ REST API / Axios
       ▼
Spring Boot Backend
       │
       ├── Spring Security + JWT
       ├── Controllers
       ├── Services
       ├── Repositories
       └── Hibernate / JPA
              │
              ▼
           MySQL
```

## 📁 Project Structure

```text
invoice-processing-system/
│
├── invoice-backend/
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── ...
│   ├── Dockerfile
│   └── pom.xml
│
├── invoice-frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── context/
│   │   ├── pages/
│   │   └── ...
│   ├── Dockerfile
│   └── package.json
│
├── docker-compose.yml
└── README.md
```

## 🔑 Authentication

The application uses **Spring Security with JWT-based authentication**.

Authentication flow:

```text
User Login
    ↓
Spring Boot Authentication
    ↓
JWT Token Generated
    ↓
Token Stored by Frontend
    ↓
Token Sent with API Requests
    ↓
JWT Filter Validates Token
    ↓
Protected API Access
```

## 🧾 Invoice Workflow

```text
Login
  ↓
Select Customer
  ↓
Select Products
  ↓
Enter Quantity
  ↓
Calculate Subtotal
  ↓
Apply Discount
  ↓
Calculate GST
  ↓
Generate Invoice
  ↓
Update Product Stock
```

## 🐳 Running with Docker

Make sure **Docker Desktop** is installed and running.

Clone the repository:

```bash
git clone <your-repository-url>
cd invoice-processing-system
```

Build and start the containers:

```bash
docker compose up --build
```

To run the containers in detached mode:

```bash
docker compose up --build -d
```

To stop the application:

```bash
docker compose down
```

To remove containers and database volumes:

```bash
docker compose down -v
```

> **Warning:** `docker compose down -v` removes the database volume and can delete stored database data.

## 🌐 Application

After starting the containers, access the frontend through the configured frontend port and the backend through the configured Spring Boot API port.

Example development setup:

```text
Frontend: http://localhost:5173
Backend:  http://localhost:8080
```

## 🧪 API Testing

REST APIs can be tested using **Postman**.

Example authentication endpoints:

```text
POST /api/auth/register
POST /api/auth/login
```

Protected endpoints require the JWT token in the request authorization header.

## 📌 Future Improvements

* Invoice PDF generation and download
* Advanced invoice search and filtering
* Role-based permissions
* Improved dashboard analytics
* Cloud deployment
* Automated testing and CI/CD

## 👨‍💻 Author

**Priyanshu Harshvardhan**

Information Science and Engineering
Nitte Meenakshi Institute of Technology

