DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS filter;
DROP TABLE IF EXISTS photo;
DROP TABLE IF EXISTS users;


CREATE TABLE users (
  id serial primary key,
  username varchar(100) NOT NULL,
  password varchar(50) NOT NULL
);

CREATE TABLE photos (
  id serial primary key,
  user_id integer REFERENCES users (id),
  created_at TIMESTAMP DEFAULT NOW(),
  likes integer DEFAULT 0,
  text varchar(1024),
  is_ready boolean DEFAULT false,
  key varchar(128)
);

CREATE TABLE filters (
  id serial primary key,
  user_id integer REFERENCES users (id),
  key varchar(128)
);

CREATE TABLE comments (
  id serial primary key,
  user_id integer REFERENCES users (id),
  photo_id integer REFERENCES photos (id),
  created_at TIMESTAMP DEFAULT NOW(),
  text varchar(1024)
);
