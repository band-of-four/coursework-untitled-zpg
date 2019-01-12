-- vim: syntax=pgsql

# --- !Ups

create type note_kind as enum
  ('Lesson', 'Club', 'Fight', 'Infirmary');;

create table notes (
  id bigserial primary key,
  kind note_kind not null,
  text text not null,
  text_gender student_gender not null,
  creator_id bigserial not null references users,

  lesson_id bigint references lessons,
  club_id bigint references student_clubs,
  creature_id bigint references creatures,

  constraint note_kind_integrity check (
    (kind = 'Lesson'
      and lesson_id is not null
      and club_id is null
      and creature_id is null)
    or (kind = 'Club'
      and lesson_id is null
      and club_id is not null
      and creature_id is null)
    or (kind = 'Fight'
      and lesson_id is null
      and club_id is null
      and creature_id is not null)
    or (kind = 'Infirmary'
      and lesson_id is null
      and club_id is null
      and creature_id is null)
  )
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
drop table notes;
drop type note_kind;
