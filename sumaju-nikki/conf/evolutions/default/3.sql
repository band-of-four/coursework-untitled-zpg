-- vim: syntax=pgsql

# --- !Ups
create table spells (
  id bigserial primary key,
  name text,
  power integer,
  type text
);;

create table creatures (
  id bigserial primary key,
  name text,
  power integer,
  hp integer,
  level integer -- for gating
);;

create table student_clubs (
  id bigserial primary key,
  name text
);;

create table lessons (
  id bigserial primary key,
  name text,
  academic_year bigint,
  required_attendance integer
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
  name text,
  stage text,
  next_stage_time timestamp,
  attack_spell bigint not null references spells,
  defence_spell bigint not null references spells,
  luck_spell bigint not null references spells,
  pet_type bigint references creatures,
  pet_name text,
  book_permissions_amount bigint,
  academic_year bigint,
  current_room bigint not null references rooms
);;

create table lesson_attendance (
  lesson_id bigint not null references lessons,
  character_id bigint not null references characters,
  attendance bigint
);;

create table student_letters (
  sender_id bigint not null references characters,
  receiver_id bigint not null references characters,
  club_id bigint not null references student_clubs
);;

create table phrases (
  id bigserial primary key,
  creature_type_id bigint references creatures,
  student_club_id bigint references student_clubs,
  lesson_id bigint references lessons,
  phrase_type text, -- enter the battle, winning the battle etc
  phrase text
);;

create table diary (
  character_id bigint not null references characters,
  phrase_id bigint not null references phrases
);;

-- personal creature battle modifier character can learn from a books
create table creature_bonuses (
  character_id bigint not null references characters,
  creature_type_id bigint not null references creatures,
  modifier bigint
);;

create table owls_characters (
  character_id bigint not null references characters,
  owl_type text
);;

create table relationships (
  first_character bigint not null references characters,
  second_character bigint not null references characters,
  relationships bigint
);;

create or replace function display_owls (username text)
returns table (
  owl_type text,
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
drop function display_owls(username text);
drop table rooms cascade;
drop table diary cascade;
drop table relationships cascade;
drop table phrases cascade;
drop table student_letters cascade;
drop table student_clubs cascade;
drop table lesson_attendance cascade;
drop table lessons cascade;
drop table owls_characters cascade;
drop table creature_bonuses cascade;
drop table characters cascade;
drop table spells cascade;
drop table creatures cascade;
