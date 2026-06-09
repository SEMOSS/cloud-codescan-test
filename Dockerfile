# ── Stage 1: Build the React application ──────────────────────────────────────
FROM node:20-alpine AS react-builder

WORKDIR /app

# Enable pnpm via corepack
RUN corepack enable && corepack prepare pnpm@latest --activate

# Copy package files first for better layer caching
COPY react-app/package.json ./
RUN pnpm install

# Copy source and build
COPY react-app/ ./
RUN pnpm run build

# ── Stage 2: Build the Java servlet WAR ───────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS java-builder

WORKDIR /app

# Copy Maven project
COPY java-backend/ ./

# Merge the React static output into the WAR's webapp root.
# WEB-INF/ is already present; React files land alongside it.
COPY --from=react-builder /app/build/ ./src/main/webapp/

# Package the WAR (skipping tests — no test suite present)
RUN mvn package -DskipTests -q

# ── Stage 3: Serve with Apache Tomcat ─────────────────────────────────────────
FROM tomcat:10.1-jdk17-temurin

# Remove the default Tomcat ROOT webapp
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Deploy the WAR as ROOT so everything is served at /
COPY --from=java-builder /app/target/hello-world.war /usr/local/tomcat/webapps/ROOT.war

# Copy Tomcat server config that sets the HTTP connector port to 8888
COPY tomcat/server.xml /usr/local/tomcat/conf/server.xml

EXPOSE 8888

CMD ["catalina.sh", "run"]
