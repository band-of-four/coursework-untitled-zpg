-- vim: syntax=pgsql

# --- !Ups
create table spells (
  id bigserial primary key,
  name varchar,
  power int,
  type varchar
);;

create table creature_type (
  id bigserial primary key,
  name varchar,
  power int,
  hp int,
  level int -- for gating
);;

create table student_clubs (
  id bigserial primary key,
  name varchar
);;

create table lessons (
  id bigserial primary key,
  name varchar,
  academic_year bigint
);;

-- room number == id, in the room can be a club or a lesson,
-- if both are null - then its a library
create table rooms (
  number bigserial primary key,
  club_id bigint references student_clubs,
  lesson_id bigint references lessons
);;

create table characters (
  id bigserial primary key,
  name varchar,
  stage varchar,
  next_stage_time timestamp,
  attack_spell bigint not null references spells,
  defence_spell bigint not null references spells,
  luck_spell bigint not null references spells,
  pet_type bigint references creature_type,
  pet_name varchar,
  book_permissions_amount bigint,
  academic_year bigint,
  current_room bigint not null references rooms
);;

create table lessons_attendance (
  lesson_id bigint not null references lessons,
  character_id bigint not null references characters,
  attendance bigint
);;

create table student_messages (
  sender_id bigint not null references characters,
  receiver_id bigint not null references characters,
  club_id bigint not null references student_clubs
);;

create table phrases (
  creature_type_id bigint references creature_type,
  student_club_id bigint references student_clubs,
  lesson_id bigint references lessons,
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

create table relationships (
  first_character bigint not null references characters,
  second_character bigint not null references characters,
  relationships bigint
);;

create or replace function display_owls (username varchar)
returns table (
  owl_type varchar,
  amount bigint
)
as $$
begin
  return query
  select owls_characters.owl_type, count(owls_characters.owl_type) from characters, owls_characters
  where characters.id = owls_characters.character_id
  and characters.name = username
  group by owls_characters.owl_type;;
end;; $$
language 'plpgsql';;

# --- !Downs
drop function display_owls(username varchar);
drop table rooms;
drop table relationships;
drop table phrases;
drop table student_messages;
drop table student_clubs;
drop table lessons_attendance;
drop table lessons;
drop table owls_characters;
drop table creature_bonuses;
drop table characters;
drop table spells;
drop table creature_type;
