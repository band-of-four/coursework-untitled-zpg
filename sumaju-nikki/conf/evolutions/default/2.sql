-- vim: syntax=pgsql

# --- !Ups

create table user_oauth2_info (
  info_id bigint not null references user_login_info (id),
  access_token text not null,
  token_type text,
  expires_in integer,
  refresh_token text
);

# --- !Downs

drop table user_oauth2_info;
