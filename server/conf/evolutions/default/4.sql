-- vim: syntax=pgsql

# --- !Ups

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
drop table diary cascade;
drop table relationships cascade;
drop table phrases cascade;
drop table student_letters cascade;
drop table lesson_attendances cascade;
drop table owls_students cascade;
