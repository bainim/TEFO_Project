#!/bin/bash

# Rebuild Backend
rm -rf target/* && mvn package


# Start The Frontend
docker-compose down
docker-compose up  --build -d backend_empreinte_carbone_container &
