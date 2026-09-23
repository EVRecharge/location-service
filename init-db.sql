CREATE TABLE IF NOT EXISTS locations (
    location_id SERIAL PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    address VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(50),
    zip_code VARCHAR(20),
    name VARCHAR(200)
);

CREATE INDEX IF NOT EXISTS idx_locations_entity ON locations(entity_id, entity_type);
CREATE INDEX IF NOT EXISTS idx_locations_type ON locations(entity_type);
CREATE INDEX IF NOT EXISTS idx_locations_city ON locations(city);
