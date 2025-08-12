# PostgreSQL Setup Steps - Fix Authentication Error

## Step 1: Connect to PostgreSQL as superuser

Open Command Prompt or PowerShell as Administrator and run:

```bash
# Connect to PostgreSQL (replace 'postgres' with your superuser if different)
psql -U postgres
```

If this doesn't work, try:
```bash
# If PostgreSQL is not in PATH, use full path
"C:\Program Files\PostgreSQL\[version]\bin\psql.exe" -U postgres
```

## Step 2: Create Database and User

Once connected to PostgreSQL, run these commands:

```sql
-- Create the database
CREATE DATABASE seettu_db;

-- Create the user with the exact password
CREATE USER seettu_user WITH PASSWORD 'Sseettu1125@#';

-- Grant all privileges on the database
GRANT ALL PRIVILEGES ON DATABASE seettu_db TO seettu_user;

-- Connect to the new database
\c seettu_db;

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO seettu_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO seettu_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO seettu_user;

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO seettu_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO seettu_user;

-- Exit PostgreSQL
\q
```

## Step 3: Test the Connection

Test if the user can connect:

```bash
psql -U seettu_user -d seettu_db -h localhost
```

Enter password: `Sseettu1125@#`

If successful, you should see the PostgreSQL prompt. Type `\q` to exit.

## Step 4: Check PostgreSQL Configuration (if connection still fails)

### Check pg_hba.conf file:

1. Find the pg_hba.conf file (usually in PostgreSQL data directory)
2. Open it in a text editor as Administrator
3. Ensure there's a line like this for local connections:

```
# TYPE  DATABASE        USER            ADDRESS                 METHOD
local   all             all                                     md5
host    all             all             127.0.0.1/32            md5
host    all             all             ::1/128                 md5
```

4. If you made changes, restart PostgreSQL service:

**Windows:**
```bash
net stop postgresql-x64-[version]
net start postgresql-x64-[version]
```

## Step 5: Alternative - Reset User Password

If the user exists but password is wrong:

```sql
-- Connect as superuser
psql -U postgres

-- Reset the password
ALTER USER seettu_user WITH PASSWORD 'Sseettu1125@#';

-- Exit
\q
```

## Step 6: Verify Database Setup

Once connected as seettu_user:

```sql
-- Check current database
SELECT current_database();

-- Check current user
SELECT current_user;

-- List tables (should be empty initially)
\dt

-- Exit
\q
```

## Step 7: Start Spring Boot Application

After successful database setup, try running the Spring Boot application again:

```bash
cd seettu-backend/backend
mvn spring-boot:run
```

## Troubleshooting

### If you get "psql: command not found":
Add PostgreSQL bin directory to your PATH or use full path:
```bash
"C:\Program Files\PostgreSQL\[version]\bin\psql.exe"
```

### If you get "peer authentication failed":
Edit pg_hba.conf and change `peer` to `md5` for local connections.

### If you get "database does not exist":
Make sure you created the database with the exact name `seettu_db`.

### If you get "role does not exist":
Make sure you created the user with the exact name `seettu_user`.

## Quick Fix Script

Here's a complete script you can run in PostgreSQL as superuser:

```sql
-- Drop existing database and user if they exist (optional)
DROP DATABASE IF EXISTS seettu_db;
DROP USER IF EXISTS seettu_user;

-- Create fresh database and user
CREATE DATABASE seettu_db;
CREATE USER seettu_user WITH PASSWORD 'Sseettu1125@#';
GRANT ALL PRIVILEGES ON DATABASE seettu_db TO seettu_user;

-- Connect to the database and set permissions
\c seettu_db;
GRANT ALL ON SCHEMA public TO seettu_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO seettu_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO seettu_user;

-- Verify setup
SELECT 'Database setup completed successfully!' as status;
```