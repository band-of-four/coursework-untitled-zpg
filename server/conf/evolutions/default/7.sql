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
drop table relationships cascade;
drop table student_letters cascade;
drop table lesson_attendances cascade;
drop table owls_students cascade;
