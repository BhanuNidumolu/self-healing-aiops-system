#!/bin/bash
set -e

# ============================================================================
# SENTINEL PHASE 1 — EC2 DEPLOY SCRIPT
# Run this from your project root after building JARs
# ============================================================================

EC2_USER="ubuntu"
EC2_HOST="$1"  # Pass EC2 public IP as first argument
KEY_PATH="$2"  # Pass path to .pem key as second argument

if [ -z "$EC2_HOST" ] || [ -z "$KEY_PATH" ]; then
    echo "Usage: ./deploy.sh <EC2_PUBLIC_IP> <PATH_TO_KEY.pem>"
    exit 1
fi

echo ">>> Building JARs locally..."
# Uncomment the build tool you use:
# ./mvnw clean package -DskipTests
# OR
# ./gradlew bootJar

echo ">>> Ensuring nginx config exists..."
mkdir -p nginx
cp -n nginx/nginx.ec2.conf nginx/nginx.conf 2>/dev/null || true

echo ">>> Copying files to EC2..."
scp -i "$KEY_PATH" -o StrictHostKeyChecking=no \
    docker-compose.ec2.yml \
    Dockerfile.simple \
    Dockerfile.healing \
    "$KEY_PATH" \
    "${EC2_USER}@${EC2_HOST}:/opt/sentinel/"

# Copy nginx config
scp -i "$KEY_PATH" -o StrictHostKeyChecking=no -r nginx/ "${EC2_USER}@${EC2_HOST}:/opt/sentinel/"

# Copy all service JARs (assumes standard Maven structure)
for svc in monitored-service metrics-agent-service logs-agent-service anomaly-agent-service supervisor-service healing-agent-service; do
    if [ -d "$svc/target" ]; then
        echo ">>> Copying $svc JAR..."
        scp -i "$KEY_PATH" -o StrictHostKeyChecking=no             "$svc/target/"*.jar             "${EC2_USER}@${EC2_HOST}:/opt/sentinel/$svc/target/"
    fi
done

echo ">>> Starting services on EC2..."
ssh -i "$KEY_PATH" -o StrictHostKeyChecking=no "${EC2_USER}@${EC2_HOST}" << 'REMOTE'
    cd /opt/sentinel
    sudo docker compose -f docker-compose.ec2.yml down 2>/dev/null || true
    sudo docker compose -f docker-compose.ec2.yml up --build -d
    sleep 5
    sudo docker compose -f docker-compose.ec2.yml ps
REMOTE

echo ">>> Deploy complete!"
echo "    Dashboard (S3):  Check terraform output for bucket URL"
echo "    API Endpoint:      http://${EC2_HOST}/api/process"
echo "    SSH Debug:         ssh -i ${KEY_PATH} ${EC2_USER}@${EC2_HOST}"