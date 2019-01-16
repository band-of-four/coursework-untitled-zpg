-- vim: syntax=pgsql

# --- !Ups

create type student_gender as enum
  ('Female', 'Male');;

create type student_stage as enum
  ('Lesson', 'Club', 'Travel', 'Fight', 'FightWon', 'FightLost', 'Infirmary', 'Library');;

create table notes (
  id bigserial primary key,
  stage student_stage not null,
  text text not null,
  text_gender student_gender not null,
  creator_id bigint references users,
  is_approved boolean not null default false,

  lesson_id bigint references lessons,
  club_id bigint references clubs,
  creature_id bigint references creatures,

  constraint note_stage_integrity check (
    (stage = 'Lesson'
      and lesson_id is not null
      and club_id is null and creature_id is null)
    or (stage = 'Club'
      and club_id is not null
      and lesson_id is null and creature_id is null)
    or (stage in ('Fight', 'FightWon', 'FightLost')
      and creature_id is not null
      and lesson_id is null and club_id is null)
    or (stage in ('Travel', 'Library', 'Infirmary')
      and lesson_id is null and club_id is null and creature_id is null)
  )
);;

create index notes_lessons_gender_approved_idx
  on notes (lesson_id, text_gender)
  where is_approved and stage = 'Lesson';;

create index notes_clubs_gender_approved_idx
  on notes (club_id, text_gender)
  where is_approved and stage = 'Club';;

create index notes_creatures_gender_approved_idx
  on notes (creature_id, text_gender)
  where is_approved and stage in ('Fight', 'FightWon', 'FightLost');;

create index notes_generic_stage_gender_approved_idx
  on notes (stage, text_gender)
  where is_approved and stage in ('Travel', 'Library', 'Infirmary');;

create table students (
  id bigint primary key references users,
  name text unique not null,
  gender student_gender not null,
  level student_level not null,
  hp integer not null,
  current_room bigint not null references rooms,
  stage_note_id bigint not null references notes,
  next_stage_time timestamp not null,
  stage_start_time timestamp not null
);;

create index students_next_stage_time_idx
  on students (next_stage_time asc);;

create table student_diary_entries (
  student_id bigint not null references students,
  note_id bigint not null references notes,
  date timestamp not null
);;

create unique index student_diary_entries_pkey
  on student_diary_entries (student_id, date desc);;

# --- !Downs

drop table student_diary_entries;
drop table students;
drop table notes;
drop type student_stage;
drop type student_gender;
