-- Update role constraint to include ADMIN role
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('PROVIDER', 'SUBSCRIBER', 'ADMIN'));

-- This script fixes the role constraint to allow ADMIN users to be created
COMMENT ON CONSTRAINT users_role_check ON users IS 'Allows PROVIDER, SUBSCRIBER, and ADMIN roles';
