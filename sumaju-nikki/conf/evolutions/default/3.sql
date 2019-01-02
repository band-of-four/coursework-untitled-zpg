-- vim: syntax=pgsql

# --- !Ups
create table characters (
  id bigserial primary key,
  name varchar
);

create table owls (
  id bigserial primary key,
  owl_type varchar
);

create table owls_characters (
  owl_id bigint not null references owls,
  character_id bigint not null references characters
);

create or replace function display_owls (username varchar)
returns table (
  owl_type varchar,
  amount bigint
)
as $$
begin
  return query
  select owls.owl_type, count(owls.owl_type) from owls, characters, owls_characters
  where owls.id = owls_characters.owl_id
  and characters.id = owls_characters.character_id
  and characters.name = username
  group by owls.owl_type;
end; $$
language 'plpgsql';

# --- !Downs
drop table characters;
drop table owls;
drop table owls_characters;
drop function display_owls;
