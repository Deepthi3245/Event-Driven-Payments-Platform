# Microservice Fintech Platform

A distributed fintech platform built with microservices architecture implementing the Saga pattern for managing distributed transactions across wallet and payment services.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Understanding the Saga Pattern](#understanding-the-saga-pattern)
- [Services](#services)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Observability](#observability)
- [Screenshots](#screenshots)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)

## Overview

This project demonstrates a production-grade microservices-based fintech platform that handles financial transactions with distributed transaction management using the Saga orchestration pattern. The system ensures data consistency across multiple services without traditional two-phase commit protocols.

## Architecture

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          Client Applications                            │
└───────────────────────────────┬─────────────────────────────────────────┘
                                │
                                │ HTTP Requests
                                ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                         Eureka Server (Service Discovery)                 │
└───────────────────────────────────────────────────────────────────────────┘
                                │
                ┌───────────────┼───────────────┐
                │               │               │
                ▼               ▼               ▼
    ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
    │ Wallet Service   │  │ Ledger Service   │  │ Payment Service  │
    │                  │  │                  │  │                  │
    │ - Account Mgmt   │  │ - Transaction    │  │ - Payment        │
    │ - Balance Ops    │  │   Recording      │  │   Processing     │
    └────────┬─────────┘  └────────┬─────────┘  └────────┬─────────┘
             │                     │                     │
             │                     │                     │
             └──────────┬──────────┴──────────┬──────────┘
                        │                     │
                        │    Kafka Events     │
                        ▼                     ▼
             ┌──────────────────────────────────────────┐
             │         Apache Kafka Message Bus         │
             │                                          │
             │  Topics:                                 │
             │  - transaction-events                    │
             │  - wallet-events                         │
             │  - payment-events                        │
             └──────────────────┬───────────────────────┘
                                │
                ┌───────────────┼───────────────┐
                ▼               ▼               ▼
    ┌──────────────────────────────────────────────────────┐
    │       Transaction Orchestrator Service               │
    │                                                      │
    │  - Saga Orchestration                               │
    │  - Transaction Coordination                         │
    │  - Compensation Handling                            │
    └──────────────────┬───────────────────────────────────┘
                       │
                       ▼
         ┌────────────────────────────┐
         │  Event Consumer Service    │
         │                            │
         │  - Event Processing        │
         │  - State Management        │
         └────────────────────────────┘
```

### Observability Stack

```
┌───────────────────────────────────────────────────────────────┐
│                     All Microservices                         │
│                                                               │
│  Generate: Traces, Logs, Metrics                            │
└───────────────────────┬───────────────────────────────────────┘
                        │
                        │ OTLP Protocol
                        ▼
            ┌───────────────────────────┐
            │  OpenTelemetry Collector  │
            │                           │
            │  - Receives telemetry     │
            │  - Processes & Routes     │
            └─────┬──────────┬─────┬────┘
                  │          │     │
        ┌─────────┘          │     └─────────┐
        │                    │               │
        ▼                    ▼               ▼
┌──────────────┐    ┌──────────────┐  ┌──────────────┐
│    Tempo     │    │     Loki     │  │  Prometheus  │
│              │    │              │  │              │
│   Traces     │    │     Logs     │  │   Metrics    │
└──────────────┘    └──────────────┘  └──────────────┘
        │                    │               │
        └──────────┬─────────┴───────┬───────┘
                   │                 │
                   ▼                 ▼
           ┌────────────────────────────┐
           │        Grafana             │
           │                            │
           │  - Unified Dashboard       │
           │  - Visualization           │
           │  - Alerting                │
           └────────────────────────────┘
```

## Understanding the Saga Pattern

### What is the Saga Pattern?

The Saga pattern is a design pattern for managing distributed transactions across multiple microservices. Unlike traditional ACID transactions that use two-phase commit (2PC), Sagas break down a transaction into a series of local transactions, each with its own compensating action.

### Why Use the Saga Pattern?

**Traditional Challenges:**
- Distributed transactions with 2PC are complex and can cause performance bottlenecks
- Locking resources across services reduces system availability
- Services become tightly coupled

**Saga Pattern Benefits:**
- Maintains data consistency without distributed locks
- Each service can commit its local transaction independently
- Improved system availability and scalability
- Loose coupling between services

### Saga Pattern Types

#### 1. Choreography-Based Saga
Services communicate through events without central coordination. Each service listens to events and decides what to do next.

#### 2. Orchestration-Based Saga (Used in This Project)
A central orchestrator (Transaction Orchestrator Service) coordinates the saga workflow and manages compensations.

### How It Works in This Project

**Transaction Flow Example: Wallet Transfer**

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Transaction Initiated                           │
│                                                                     │
│  User requests: Transfer $100 from Wallet A to Wallet B            │
└───────────────────────────┬─────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Step 1: Transaction Orchestrator Starts Saga                       │
│                                                                     │
│  - Creates transaction record                                       │
│  - Publishes "TransactionStarted" event                            │
└───────────────────────────┬─────────────────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────���────────────────────────────────────┐
│  Step 2: Wallet Service - Debit Operation                          │
│                                                                     │
│  ✓ Deduct $100 from Wallet A                                       │
│  ✓ Publish "WalletDebited" event                                   │
│  ✓ Store compensation: Credit $100 back to Wallet A                │
└───────────────────────────┬─────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Step 3: Payment Simulator - Process Payment                       │
│                                                                     │
│  ✓ Validate payment                                                │
│  ✓ Publish "PaymentProcessed" event                                │
│  ✓ Store compensation: Reverse payment                             │
└───────────────────────────┬─────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Step 4: Wallet Service - Credit Operation                         │
│                                                                     │
│  ✓ Add $100 to Wallet B                                            │
│  ✓ Publish "WalletCredited" event                                  │
│  ✓ Store compensation: Debit $100 from Wallet B                    │
└───────────────────────────┬─────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Step 5: Ledger Service - Record Transaction                       │
│                                                                     │
│  ✓ Record transaction in ledger                                    │
│  ✓ Publish "TransactionRecorded" event                             │
│  ✓ Store compensation: Delete ledger entry                         │
└───────────────────────────┬─────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Step 6: Transaction Orchestrator Completes Saga                   │
│                                                                     │
│  ✓ All steps completed successfully                                │
│  ✓ Mark transaction as COMPLETED                                   │
└─────────────────────────────────────────────────────────────────────┘
```

**Failure & Compensation Flow:**

```
┌─────────────────────────────────────────────────────────────────────┐
│  Failure Scenario: Step 4 Fails (Wallet B credit fails)            │
└───────────────────────────┬─────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Orchestrator Detects Failure                                       │
│                                                                     │
│  - Receives "WalletCreditFailed" event                             │
│  - Initiates compensation workflow                                  │
└───────────────────────────┬─────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Compensation Step 1: Reverse Payment                               │
│                                                                     │
│  ✓ Execute payment reversal                                        │
│  ✓ Publish "PaymentReversed" event                                 │
└───────────────────────────┬─────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Compensation Step 2: Credit Back to Wallet A                      │
│                                                                     │
│  ✓ Credit $100 back to Wallet A                                    │
│  ✓ Publish "WalletCreditedBack" event                              │
└───────────────────────────┬─────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────────┐
│  Saga Completed with Compensation                                   │
│                                                                     │
│  ✓ All compensations executed                                      │
│  ✓ Mark transaction as COMPENSATED/FAILED                          │
│  ✓ System state rolled back to initial state                       │
└─────────────────────────────────────────────────────────────────────┘
```

### Key Concepts

**Local Transaction:**
Each service executes its own database transaction independently.

**Compensating Transaction:**
An operation that undoes the effect of a previously committed local transaction. Every step in the saga must have a compensating action.

**Idempotency:**
All operations (both forward and compensating) must be idempotent to handle duplicate messages safely.

**Event Sourcing:**
The system publishes events for each step, creating an audit trail of all state changes.

## Services

### Transaction Orchestrator Service
**Port:** 8081

Central coordinator for saga orchestration:
- Manages transaction workflows
- Coordinates between services
- Handles compensation logic
- Maintains transaction state
- Publishes and consumes Kafka events

### Wallet Service
**Port:** 8082

Manages digital wallet operations:
- Create and manage wallets
- Debit/Credit operations
- Balance inquiries
- Account management
- Publishes wallet events

### Ledger Service
**Port:** 8083

Maintains transaction records:
- Records all financial transactions
- Audit trail management
- Transaction history
- Balance reconciliation
- Financial reporting

### Payment Simulator Service
**Port:** 8084

Simulates external payment gateway:
- Payment processing simulation
- Success/failure scenarios
- Payment validation
- Gateway response simulation

### Event Consumer Service
**Port:** 8085

Processes events from Kafka:
- Consumes events from all topics
- Event-driven processing
- State updates
- Analytics and monitoring

### Eureka Server
**Port:** 8761

Service discovery and registration:
- Service registry
- Health monitoring
- Load balancing support
- Service discovery

## Technology Stack

### Core Technologies
- **Java** - Primary programming language
- **Spring Boot** - Microservices framework
- **Spring Cloud** - Microservices infrastructure
- **Netflix Eureka** - Service discovery

### Messaging & Events
- **Apache Kafka** - Event streaming platform
- **Zookeeper** - Kafka coordination

### Observability
- **OpenTelemetry** - Unified observability
- **Grafana Tempo** - Distributed tracing
- **Grafana Loki** - Log aggregation
- **Prometheus** - Metrics collection
- **Grafana** - Visualization dashboard

### Infrastructure
- **Docker** - Containerization
- **Docker Compose** - Container orchestration

### Additional Tools
- **Lombok** - Reduce boilerplate code

## Prerequisites

Before running this application, ensure you have the following installed:

- **Java JDK 17 or higher**
- **Maven 3.6+**
- **Docker 20.10+**
- **Docker Compose 2.0+**
- **Git**

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/rishabhrawat05/microservice-fintech.git
cd microservice-fintech
```

### 2. Start Infrastructure Services

Start Kafka and Zookeeper:

```bash
docker-compose up -d
```

This will start:
- Zookeeper on port 2181
- Kafka on port 9092

### 3. Start Observability Stack (Optional)

For monitoring and observability:

```bash
cd observability
docker-compose up -d
cd ..
```

This will start:
- OpenTelemetry Collector (ports 4317, 4318)
- Grafana Tempo (port 3200)
- Loki (port 3100)
- Prometheus (port 9090)
- Grafana (port 3000)

### 4. Build All Services

```bash
# Build each service
cd eureka-server && mvn clean install && cd ..
cd transaction-orchestrator-service && mvn clean install && cd ..
cd wallet-service && mvn clean install && cd ..
cd ledger-service && mvn clean install && cd ..
cd payment-simulator-service && mvn clean install && cd ..
cd event-consumer-service && mvn clean install && cd ..
```

### 5. Start Services

Start services in the following order:

```bash
# 1. Start Eureka Server
cd eureka-server && mvn spring-boot:run &

# Wait 30 seconds for Eureka to start

# 2. Start all other services
cd transaction-orchestrator-service && mvn spring-boot:run &
cd wallet-service && mvn spring-boot:run &
cd ledger-service && mvn spring-boot:run &
cd payment-simulator-service && mvn spring-boot:run &
cd event-consumer-service && mvn spring-boot:run &
```

### 6. Verify Services

Check Eureka Dashboard to verify all services are registered:

```
http://localhost:8761
```

## Observability

### Access Grafana Dashboard

1. Open Grafana: `http://localhost:3000`
2. Default credentials: `admin/admin`
3. Configure data sources:
   - Tempo for traces
   - Loki for logs
   - Prometheus for metrics

### Viewing Traces

Navigate to Explore > Tempo and search for traces by:
- Service name
- Operation name
- Tags
- Duration

### Viewing Logs

Navigate to Explore > Loki and query logs:

```logql
{service="wallet-service"} |= "transaction"
```

### Viewing Metrics

Navigate to Explore > Prometheus and query metrics:

```promql
rate(http_server_requests_seconds_count[5m])
```

### Observability Screenshots

#### Distributed Tracing
<img width="2872" height="1363" alt="Screenshot 2026-02-16 201041" src="https://github.com/user-attachments/assets/4fe2446b-88c5-438f-9c91-bc3042f15fe8" />

## Project Structure

```
microservice-fintech/
├── docker-compose.yml
├── lombok.jar
├── eureka-server/
├── transaction-orchestrator-service/
├── wallet-service/
├── ledger-service/
├── payment-simulator-service/
├── event-consumer-service/
├── walletra-events/
├── kafka/
│   └── docker-compose.yml
└── observability/
    ├── docker-compose.yml
    ├── otel-collector-config.yaml
    └── tempo-config.yaml
```
Inspired from rishab
## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request


## Contact

For questions or support, please open an issue in the GitHub repository.

## Acknowledgments

- Spring Boot team for excellent microservices framework
- Apache Kafka for robust event streaming
- Grafana Labs for observability tools
- Netflix for Eureka service discovery
