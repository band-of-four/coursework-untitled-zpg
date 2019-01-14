-- vim: syntax=pgsql

# --- !Ups

create table owls (
  id bigserial primary key,
  name text not null,
  description text not null,
  applicable_stage student_stage,
  stages_active integer not null
);;

create table owls_students (
  student_id bigint references students,
  owl_id bigint references owls,
  owl_count smallint not null check (owl_count >= 0),
  active_stages_left smallint check (active_stages_left > 0),

  primary key (student_id, owl_id)
);;

create function owl_apply(in user_id bigint, in target_owl_id bigint, out applied boolean)
  as $$
  declare
    begin
      if exists(
          select from owls_students os
          where os.student_id = user_id and os.owl_id = target_owl_id
            and os.owl_count > 0 and active_stages_left is null
        )
      then
        update owls_students os
        set owl_count = owl_count - 1,
            active_stages_left = o.stages_active
        from (select id, stages_active from owls) as o
        where os.student_id = user_id and os.owl_id = target_owl_id
          and o.id = os.owl_id;;

        applied := true;;
      else
        applied := false;;
      end if;;
    end;;
  $$
  language plpgsql;;

# --- !Downs

drop function owl_apply(bigint, bigint, out boolean);
drop table owls_students;
drop table owls;
