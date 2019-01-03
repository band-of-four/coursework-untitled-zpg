-- vim: syntax=pgsql

# --- !Ups
create table spells (
  id bigserial primary key,
  name varchar,
  power int,
  type varchar
);;

create table characters (
  id bigserial primary key,
  name varchar,
  current_stage varchar,
  next_stage_time timestamp,
  attack_spell bigint not null references spells,
  defence_spell bigint not null references spells,
  luck_spell bigint not null references spells
  pet_type bigint references creature_type,
  pet_name varchar
);;

create table creature_type (
  id bigserial primary key,
  name varchar,
  power int,
  hp int,
  level int -- for gating
);;

create table creature_phrases (
  creature_type_id bigint not null references creature_type,
  phrase_type varchar, -- enter the battle, winning the battle etc
  phrase varchar
);;

-- personal creature battle modifier character can learn from a books
create table creature_bonuses (
  character_id bigint not null references characters,
  creature_type_id bigint not null references creature_type,
  modifier bigint
);;

create table owls_characters (
  character_id bigint not null references characters,
  owl_type varchar
);;

create or replace function display_owls (username varchar)
returns table (
  owl_type varchar,
  amount bigint
)
as $body$
begin
  return query
  select owls_characters.owl_type, count(owls_characters.owl_type) from characters, owls_characters
  where characters.id = owls_characters.character_id
  and characters.name = username
  group by owls_characters.owl_type;;
end;; $body$
language 'plpgsql';;

# --- !Downs
drop function display_owls(username varchar);;
drop table owls_characters;;
drop table creature_bonuses;;
drop table characters;;
drop table spells;;
drop table creature_phrases;;
drop table creature_type;;
