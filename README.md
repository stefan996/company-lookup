# Backend Service for Company Data Verification

## Project Overview

This application is a backend service designed to interact with third-party APIs, handle failures gracefully
and provide structured company data in a standardized format. 
It acts as an intermediary between clients and external company databases, ensuring data retrieval, fallback mechanisms 
and verification storage.

## Requirements

This service uses:

- **PostgreSQL 17**
- **Java 21**

### Database Setup

After installing **PostgreSQL 17**, follow these steps to set up the database.  

First, **_navigate to the root of the project directory_** and execute the following commands:

1. **Connect to PostgreSQL:**
   Open a PostgreSQL shell and connect to the default `postgres` database:
   ```bash
   psql -d postgres
This command opens an interactive PostgreSQL session connected to the postgres database.

2. Create a new database for the application:
    ```bash
    CREATE DATABASE company_lookup;
This creates a new database named **company_lookup**, which will be used by the backend service.

3. Initialize the database schema:
    ```bash
    \i src/main/resources/db/init.sql
This command runs the SQL script which sets up the necessary tables and initial data for the application.


## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/stefan996/company-lookup
   
2. Build the application:
   ```bashs
   mvn clean install
   
3. Run the application:
   ```bash
   mvn spring-boot:run

The service will start running on http://localhost:8080.


## Dockerization

A **Dockerfile** is provided for easy containerization of the application. The Docker setup consists of two stages:


1. Build the Docker image
   ```bash
   docker compose build

2. Run the container
   ```bash
   docker-compose up

3. If you encounter any difficulties with installation or dockerization, you can simply pull the Docker image 
from my repository using the following command:
   ```bash
     docker pull stefanstevovic/company-lookup:latest

## Swagger documentation

After starting the server, the Swagger documentation can be found here:
1. http://localhost:8080/swagger-ui/index.html
2. http://localhost:8080/v3/api-docs
