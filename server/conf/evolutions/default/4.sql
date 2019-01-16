-- vim: syntax=pgsql

# --- !Ups

create type spell_kind as enum
  ('Attack', 'Defence', 'Luck', 'Charisma');;

create table spells (
  id bigserial primary key,
  kind spell_kind not null,
  power integer not null,
  level student_level not null,
  name text not null
);;

create table spells_students (
  student_id bigint references students,
  spell_id bigint references spells,

  primary key (student_id, spell_id)
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
  student_id bigint primary key references students,
  creature_id bigint references creatures,
  creature_hp integer not null
);;

create table creature_handling_skills (
  student_id bigint references students,
  creature_id bigint references creatures,
  modifier smallint not null check (modifier >= 0 and modifier <= 100),

  primary key (student_id, creature_id)
);;

create function creature_fights_end_updating_skill(in in_student_id bigint, in in_mod_delta smallint)
  returns void as $$
    begin
      with fight as (
        delete from creature_fights
          where student_id = in_student_id
          returning creature_id
      )
      insert into creature_handling_skills as chs (student_id, creature_id, modifier)
        select in_student_id, fight.creature_id, in_mod_delta
        from fight
      on conflict (student_id, creature_id)
        do update set modifier = least(100, chs.modifier + in_mod_delta);;
    end;;
  $$
  language plpgsql;;

# --- !Downs

drop function creature_fights_end_updating_skill(bigint, smallint);
drop table creature_handling_skills;
drop table creature_fights;
drop trigger spells_students_integrity_trig on students;
drop function check_student_spells_all_kinds_present;
drop table spells_students;
drop table spells;
drop type spell_kind;
