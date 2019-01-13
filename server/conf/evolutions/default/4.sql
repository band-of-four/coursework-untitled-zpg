-- vim: syntax=pgsql

# --- !Ups

create type spell_kind as enum
  ('Attack', 'Defence', 'Luck');;

create table spells (
  id bigserial primary key,
  kind spell_kind not null,
  power integer not null,
  level student_level not null,
  name text not null
);;

create table spells_students (
  spell_id bigint references spells,
  student_id bigint references students,

  primary key (spell_id, student_id)
);;

create function check_student_spells_all_kinds_present()
  returns trigger as $$
    begin
      if exists(
        select unnest(enum_range(NULL::spell_kind))
        except (
            select s.kind from spells s
            inner join spells_students spst on
            s.id = spst.spell_id and spst.student_id = new.id
          )
        )
      then raise exception 'new student entries must have spells of all existing kinds assigned to them (via spells_students)';;
      end if;;
      return null;;
    end;;
  $$
  language plpgsql;;

create constraint trigger spells_students_integrity_trig
  after insert on students
  deferrable initially deferred
  for each row execute procedure check_student_spells_all_kinds_present();;

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
drop trigger spells_students_integrity_trig on students;
drop function check_student_spells_all_kinds_present;
drop table spells_students;
drop table spells;
drop type spell_kind;
