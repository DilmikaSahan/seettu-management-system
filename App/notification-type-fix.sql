-- Fix Notification Type Constraint for Group Status Management
-- Run this in your PostgreSQL client to fix the notification type constraint

-- Step 1: Check current constraint
SELECT constraint_name, check_clause 
FROM information_schema.check_constraints 
WHERE constraint_name LIKE '%type%' 
AND table_name = 'notifications';

-- Step 2: Drop the old constraint
ALTER TABLE notifications DROP CONSTRAINT IF EXISTS notifications_type_check;

-- Step 3: Add new constraint with all notification types
ALTER TABLE notifications 
ADD CONSTRAINT notifications_type_check 
CHECK (type IN (
    'PAYMENT_RECEIVED', 
    'PACKAGE_RECEIVED', 
    'GROUP_STARTED', 
    'GROUP_ENDED', 
    'MEMBER_ADDED', 
    'GENERAL',
    'GROUP_COMPLETED',
    'GROUP_CANCELLED'
));

-- Step 4: Verify the fix
SELECT constraint_name, check_clause 
FROM information_schema.check_constraints 
WHERE constraint_name = 'notifications_type_check';
