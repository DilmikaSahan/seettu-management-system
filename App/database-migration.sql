-- Database Migration Script for Group Status Management
-- Run this script if Hibernate didn't automatically add the status column

-- Add status column to seettu_groups table if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'seettu_groups' 
        AND column_name = 'status'
    ) THEN
        -- Add the status column with default value 'PENDING'
        ALTER TABLE seettu_groups 
        ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING';
        
        -- Update existing groups based on their current state
        UPDATE seettu_groups 
        SET status = CASE 
            WHEN is_started = true AND is_active = true THEN 'ACTIVE'
            WHEN is_started = false THEN 'PENDING'
            ELSE 'PENDING'
        END;
        
        -- Add NOT NULL constraint after setting default values
        ALTER TABLE seettu_groups 
        ALTER COLUMN status SET NOT NULL;
        
        RAISE NOTICE 'Status column added to seettu_groups table and existing data migrated';
    ELSE
        RAISE NOTICE 'Status column already exists in seettu_groups table';
    END IF;
END $$;

-- Create index on status column for better query performance
CREATE INDEX IF NOT EXISTS idx_seettu_groups_status ON seettu_groups(status);

-- Verify the migration
SELECT 
    table_name,
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_name = 'seettu_groups' 
AND column_name = 'status';
