# Build stage
FROM node:22-alpine AS builder
WORKDIR /app
RUN npm install -g pnpm@latest-10
COPY package.json pnpm-lock.yaml ./
RUN pnpm install
COPY . .
RUN pnpm build

# Development stage
FROM node:22-alpine AS development
WORKDIR /app
RUN npm install -g pnpm@latest-10
COPY package.json pnpm-lock.yaml ./
RUN pnpm install
COPY . .
EXPOSE 5173
CMD ["pnpm", "dev"]

# Production stage
FROM nginx:alpine AS production
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 4173
CMD ["nginx", "-g", "daemon off;"]
