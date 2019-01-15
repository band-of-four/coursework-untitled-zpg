-- vim: syntax=pgsql

# --- !Ups

create table student_library_visits (
  student_id bigint primary key references students,
  acquiring_spell_id bigint not null references spells
);;

create function student_library_visit_end(in in_student_id bigint)
  returns void as $$
    declare
      spell_id bigint;;
      spell_kind spell_kind;;
    begin
      select id, kind from spells s
        inner join student_library_visits slv
        on s.id = slv.acquiring_spell_id and slv.student_id = in_student_id
      into spell_id, spell_kind;;

      delete from spells_students spst
        using spells s
        where spst.student_id = student_id and spst.spell_id = s.id and s.kind = spell_kind;;

      insert into spells_students (student_id, spell_id)
        values (in_student_id, spell_id);;

      delete from student_library_visits slv
        where slv.student_id = student_id;;
    end;;
  $$
  language plpgsql;;

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

drop table relationships;;
drop table student_letters;;
drop table lesson_attendances;;
drop function student_library_visit_end(bigint);;
drop table student_library_visits;;
