-- Create the user 'test_user' if it does not exist
DO $$
BEGIN
   -- Check if the role 'test_user' already exists
   IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'test_user') THEN
      -- Create the 'test_user' with an encrypted password
      CREATE USER test_user WITH ENCRYPTED PASSWORD 'test_password';
   END IF;
END $$;

-- Switch to the 'company_lookup' database
\c company_lookup;

-- Create the 'verifications' table if it does not already exist
CREATE TABLE IF NOT EXISTS verifications (
    verification_id UUID PRIMARY KEY,
    query_text VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    result TEXT,
    source VARCHAR(255) NOT NULL
);

-- Grant usage and create privileges on the 'public' schema to 'test_user'
GRANT USAGE, CREATE ON SCHEMA public TO test_user;

-- Grant SELECT, INSERT, UPDATE, and DELETE privileges on all tables in the 'public' schema to 'test_user'
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO test_user;

-- Change the owner of the 'verifications' table to 'test_user'
ALTER TABLE verifications OWNER TO test_user;
