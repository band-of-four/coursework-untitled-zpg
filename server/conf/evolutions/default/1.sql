-- vim: syntax=pgsql

# --- !Ups

create table users (
  id bigserial primary key,
  email text
);

create table user_login_infos (
  id bigserial primary key,
  user_id bigint not null references users,
  provider_id text not null,
  provider_key text not null,

  unique (provider_id, provider_key)
);

create table user_password_infos (
  info_id bigint primary key references user_login_infos (id),
  hasher text not null,
  password text not null,
  salt text
);

create table user_oauth2_infos (
  info_id bigint primary key references user_login_infos (id),
  access_token text not null,
  token_type text,
  expires_in integer,
  refresh_token text
);

# --- !Downs

drop table user_oauth2_infos;
drop table user_password_infos;
drop table user_login_infos;
drop table users;
