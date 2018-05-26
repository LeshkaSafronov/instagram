DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id serial primary key,
  username varchar(100) NOT NULL,
  first_name varchar(50) NOT NULL,
  last_name varchar(50) NOT NULL,
  password varchar(50) NOT NULL
)