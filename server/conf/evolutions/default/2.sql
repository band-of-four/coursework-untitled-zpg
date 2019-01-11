-- vim: syntax=pgsql

-- Entities that implement the _travel_ game mechanic (`students` travelling between `rooms`)

# --- !Ups

create domain student_level as smallint
  constraint student_level_range check (value >= 0 and value <= 7);;

create type student_gender as enum
  ('female', 'male');;

create type room_kind as enum
  ('classroom', 'clubroom', 'library', 'infirmary');;

create table lessons (
  id bigserial primary key,
  name text not null,
  level student_level not null,
  required_attendance integer not null check (required_attendance > 0)
);;

create table student_clubs (
  id bigserial primary key,
  name text not null
);;

create table rooms (
  number bigserial primary key,
  level student_level not null,
  kind room_kind not null,
  club_id bigint references student_clubs,
  lesson_id bigint references lessons,

  constraint room_kind_integrity check (
    (kind = 'classroom'
      and club_id is null
      and lesson_id is not null)
    or (kind = 'clubroom'
      and club_id is not null
      and lesson_id is null)
    or (kind = 'library'
      and club_id is null
      and lesson_id is null)
    or (kind = 'infirmary'
      and club_id is null
      and lesson_id is null)
  )
);;

create table students (
  id bigserial primary key references users,
  name text not null,
  gender student_gender not null,
  level student_level not null,
  hp integer not null,
  current_room bigint not null references rooms,
  stage text not null,
  next_stage_time timestamp not null,
  stage_start_time timestamp not null
);;


# --- !Downs

drop table students;
drop table rooms;
drop table student_clubs;
drop table lessons;
drop type room_kind;
drop domain student_level;
