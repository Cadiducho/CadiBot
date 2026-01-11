# syntax=docker/dockerfile:1
FROM eclipse-temurin:25-jre AS builder

WORKDIR /build

# Install unzip with cache mount for apt
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
    apt-get update && apt-get install -y unzip

# Copy and extract the distribution zip
COPY build/distributions/CadiBot.zip .
RUN unzip CadiBot.zip && mv CadiBot/* .

# Final runtime stage
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy extracted application from builder
COPY --from=builder /build/bin ./bin
COPY --from=builder /build/lib ./lib

RUN chmod +x bin/CadiBot

ENTRYPOINT ["bin/CadiBot"]