-- File purpose: Contains supporting implementation for the Fleet Management application.
CREATE TABLE vehicles (
    vehicle_id VARCHAR(36) PRIMARY KEY,
    registration_number VARCHAR(100) UNIQUE NOT NULL,
    vehicle_type VARCHAR(100),
    manufacturer VARCHAR(150),
    model VARCHAR(150),
    vehicle_year INT,
    status VARCHAR(30),
    purchase_date DATE,
    ownership_details TEXT,
    current_odometer INT DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE drivers (
    driver_id VARCHAR(36) PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    license_number VARCHAR(100) UNIQUE NOT NULL,
    license_type VARCHAR(100),
    license_issue_date DATE,
    license_expiry_date DATE,
    contact_number VARCHAR(50),
    email VARCHAR(150) UNIQUE NOT NULL,
    address TEXT,
    availability_status VARCHAR(30),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE assignments (
    assignment_id VARCHAR(36) PRIMARY KEY,
    vehicle_id VARCHAR(36),
    driver_id VARCHAR(36),
    assignment_date DATE,
    start_date DATE,
    end_date DATE,
    status VARCHAR(30),
    notes TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_assignments_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id),
    CONSTRAINT fk_assignments_driver FOREIGN KEY (driver_id) REFERENCES drivers(driver_id)
);

CREATE TABLE maintenance_records (
    maintenance_id VARCHAR(36) PRIMARY KEY,
    vehicle_id VARCHAR(36),
    service_date DATE,
    service_type VARCHAR(30),
    description TEXT,
    service_provider VARCHAR(255),
    cost DECIMAL(12,2),
    next_service_due DATE,
    odometer_reading INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_maintenance_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id)
);

CREATE TABLE expenses (
    expense_id VARCHAR(36) PRIMARY KEY,
    vehicle_id VARCHAR(36),
    expense_type VARCHAR(30),
    amount DECIMAL(12,2),
    expense_date DATE,
    description TEXT,
    receipt_number VARCHAR(100),
    vendor VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_expenses_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id)
);

CREATE TABLE trips (
    trip_id VARCHAR(36) PRIMARY KEY,
    vehicle_id VARCHAR(36),
    driver_id VARCHAR(36),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    purpose VARCHAR(255),
    start_location VARCHAR(255),
    end_location VARCHAR(255),
    start_odometer INT,
    end_odometer INT,
    distance_covered INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_trips_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id),
    CONSTRAINT fk_trips_driver FOREIGN KEY (driver_id) REFERENCES drivers(driver_id)
);

CREATE INDEX idx_vehicles_status ON vehicles(status);
CREATE INDEX idx_vehicles_vehicle_type ON vehicles(vehicle_type);
CREATE INDEX idx_drivers_availability_status ON drivers(availability_status);
CREATE INDEX idx_assignments_vehicle_id ON assignments(vehicle_id);
CREATE INDEX idx_assignments_driver_id ON assignments(driver_id);
CREATE INDEX idx_assignments_status ON assignments(status);
CREATE INDEX idx_expenses_vehicle_id ON expenses(vehicle_id);
CREATE INDEX idx_trips_vehicle_id ON trips(vehicle_id);
CREATE INDEX idx_trips_driver_id ON trips(driver_id);

