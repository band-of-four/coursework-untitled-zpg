-- vim: syntax=pgsql

# --- !Ups

create table users (
  id bigserial primary key,
  name text not null,
  password_hash text not null,
  is_activated boolean default false
);

create table login_info (
  id bigserial primary key,
  provider_id text not null,
  provider_key text not null
);

create unique index login_info_provider_idx on
  login_info (provider_id, provider_key);

create table users_login_info (
  user_id bigint not null references users (id),
  login_info_id bigint not null references login_info (id)
);

create table login_password_info (
  user_id bigint not null references users (id),
  login_info_id bigint not null references login_info (id),
  password text not null,
  salt text
);

# --- !Downs

drop table login_password_info;
drop table users_login_info;
drop table login_info;
drop table users;
