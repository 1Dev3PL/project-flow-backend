#!/bin/bash

echo "Building backend image..."
docker build -f Dockerfile -t 1dev3pl/project-flow-backend .
if [ $? -eq 0 ]; then
  echo "Pushing backend image..."
  docker push 1dev3pl/project-flow-backend
else
  echo "Backend build failed."
  exit 1
fi

echo "Backend image built and pushed successfully."
