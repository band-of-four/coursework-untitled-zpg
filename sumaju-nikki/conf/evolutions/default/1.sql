-- vim: syntax=pgsql

# --- !Ups

create table users (
  id bigserial primary key,
  email text
);

create table user_login_info (
  id bigserial primary key,
  user_id bigint not null references users,
  provider_id text not null,
  provider_key text not null
);

create unique index user_login_info_provider_idx on
  user_login_info (provider_id, provider_key);

create table user_password_info (
  info_id bigint not null references user_login_info (id),
  hasher text not null,
  password text not null,
  salt text
);

# --- !Downs

drop table user_password_info;
drop table user_login_info;
drop table users;
