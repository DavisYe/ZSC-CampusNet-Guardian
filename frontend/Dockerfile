# Build stage
FROM node:18-alpine AS builder
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies with cache optimization
RUN npm ci --registry=https://mirrors.huaweicloud.com/repository/npm/ \
    && npm cache clean --force

# Copy source code
COPY . .

# Set environment to production
ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

# Build application
RUN npm run build

# Production stage
FROM node:18-alpine
WORKDIR /app

# Copy necessary files
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static

# Set environment variables
ENV NODE_ENV=production
ENV PORT=3000

EXPOSE 3000

# Start the application
CMD ["node", "server.js"]