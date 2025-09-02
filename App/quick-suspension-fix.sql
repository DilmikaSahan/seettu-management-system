-- Quick fix to set default values for existing users
-- This will make all existing users active (not suspended)

-- First, let's check if the column exists and update null values
UPDATE users SET is_suspended = false WHERE is_suspended IS NULL;

-- Set default value for the column
ALTER TABLE users ALTER COLUMN is_suspended SET DEFAULT false;

-- Optional: Make the column NOT NULL after setting defaults
-- ALTER TABLE users ALTER COLUMN is_suspended SET NOT NULL;
