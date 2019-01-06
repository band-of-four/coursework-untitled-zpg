-- vim: syntax=pgsql

# --- !Ups
create table spells (
  id bigserial primary key,
  kind text not null,
  name text not null,
  power integer not null,
  academic_year integer not null
);;

create table creatures (
  id bigserial primary key,
  name text,
  power integer,
  total_hp integer,
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
  level integer not null,
  club_id bigint references student_clubs,
  lesson_id bigint references lessons
);;

create table students (
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

create table creature_fights (
  creature_id bigint references creatures,
  student_id bigint references students,
  creature_hp integer,

  primary key (creature_id, student_id)
);;

create table creature_handling_skills (
  creature_id bigint references creatures,
  student_id bigint references students,
  modifier integer,

  primary key (creature_id, student_id)
);;

create table spells_students (
  spell_id bigint references spells,
  student_id bigint references students,

  primary key (spell_id, student_id)
);;

create table lesson_attendances (
  lesson_id bigint not null references lessons,
  student_id bigint not null references students,
  classes_attended integer
);;

create table student_letters (
  sender_id bigint not null references students,
  receiver_id bigint not null references students,
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
  student_id bigint not null references students,
  phrase_id bigint not null references phrases
);;

create table owls_students (
  student_id bigint not null references students,
  owl_type text
);;

create table relationships (
  student_a bigint not null references students,
  student_b bigint not null references students,
  relationship integer
);;

create or replace function display_owls (username text)
returns table (
  owl_type text,
  amount bigint
)
as $$
begin
  return query
  select owls_students.owl_type, count(owls_students.owl_type) from students, owls_students
  where students.id = owls_students.student_id
  and students.name = username
  group by owls_students.owl_type;;
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
drop table lesson_attendances cascade;
drop table lessons cascade;
drop table owls_students cascade;
drop table creature_fights cascade;
drop table creature_handling_skills cascade;
drop table spells_students cascade;
drop table students cascade;
drop table spells cascade;
drop table creatures cascade;
