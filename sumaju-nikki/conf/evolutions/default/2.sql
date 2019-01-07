-- vim: syntax=pgsql

-- Entities that implement the _travel_ game mechanic (`students` travelling between `rooms`)

# --- !Ups

create domain student_level as smallint
  constraint student_level_range check (value >= 0 and value <= 7);;

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
  club_id bigint references student_clubs,
  lesson_id bigint references lessons
);;

create table students (
  id bigserial primary key,
  name text not null,
  level student_level not null,
  current_room bigint not null references rooms,
  stage text not null,
  next_stage_time timestamp not null
);;


# --- !Downs

drop table students;
drop table rooms;
drop table student_clubs;
drop table lessons;
