-- =============================================
-- ADMIN ROLE DATABASE MIGRATION SCRIPT
-- =============================================
-- This script fixes the database constraint to allow ADMIN role

-- Step 1: Drop existing role constraint
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

-- Step 2: Add new constraint that includes ADMIN role
ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('PROVIDER', 'SUBSCRIBER', 'ADMIN'));

-- Step 3: Verify the constraint is working
SELECT constraint_name, check_clause 
FROM information_schema.check_constraints 
WHERE constraint_name = 'users_role_check';

-- Display success message
SELECT 'Database constraint updated successfully - ADMIN role is now allowed' AS message;
