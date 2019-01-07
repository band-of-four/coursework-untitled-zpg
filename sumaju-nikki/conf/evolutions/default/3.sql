-- vim: syntax=pgsql

-- Entities that implement the _fight_ game mechanic (`students` fighting `creatures`)

# --- !Ups

create table spells (
  id bigserial primary key,
  kind text not null,
  name text not null,
  power integer not null,
  level student_level not null
);;

create table spells_students (
  spell_id bigint references spells,
  student_id bigint references students,

  primary key (spell_id, student_id)
);;

create table creatures (
  id bigserial primary key,
  name text not null,
  power integer not null,
  total_hp integer not null,
  level student_level not null
);;

create table creature_fights (
  creature_id bigint references creatures,
  student_id bigint references students,
  creature_hp integer not null,

  primary key (creature_id, student_id)
);;

create table creature_handling_skills (
  creature_id bigint references creatures,
  student_id bigint references students,
  modifier integer not null,

  primary key (creature_id, student_id)
);;

# --- !Downs

drop table creature_handling_skills;
drop table creature_fights;
drop table creatures;
drop table spells_students;
drop table spells;
