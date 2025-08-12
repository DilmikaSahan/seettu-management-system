-- Quick Database Fix for Status Column
-- Run this in your PostgreSQL client BEFORE starting the backend

-- Step 1: Add status column with nullable first
ALTER TABLE seettu_groups ADD COLUMN IF NOT EXISTS status VARCHAR(20);

-- Step 2: Set default values for existing rows
UPDATE seettu_groups 
SET status = CASE 
    WHEN is_started = true AND is_active = true THEN 'ACTIVE'
    WHEN is_started = false THEN 'PENDING'
    ELSE 'PENDING'
END
WHERE status IS NULL;

-- Step 3: Now make it NOT NULL (after setting values)
ALTER TABLE seettu_groups ALTER COLUMN status SET NOT NULL;

-- Step 4: Set default for future inserts
ALTER TABLE seettu_groups ALTER COLUMN status SET DEFAULT 'PENDING';

-- Verify the fix
SELECT id, group_name, is_started, is_active, status FROM seettu_groups LIMIT 3;
