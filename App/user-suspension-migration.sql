-- User Suspension System Migration
-- This script adds suspension functionality to the users table

-- Add suspension columns to users table
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS is_suspended BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS suspended_date TIMESTAMP,
ADD COLUMN IF NOT EXISTS suspension_reason TEXT;

-- Create index for faster queries on suspended users
CREATE INDEX IF NOT EXISTS idx_users_suspended ON users(is_suspended);

-- Update any existing users to ensure they are not suspended by default
UPDATE users SET is_suspended = FALSE WHERE is_suspended IS NULL;

-- Optional: Add a comment to document the new columns
COMMENT ON COLUMN users.is_suspended IS 'Flag to indicate if the user account is suspended';
COMMENT ON COLUMN users.suspended_date IS 'Timestamp when the user was suspended';
COMMENT ON COLUMN users.suspension_reason IS 'Reason for suspension provided by admin';
