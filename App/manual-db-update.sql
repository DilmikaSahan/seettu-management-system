-- Manual Database Update Script for Group Status Management
-- Run these commands in your PostgreSQL client (pgAdmin, DBeaver, etc.)
-- or copy-paste them one by one in psql

-- Step 1: Connect to the database
\c seettu_db;

-- Step 2: Check current table structure
\d seettu_groups;

-- Step 3: Add status column if it doesn't exist
ALTER TABLE seettu_groups ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'PENDING';

-- Step 4: Update existing groups based on their current state
UPDATE seettu_groups 
SET status = CASE 
    WHEN is_started = true AND is_active = true THEN 'ACTIVE'
    WHEN is_started = false THEN 'PENDING'
    ELSE 'PENDING'
END
WHERE status IS NULL OR status = 'PENDING';

-- Step 5: Make status column NOT NULL
ALTER TABLE seettu_groups ALTER COLUMN status SET NOT NULL;

-- Step 6: Create index for better performance
CREATE INDEX IF NOT EXISTS idx_seettu_groups_status ON seettu_groups(status);

-- Step 7: Verify the changes
SELECT 
    table_name,
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_name = 'seettu_groups' 
AND column_name = 'status';

-- Step 8: Check a sample of updated data
SELECT id, group_name, is_started, is_active, status 
FROM seettu_groups 
LIMIT 5;

-- SUCCESS: Status column has been added and existing data migrated!
