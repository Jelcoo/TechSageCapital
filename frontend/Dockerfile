# Build stage
FROM node:22-alpine AS builder
WORKDIR /app

ARG VITE_APP_URL
ARG VITE_API_URL
ARG VITE_TURNSTILE_KEY

ENV VITE_APP_URL=$VITE_APP_URL
ENV VITE_API_URL=$VITE_API_URL
ENV VITE_TURNSTILE_KEY=$VITE_TURNSTILE_KEY

RUN npm install -g pnpm@latest-10
COPY package.json pnpm-lock.yaml ./
RUN pnpm install

COPY . .
RUN pnpm build

# Production stage
FROM nginx:alpine AS production

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
