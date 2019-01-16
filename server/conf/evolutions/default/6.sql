-- vim: syntax=pgsql

# --- !Ups

create function check_creature_notes_all_kinds_present()
  returns trigger as $$
    begin
      if exists(
          values ('Fight'::student_stage, 'Female'::student_gender),
                 ('Fight'::student_stage, 'Male'::student_gender),
                 ('FightWon'::student_stage, 'Female'::student_gender),
                 ('FightWon'::student_stage, 'Male'::student_gender),
                 ('FightLost'::student_stage, 'Female'::student_gender),
                 ('FightLost'::student_stage, 'Male'::student_gender)
          except
          select stage, text_gender from notes where creature_id = new.id
        )
      then raise exception 'new creature entries must have associated notes for Fight, FightWon, FightLost stages for all genders';;
      end if;;
      return null;;
    end;;
    $$
  language plpgsql;;

create constraint trigger notes_creatures_integrity_trig
  after insert on creatures
  deferrable initially deferred
  for each row execute procedure check_creature_notes_all_kinds_present();;

# --- !Downs

drop trigger notes_creatures_integrity_trig on creatures;
drop function check_creature_notes_all_kinds_present();
