-- vim: syntax=pgsql

# --- !Ups

create table owls (
  id bigserial primary key,
  impl text not null,
  display_name text not null,
  description text not null,
  applicable_stages student_stage[],
  stages_active integer,
  is_immediate boolean not null,

  constraint immediacy_integrity check (
    (is_immediate and stages_active is null) or
    (not is_immediate and stages_active is not null and applicable_stages is not null)
  )
);;

create table owls_students (
  student_id bigint references students,
  owl_id bigint references owls,
  owl_count smallint not null check (owl_count >= 0),
  active_stages_left smallint check (active_stages_left > 0),

  primary key (student_id, owl_id)
);;

# --- !Downs

drop table owls_students;
drop table owls;
