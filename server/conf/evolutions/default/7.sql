-- vim: syntax=pgsql

# --- !Ups

create table owls (
  impl text primary key,
  display_name text not null,
  description text not null,
  applicable_stages student_stage[],
  stages_active integer,
  is_immediate boolean not null,
  occurrence float not null,
  level student_level not null,

  constraint immediacy_integrity check (
    (is_immediate and stages_active is null) or
    (not is_immediate and stages_active is not null and applicable_stages is not null)
  )
);;

create table owls_students (
  student_id bigint references students,
  owl_impl text references owls,
  owl_count smallint not null check (owl_count >= 0),
  active_stages_left smallint check (active_stages_left > 0),

  primary key (student_id, owl_impl)
);;

create function owls_add_random_to_student(in in_student_id bigint)
  returns void as $$
    begin
      with available_owls as (
        select o.impl, o.occurrence from owls o
        inner join students s on s.id = in_student_id and s.level >= o.level
      ), rand as (
        select random() * (select sum(occurrence) from available_owls) r
      )
      insert into owls_students as os (student_id, owl_impl, owl_count, active_stages_left)
        select in_student_id, sample.impl, 1, null
        from (
          -- https://stackoverflow.com/a/13040717/1726690
          select impl, sum(occurrence) over (order by impl) occ, r
          from available_owls
          cross join rand
        ) as sample
        where sample.occ > sample.r
        order by sample.impl
        limit 1
      on conflict (student_id, owl_impl)
         do update set owl_count = os.owl_count + 1;;
    end;;
  $$
  language plpgsql;;

# --- !Downs

drop function owls_add_random_to_student(bigint);
drop table owls_students;
drop table owls;
