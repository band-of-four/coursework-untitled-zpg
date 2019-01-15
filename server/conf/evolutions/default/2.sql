-- vim: syntax=pgsql

# --- !Ups

create domain student_level as smallint
  constraint student_level_range check (value >= 0 and value <= 3);;

create type room_kind as enum
  ('Classroom', 'Clubroom', 'Library', 'Infirmary');;

create table lessons (
  id bigserial primary key,
  name text not null,
  level student_level not null,
  required_attendance integer not null check (required_attendance > 0)
);;

create table clubs (
  id bigserial primary key,
  name text not null
);;

create table creatures (
  id bigserial primary key,
  name text not null,
  power integer not null,
  total_hp integer not null,
  level student_level not null
);;

create table rooms (
  number bigserial primary key,
  level student_level not null,
  kind room_kind not null,
  club_id bigint references clubs,
  lesson_id bigint references lessons,

  constraint room_kind_integrity check (
    (kind = 'Classroom'
      and club_id is null
      and lesson_id is not null)
    or (kind = 'Clubroom'
      and club_id is not null
      and lesson_id is null)
    or (kind = 'Library'
      and club_id is null
      and lesson_id is null)
    or (kind = 'Infirmary'
      and club_id is null
      and lesson_id is null)
  )
);;

# --- !Downs

drop table rooms;
drop table creatures;
drop table clubs;
drop table lessons;
drop type room_kind;
drop domain student_level;
