# Location Service - Independent Microservice

## Overview
Location Service handles location data management with Google Maps integration for distance calculations and geocoding.

## Service Details
- **Port**: 8004
- **Database**: PostgreSQL (ev_location_db)
- **Version**: 1.0.0
- **Integration**: Google Maps API

## Quick Start
```bash
cd location-service
# Update GOOGLE_MAPS_API_KEY in .env
docker-compose up -d
```

## API Endpoints
- POST /api/locations - Save location
- GET /api/locations/{entityId}/{entityType} - Get location by entity
- GET /api/locations/type/{entityType} - Get locations by type
- GET /api/locations/city/{city} - Get locations by city
- GET /api/locations/distance - Calculate distance between points
- DELETE /api/locations/{id} - Delete location

## Database Schema
- locations table with entity mapping, coordinates, address details
- Supports USER and PROVIDER entity types

## Features
- Location storage and retrieval
- Haversine distance calculation
- Google Maps integration (optional)
- Address components (city, state, zip)
- Entity type-based queries

## Configuration
Edit .env file:
- DB_HOST, DB_PORT, DB_NAME
- DB_USER, DB_PASSWORD
- GOOGLE_MAPS_API_KEY (get from Google Cloud Console)
- EUREKA_SERVER

## Testing
```bash
# Save location
curl -X POST http://localhost:8004/api/locations \
  -H "Content-Type: application/json" \
  -d '{
    "entityId": 1,
    "entityType": "PROVIDER",
    "latitude": 37.7749,
    "longitude": -122.4194,
    "address": "123 Main St",
    "city": "San Francisco",
    "state": "CA",
    "zipCode": "94102"
  }'

# Calculate distance
curl "http://localhost:8004/api/locations/distance?lat1=37.7749&lon1=-122.4194&lat2=37.8044&lon2=-122.2712"
```

## Deployment
```bash
docker build -t location-service:1.0.0 .
docker push location-service:1.0.0
docker-compose up -d
```

## Monitoring
- Health: http://localhost:8004/actuator/health
- Logs: logs/location-service.log

## Google Maps API Setup
1. Create project in Google Cloud Console
2. Enable Maps API
3. Create API key with restrictions
4. Add to .env as GOOGLE_MAPS_API_KEY
