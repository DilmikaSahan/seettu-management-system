# PostgreSQL Database Setup for Seettu Management System

## Prerequisites
- PostgreSQL installed and running
- Access to PostgreSQL with superuser privileges (for initial setup)

## Database Setup Steps

### 1. Connect to PostgreSQL as superuser
```bash
psql -U postgres
```

### 2. Create Database and User
```sql
-- Create the database
CREATE DATABASE seettu_db;

-- Create the user (if not already exists)
CREATE USER seettu_user WITH PASSWORD 'Sseettu1125@#';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE seettu_db TO seettu_user;

-- Connect to the new database
\c seettu_db;

-- Grant schema privileges
GRANT ALL PRIVILEGES ON SCHEMA public TO seettu_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO seettu_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO seettu_user;

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO seettu_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO seettu_user;
```

### 3. Verify Connection
Test the connection with the new user:
```bash
psql -U seettu_user -d seettu_db -h localhost
```

### 4. Application Configuration
The application is already configured to use:
- **Database**: `seettu_db`
- **Username**: `seettu_user`
- **Password**: `Sseettu1125@#`
- **Host**: `localhost`
- **Port**: `5432` (default)

## Table Creation
Tables will be automatically created by Hibernate when you start the Spring Boot application due to the configuration:
```properties
spring.jpa.hibernate.ddl-auto=update
```

## Expected Tables
The following tables will be created automatically:

1. **users** - Store provider and subscriber information
2. **seettu_packages** - Store package definitions
3. **seettu_groups** - Store group information
4. **members** - Store group membership details
5. **payments** - Store payment tracking information
6. **notifications** - Store user notifications

## Starting the Application

### Backend
```bash
cd seettu-backend/backend
mvn spring-boot:run
```

### Frontend
```bash
cd seettu-frontend
npm install
ng serve
```

## Troubleshooting

### Connection Issues
If you encounter connection issues:

1. **Check PostgreSQL is running**:
   ```bash
   sudo systemctl status postgresql
   # or on Windows
   net start postgresql-x64-13
   ```

2. **Check pg_hba.conf** for authentication settings:
   - Location: Usually in `/etc/postgresql/[version]/main/pg_hba.conf`
   - Ensure local connections are allowed for the user

3. **Check postgresql.conf** for connection settings:
   - Ensure `listen_addresses` includes localhost
   - Check `port` setting (default 5432)

### Permission Issues
If you get permission errors:
```sql
-- Connect as superuser and run:
GRANT ALL PRIVILEGES ON DATABASE seettu_db TO seettu_user;
GRANT ALL PRIVILEGES ON SCHEMA public TO seettu_user;
```

### Password Authentication
If password authentication fails:
1. Check pg_hba.conf has `md5` or `password` method for local connections
2. Restart PostgreSQL after changes:
   ```bash
   sudo systemctl restart postgresql
   ```

## Sample Data
Once the application is running, you can:
1. Register as a PROVIDER through the web interface
2. Create packages and groups
3. Add subscribers
4. Test the complete workflow

## Database Backup
To backup your database:
```bash
pg_dump -U seettu_user -d seettu_db > seettu_backup.sql
```

To restore:
```bash
psql -U seettu_user -d seettu_db < seettu_backup.sql
```