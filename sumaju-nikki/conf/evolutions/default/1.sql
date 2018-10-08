-- vim: syntax=pgsql

# --- !Ups

create table users (
  id bigserial not null primary key,
  name text not null,
  password_hash text not null
);

# --- !Downs

drop table users;
