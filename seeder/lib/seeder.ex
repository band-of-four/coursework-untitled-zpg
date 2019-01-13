defmodule Seeder do
  alias Seeder.{Repo, Lesson, Room, Creature, Spell}

  def run do
    Lesson.records()
    |> Map.values()
    |> List.flatten()
    |> Enum.each(&Repo.insert!/1)
    Creature.records()
    |> Map.values()
    |> List.flatten()
    |> Enum.each(&Repo.insert!/1)
    Spell.records()
    |> Map.values()
    |> List.flatten()
    |> Enum.each(&Repo.insert!/1)
  end
end
