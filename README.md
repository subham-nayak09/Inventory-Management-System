# Inventory Management System

A microservices-based e-commerce backend built with Spring Boot, featuring service discovery, API gateway, event-driven communication, and containerized infrastructure.

🏗️ Architecture
Client
└── API Gateway (Spring Cloud Gateway)
├── Product Service
├── Order Service
│     └── Inventory Service (via WebClient)
└── Notification Service (via Kafka)

Discovery Server (Eureka) — all services register here

📦 Services
ServiceDescriptionapi-gatewayRoutes incoming requests to appropriate microservicesdiscovery-serverEureka-based service registry for service discoveryproduct-serviceManages product catalog (CRUD operations)order-serviceHandles order placement and checks inventory availabilityinventory-serviceTracks product stock levelsnotification-serviceSends notifications via Kafka events

🛠️ Tech Stack

Java 17 + Spring Boot 3
Spring Cloud (Eureka, Gateway, LoadBalancer)
Spring WebFlux (WebClient for inter-service communication)
Apache Kafka (event-driven notifications)
MySQL (persistent storage)
Docker & Docker Compose (infrastructure)
Confluent Schema Registry (Kafka schema management)


🚀 Getting Started
Prerequisites

Java 17+
Maven
Docker & Docker Compose

1. Start Infrastructure
   Start MySQL, Kafka, Zookeeper, Schema Registry, and Kafka UI:
   bashdocker compose up -d
   Verify all containers are running:
   bashdocker compose ps
   ServiceURLKafka UIhttp://localhost:8086Schema Registryhttp://localhost:8085MySQLlocalhost:3307
2. Start Microservices
   Start services in this order:
   bash# 1. Discovery Server (Eureka)
   cd discovery-server && mvn spring-boot:run

# 2. Core Services (any order)
cd product-service && mvn spring-boot:run
cd inventory-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd order-service && mvn spring-boot:run

# 3. API Gateway (last)
cd api-gateway && mvn spring-boot:run
3. Verify Services
   Open Eureka Dashboard to confirm all services are registered:
   http://localhost:8761

🔗 API Endpoints
All requests go through the API Gateway.
Product Service
MethodEndpointDescriptionPOST/api/productCreate a productGET/api/productGet all products
Order Service
MethodEndpointDescriptionPOST/api/orderPlace an order
Inventory Service
MethodEndpointDescriptionGET/api/inventoryCheck stock by SKU code

⚙️ Configuration
Each service has its own application.properties. Key settings:
properties# Service Discovery
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka
eureka.instance.prefer-ip-address=true

# Database (inventory, order, product services)
spring.datasource.url=jdbc:mysql://localhost:3307/<db_name>
spring.datasource.username=root
spring.datasource.password=mysql

# Kafka (order, notification services)
spring.kafka.bootstrap-servers=localhost:9092

🐳 Docker Infrastructure
The docker-compose.yml includes:

MySQL 8.3 — Database (host port 3307)
Zookeeper — Kafka coordination (port 2181)
Kafka Broker — Message broker (port 9092)
Schema Registry — Kafka schema management (port 8085)
Kafka UI — Visual Kafka management (port 8086)


📁 Project Structure
store-manager/
├── api-gateway/
├── discovery-server/
├── inventory-service/
├── notification-service/
├── order-service/
├── product-service/
├── docker-compose.yml
├── pom.xml
└── README.md
