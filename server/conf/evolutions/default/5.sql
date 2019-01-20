-- vim: syntax=pgsql

# --- !Ups

alter table notes add heart_count bigint not null default 0 check (heart_count >= 0);;

create table note_hearts_users (
  user_id bigint not null references users,
  note_id bigint not null references notes,

  primary key (user_id, note_id)
);;

create function note_hearts_counter_cache_update()
  returns trigger as $$
    begin
      if tg_op = 'INSERT'
      then update notes set heart_count = heart_count + 1 where id = new.note_id;;
      end if;;

      if tg_op = 'DELETE'
      then update notes set heart_count = heart_count - 1 where id = old.note_id;;
      end if;;

      return null;;
    end;;
    $$
  language plpgsql;;

create trigger note_hearts_counter_cache_trig
  after insert or delete on note_hearts_users
  for each row execute procedure note_hearts_counter_cache_update();;

create function note_heart_toggle(in heart_user_id bigint, in heart_note_id bigint,
                                  out status text, out new_heart_count bigint)
  as $$
    declare
      note_creator_id bigint;;
      heart_deleted boolean;;
    begin
      note_creator_id := (select creator_id from notes where id = heart_note_id);;

      if note_creator_id = heart_user_id
      then
        status := 'error';;
        return;;
      end if;;

      with try_delete as (
        delete from note_hearts_users
        where user_id = heart_user_id and note_id = heart_note_id
        returning 1
      )
      select exists(select from try_delete)
      into heart_deleted;;

      if heart_deleted then
        status := 'removed';;
      else
        status := 'added';;
        insert into note_hearts_users values (heart_user_id, heart_note_id);;
      end if;;

      new_heart_count := (select heart_count from notes where id = heart_note_id);;
    end;;
  $$
  language plpgsql;;

# --- !Downs

drop function note_heart_toggle(bigint, bigint, out text, out bigint);
drop trigger note_hearts_counter_cache_trig on note_hearts_users;;
drop function note_hearts_counter_cache_update;;
drop table note_hearts_users;;
alter table notes drop column heart_count;
