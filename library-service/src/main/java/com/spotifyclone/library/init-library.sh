#!/usr/bin/env bash

set -e

CONTAINER_NAME="spotify-library-cassandra"
KEYSPACE_NAME="spotify_library"

echo "Checking Cassandra container: $CONTAINER_NAME"

if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
  echo "Container is not running: $CONTAINER_NAME"
  echo "Start it first:"
  echo "docker compose up -d library-cassandra"
  exit 1
fi

echo "Waiting for Cassandra to be ready..."

until docker exec "$CONTAINER_NAME" cqlsh -e "DESCRIBE KEYSPACES;" > /dev/null 2>&1; do
  echo "Cassandra is not ready yet. Waiting..."
  sleep 5
done

echo "Cassandra is ready."

echo "Creating keyspace: $KEYSPACE_NAME"

docker exec -i "$CONTAINER_NAME" cqlsh <<EOF
CREATE KEYSPACE IF NOT EXISTS $KEYSPACE_NAME
WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
};

USE $KEYSPACE_NAME;

CREATE TABLE IF NOT EXISTS liked_songs (
  user_id uuid,
  song_id uuid,
  liked_at timestamp,
  PRIMARY KEY ((user_id), song_id)
);

DESCRIBE KEYSPACES;
DESCRIBE TABLES;
EOF

echo "Cassandra initialization completed."