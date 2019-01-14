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

create table relationships (
  student_a bigint not null references students,
  student_b bigint not null references students,
  relationship integer
);;

# --- !Downs
drop table relationships cascade;
drop table student_letters cascade;
drop table lesson_attendances cascade;
