# TechSage Capital

A modern banking platform built with Spring Boot and Vue.js.

## Prerequisites

- Java 21
- Node.js 22
- pnpm 10
- Docker
- Maven

## Project Structure

- `frontend/` - Vue.js application
- `backend/` - Spring Boot application
- `.github/workflows/` - CI/CD configuration

## Local Development

### Backend Setup

1. Navigate to the backend directory:

   ```bash
   cd backend
   ```

2. Set up JWT authentication:

   ```bash
   cd src/main/resources
   keytool -genkeypair \
     -keyalg RSA \
     -validity 365 \
     -alias inholland \
     -keystore inholland.p12 \
     -storetype PKCS12 \
     -storepass yourpassword \
     -keypass yourpassword \
     -dname "CN=inholland, OU=TechSage, O=Inholland, L=City, S=State, C=NL"
   ```

3. Copy the example properties file:

   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

4. Start the backend server:
   ```bash
   mvn spring-boot:run
   ```
   The backend will be available at `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:

   ```bash
   cd frontend
   ```

2. Install dependencies:

   ```bash
   pnpm install
   ```

3. Start the development server:
   ```bash
   pnpm dev
   ```
   The frontend will be available at `http://localhost:5173`

## Running Tests

### Backend Tests

1. Unit Tests:

   ```bash
   cd backend
   mvn clean -Dtest=ServiceTestSuite test
   ```

2. Integration Tests:
   ```bash
   cd backend
   mvn clean -Dtest=ControllerTestSuite test
   ```

### Frontend Tests

1. Linting:
   ```bash
   cd frontend
   pnpm run lint:check
   ```

## Docker Deployment

### Backend

1. Build the backend image:

   ```bash
   cd backend
   docker build -t springboot-app .
   ```

2. Run the backend container:
   ```bash
   docker run -d --restart=always --name springboot-app -p 8080:8080 -e TURNSTILE_SECRET=your_secret springboot-app
   ```

### Frontend

1. Build the frontend image:

   ```bash
   cd frontend
   docker build \
     --build-arg VITE_APP_URL=http://your-domain \
     --build-arg VITE_API_URL=http://your-api-domain \
     --build-arg VITE_TURNSTILE_KEY=your_key \
     -t vue-app .
   ```

2. Run the frontend container:
   ```bash
   docker run -d --restart=always --name vue-app -p 80:80 vue-app
   ```

## Environment Variables

### Backend

- `TURNSTILE_SECRET`: Secret key for Cloudflare Turnstile verification

### Frontend

- `VITE_APP_URL`: Frontend application URL
- `VITE_API_URL`: Backend API URL
- `VITE_TURNSTILE_KEY`: Cloudflare Turnstile site key

## CI/CD

The project uses GitHub Actions for continuous integration and deployment:

- Backend workflow (`.github/workflows/backend.yml`):

  - Runs unit and integration tests
  - Builds and deploys the backend on push to main

- Frontend workflow (`.github/workflows/frontend.yml`):
  - Runs ESLint
  - Builds and deploys the frontend on push to main

## Security Notes

- Never commit sensitive information like passwords or API keys
- Keep the JWT keystore secure and don't commit it to version control
- Use environment variables for sensitive configuration
