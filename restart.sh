#!/bin/bash

BACKEND_IMAGE="1dev3pl/project-flow-backend"
FRONTEND_IMAGE="1dev3pl/project-flow-frontend"

echo "Pulling latest backend image..."
docker pull $BACKEND_IMAGE
if [ $? -ne 0 ]; then
  echo "Failed to pull backend image."
  exit 1
fi

echo "Pulling latest frontend image..."
docker pull $FRONTEND_IMAGE
if [ $? -ne 0 ]; then
  echo "Failed to pull frontend image."
  exit 1
fi

echo "Stopping and removing existing containers..."
docker-compose down
if [ $? -ne 0 ]; then
  echo "Failed to stop and remove containers."
  exit 1
fi

echo "Rebuilding Docker Compose services..."
docker-compose build --no-cache
if [ $? -ne 0 ]; then
  echo "Failed to rebuild services."
  exit 1
fi

echo "Starting Docker Compose services..."
docker-compose up -d
if [ $? -ne 0 ]; then
  echo "Failed to start services."
  exit 1
fi

echo "Docker Compose services restarted successfully."
