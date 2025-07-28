# --- Build Stage ---
# Use the official Golang image as a builder.
FROM golang:1.19-alpine AS builder

# Set the working directory inside the container.
WORKDIR /app

# Set environment variables for the build.
# Using a public proxy for Go modules to speed up downloads.
ENV GOPROXY=https://goproxy.cn,direct

# Copy the rest of the application source code first.
COPY . .

# Add missing dependencies and update go.sum
RUN go get github.com/gin-contrib/cors@v1.4.0 && go mod tidy && go mod download

# Build the application as a static binary.
# CGO_ENABLED=0 is important for creating a static binary without C dependencies.
RUN CGO_ENABLED=0 go build -a -installsuffix cgo -o app . 

# --- Final Stage ---
# Use a minimal, empty base image for the final container.
FROM scratch

# Set the working directory.
WORKDIR /root/

# Copy the compiled application binary from the builder stage.
COPY --from=builder /app/app .

# Copy the configuration file.
COPY --from=builder /app/config.yaml .

# Expose the port the application will run on.
EXPOSE 8081

# Set the command to run the application.
CMD ["./app"]