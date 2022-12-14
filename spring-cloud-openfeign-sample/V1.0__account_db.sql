CREATE SCHEMA IF NOT EXISTS S_ACCOUNT;

CREATE TABLE T_ACCOUNT (
  ID UUID PRIMARY KEY,
  OWNER VARCHAR,
  BALANCE DECIMAL,
  CREATION_DATE TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  VERSION BIGINT
);
