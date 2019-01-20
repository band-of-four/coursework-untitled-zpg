defmodule Seeder do
  alias Seeder.{Repo, Lesson, Room, Note, Creature, Spell, Owl, Club}

  def run do

    Ecto.Adapters.SQL.query!(Repo, "DELETE FROM students CASCADE;", [])
    
    Repo.delete_all(Owl)
    Repo.delete_all(Room)
    Repo.delete_all(Note)
    Repo.delete_all(Club)
    Repo.delete_all(Lesson)
    Repo.delete_all(Creature)
    Repo.delete_all(Spell)

    [
      "ALTER SEQUENCE lessons_id_seq RESTART;",
      "ALTER SEQUENCE clubs_id_seq RESTART;",
      "ALTER SEQUENCE spells_id_seq RESTART;",
      "ALTER SEQUENCE rooms_number_seq RESTART;",
      "ALTER SEQUENCE creatures_id_seq RESTART;",
      "ALTER SEQUENCE notes_id_seq RESTART;"
    ]
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
      |> Map.new()

    clubs =
      Club.records()
      |> Enum.map(fn {level, clubs} ->
        {level, Enum.map(clubs, fn {club, notes} ->
          club = Repo.insert!(club)
          Enum.each(notes, fn note ->
            Repo.insert!(Note.build(note, club))
          end)
          club
        end)}
      end)
      |> Map.new()

    Room.records(lessons, clubs)
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

    Enum.each(Owl.records(), &Repo.insert!/1)
  end
end
