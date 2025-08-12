-- PostgreSQL Database Setup Script for Seettu Management System
-- Run this script in your PostgreSQL database as the seettu_user

-- Create the database (run this as a superuser first)
-- CREATE DATABASE seettu_db;
-- CREATE USER seettu_user WITH PASSWORD 'Sseettu1125@#';
-- GRANT ALL PRIVILEGES ON DATABASE seettu_db TO seettu_user;

-- Connect to seettu_db database and run the following:

-- Create ENUM for roles (if not using JPA auto-generation)
-- CREATE TYPE user_role AS ENUM ('PROVIDER', 'SUBSCRIBER');

-- Create ENUM for notification types (if not using JPA auto-generation)
-- CREATE TYPE notification_type AS ENUM ('PAYMENT_RECEIVED', 'PACKAGE_RECEIVED', 'GROUP_STARTED', 'GROUP_ENDED', 'MEMBER_ADDED', 'GENERAL');

-- Note: With spring.jpa.hibernate.ddl-auto=update, Hibernate will automatically create tables
-- The following is just for reference of what tables will be created:

/*
Tables that will be auto-created by Hibernate:

1. users
   - id (BIGSERIAL PRIMARY KEY)
   - name (VARCHAR)
   - email (VARCHAR UNIQUE NOT NULL)
   - password (VARCHAR)
   - phone_number (VARCHAR UNIQUE)
   - user_id (VARCHAR UNIQUE)
   - role (VARCHAR) -- PROVIDER or SUBSCRIBER

2. seettu_packages
   - id (BIGSERIAL PRIMARY KEY)
   - package_name (VARCHAR NOT NULL)
   - description (VARCHAR NOT NULL)
   - package_value (DECIMAL NOT NULL)
   - provider_id (BIGINT REFERENCES users(id))
   - is_active (BOOLEAN DEFAULT TRUE)

3. seettu_groups
   - id (BIGSERIAL PRIMARY KEY)
   - group_name (VARCHAR NOT NULL)
   - provider_id (BIGINT REFERENCES users(id))
   - package_id (BIGINT REFERENCES seettu_packages(id))
   - monthly_amount (DECIMAL NOT NULL)
   - number_of_months (INTEGER NOT NULL)
   - start_date (DATE NOT NULL)
   - is_active (BOOLEAN DEFAULT FALSE)
   - is_started (BOOLEAN DEFAULT FALSE)

4. members
   - id (BIGSERIAL PRIMARY KEY)
   - group_id (BIGINT REFERENCES seettu_groups(id))
   - user_id (BIGINT REFERENCES users(id))
   - order_number (INTEGER NOT NULL)
   - join_date (DATE NOT NULL)
   - is_active (BOOLEAN DEFAULT TRUE)

5. payments
   - id (BIGSERIAL PRIMARY KEY)
   - amount (DECIMAL NOT NULL)
   - payment_date (DATE NOT NULL)
   - month_number (INTEGER NOT NULL)
   - is_paid (BOOLEAN DEFAULT FALSE)
   - paid_at (TIMESTAMP)
   - member_id (BIGINT REFERENCES members(id))
   - group_id (BIGINT REFERENCES seettu_groups(id))
   - paid_by_provider_id (BIGINT REFERENCES users(id))

6. notifications
   - id (BIGSERIAL PRIMARY KEY)
   - user_id (BIGINT REFERENCES users(id))
   - title (VARCHAR NOT NULL)
   - message (VARCHAR(1000) NOT NULL)
   - type (VARCHAR NOT NULL)
   - is_read (BOOLEAN DEFAULT FALSE)
   - created_at (TIMESTAMP NOT NULL)
   - group_id (BIGINT REFERENCES seettu_groups(id))
   - payment_id (BIGINT REFERENCES payments(id))
*/

-- Grant necessary permissions to seettu_user (run as superuser)
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO seettu_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO seettu_user;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO seettu_user;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO seettu_user;

-- Insert sample data (optional - for testing)
-- This will be handled by the application, but here's some sample data:

/*
-- Sample Provider User
INSERT INTO users (name, email, password, phone_number, user_id, role) 
VALUES ('John Provider', 'provider@example.com', '$2a$10$encrypted_password_here', '+1234567890', 'PROV001', 'PROVIDER');

-- Sample Subscriber Users
INSERT INTO users (name, email, password, phone_number, user_id, role) 
VALUES 
('Alice Subscriber', 'alice@example.com', '$2a$10$encrypted_password_here', '+1234567891', 'SUB001', 'SUBSCRIBER'),
('Bob Subscriber', 'bob@example.com', '$2a$10$encrypted_password_here', '+1234567892', 'SUB002', 'SUBSCRIBER'),
('Carol Subscriber', 'carol@example.com', '$2a$10$encrypted_password_here', '+1234567893', 'SUB003', 'SUBSCRIBER');
*/