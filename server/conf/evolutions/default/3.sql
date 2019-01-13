-- vim: syntax=pgsql

# --- !Ups

create type student_gender as enum
  ('Female', 'Male');;

create type student_stage as enum
  ('Lesson', 'Club', 'Travel', 'Fight', 'FightWon', 'FightLost', 'Infirmary');;

create table notes (
  id bigserial primary key,
  stage student_stage not null,
  text text not null,
  text_gender student_gender not null,
  creator_id bigint references users,

  lesson_id bigint references lessons,
  club_id bigint references student_clubs,
  creature_id bigint references creatures,

  constraint note_stage_integrity check (
    (stage = 'Lesson'
      and lesson_id is not null
      and club_id is null
      and creature_id is null)
    or (stage = 'Club'
      and lesson_id is null
      and club_id is not null
      and creature_id is null)
    or ((stage = 'Fight' or stage = 'FightWon' or stage = 'FightLost')
      and lesson_id is null
      and club_id is null
      and creature_id is not null)
    or ((stage = 'Travel' or stage = 'Infirmary')
      and lesson_id is null
      and club_id is null
      and creature_id is null)
  )
);;

create table students (
  id bigserial primary key references users,
  name text not null,
  gender student_gender not null,
  level student_level not null,
  hp integer not null,
  current_room bigint not null references rooms,
  stage_note_id bigint not null references notes,
  next_stage_time timestamp not null,
  stage_start_time timestamp not null
);;

create table student_diary_entries (
  student_id bigint not null references students,
  note_id bigint not null references notes,
  date timestamp not null
);;

create unique index student_diary_entries_pkey
  on student_diary_entries (student_id, date desc);

# --- !Downs

drop index student_diary_entries_pkey;
drop table student_diary_entries;
drop table students;
drop table notes;
drop type student_stage;
drop type student_gender;
