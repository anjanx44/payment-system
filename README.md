# Payment System

This project uses Quarkus, the Supersonic Subatomic Java Framework, and implements a multi-provider payment system with PostgreSQL integration, Flyway for schema management, and multi-threaded processing.

If you want to learn more about Quarkus, visit the official website: [Quarkus](https://quarkus.io/).

## Project Setup

Follow the instructions below to set up and run the project.

### 1. Database Setup

You need to create a PostgreSQL database to store the payment system's data. Follow these steps:

1. **Install PostgreSQL** (if not already installed):
    - On Linux: `sudo apt-get install postgresql postgresql-contrib`
    - On macOS (via Homebrew): `brew install postgresql`
    - On Windows: Use the installer from the [PostgreSQL website](https://www.postgresql.org/download/).

2. **Create the Database**:
    - Connect to PostgreSQL using the command line or pgAdmin.
    - Run the following SQL command to create the database:
      ```sql
      CREATE DATABASE payment_system;
      ```

3. **Apply Migrations with Flyway**:
   Flyway will automatically apply the necessary migrations when the application starts.


## Environment Setup

To set up the environment variables for the project, follow these steps:

1. **Create a `.env` file** in the project root directory by copying the example environment configuration file:

    ```bash
    cp .env.example .env
    ```

2. **Edit the `.env` file** and update the values with your actual configuration:

    - Set the `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` to match your PostgreSQL database configuration.
    - If applicable, add any other necessary environment variables, such as API keys for payment providers.

   Example of `.env` file:

    ```bash
    # Database Configuration
    DB_URL=jdbc:postgresql://localhost:5432/payment_system
    DB_USERNAME=your_db_username
    DB_PASSWORD=your_db_password

    # Optional: Add payment provider API keys if needed
    # PROVIDER_1_API_KEY=your_provider_1_api_key
    # PROVIDER_2_API_KEY=your_provider_2_api_key
    ```

Once you have set up the `.env` file, you can proceed with running the application.


## Running the Application

### 1. Running the Application in Development Mode

In development mode, Quarkus provides live coding, allowing you to see changes instantly without restarting the application.

1. Open a terminal in the project root directory.

2. Run the following command to start the application in dev mode:

    ```bash
    ./mvnw quarkus:dev
    ```

3. The application will start, and you can access it at [http://localhost:8080](http://localhost:8080).

4. You can also access the Quarkus Dev UI at [http://localhost:8080/q/dev/](http://localhost:8080/q/dev/), where you can inspect logs, manage application settings, and more.

### 2. Running the Application in Production Mode

To run the application in production mode, you need to package it first and then execute the packaged application.

1. **Package the application**:

    ```bash
    ./mvnw package
    ```

   This will generate a `quarkus-run.jar` file in the `target/quarkus-app/` directory.

2. **Run the application**:

    ```bash
    java -jar target/quarkus-app/quarkus-run.jar
    ```

   Your application will now be running in production mode. You can access it at [http://localhost:8080](http://localhost:8080).

### 3. Running the Application as an Uber-JAR (Optional)

If you want to build a single executable JAR file that includes all dependencies (an "uber-jar"), you can use this command:

1. **Build the uber-jar**:

    ```bash
    ./mvnw package -Dquarkus.package.jar.type=uber-jar
    ```

2. **Run the uber-jar**:

    ```bash
    java -jar target/*-runner.jar
    ```

### 4. Running the Application as a Native Executable (Optional)

If you prefer to build a native executable for faster startup times and reduced memory usage, you can build it using GraalVM or via a container if GraalVM is not installed.

1. **Build the native executable**:

    ```bash
    ./mvnw package -Dnative
    ```

   Or, if you don't have GraalVM installed:

    ```bash
    ./mvnw package -Dnative -Dquarkus.native.container-build=true
    ```

2. **Run the native executable**:

    ```bash
    ./target/payment-system-1.0.0-SNAPSHOT-runner
    ```

---

### Verify the Application

Once the application is running, you can verify that it works correctly by accessing the API endpoints using Postman or by visiting the exposed REST endpoints in a web browser, depending on your project setup.


## Importing the Postman Collection

1. **Download or locate the Postman collection file**: The Postman collection file (`payment-system.postman_collection.json`) should be included in the project repository. Ensure the file is available in the project directory or download it if needed.

2. **Open Postman**: Launch the Postman application on your machine.

3. **Import the collection**:
    - Click on the **Import** button in the top left corner of the Postman interface.
    - Select the **File** tab and browse to the `payment-system.postman_collection.json` file.
    - Click **Open** to import the collection into Postman.

4. **Use the imported collection**: Once the collection is imported, you can view and test all available API endpoints for the Payment System. Ensure your application is running before sending requests from Postman.

This will enable you to interact with the Payment System API directly and test the endpoints.


