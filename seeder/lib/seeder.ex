defmodule Seeder do
  alias Seeder.{Repo, Lesson, Room, Note, Creature, Spell}

  def run do
    seq_reset_queries = [
      "ALTER SEQUENCE lessons_id_seq RESTART;",
      "ALTER SEQUENCE spells_id_seq RESTART;",
      "ALTER SEQUENCE rooms_number_seq RESTART;",
      "ALTER SEQUENCE creatures_id_seq RESTART;",
      "ALTER SEQUENCE notes_id_seq RESTART;"
    ]

    Repo.delete_all(Note)
    Repo.delete_all(Room)
    Repo.delete_all(Lesson)
    Repo.delete_all(Creature)
    Repo.delete_all(Spell)

    seq_reset_queries
    |> Enum.map(fn reset_qry -> Ecto.Adapters.SQL.query!(Repo, reset_qry, []) end)

    Repo.transaction(&insert/0)
  end

  def insert do
    Note.records()
    |> Enum.each(&Repo.insert!/1)

    lessons =
      Lesson.records_with_notes()
      |> Enum.map(fn {level, lessons} ->
        {level, Enum.map(lessons, fn {lesson, notes} ->
          lesson = Repo.insert!(lesson)
          Enum.each(notes, fn note ->
            Repo.insert!(Note.build(note, lesson))
          end)
          lesson
        end)}
      end)

    Room.records(lessons)
    |> Map.values()
    |> List.flatten()
    |> Enum.each(&Repo.insert!/1)
    
    Creature.records_with_notes()
    |> Map.values()
    |> List.flatten()
    |> Enum.each(fn {creature, notes} ->
      creature = Repo.insert!(creature)
      Enum.each(notes, fn note ->
        Repo.insert!(Note.build(note, creature))
      end)
    end)

    Spell.records()
    |> Map.values()
    |> List.flatten()
    |> Enum.each(&Repo.insert!/1)
  end
end
