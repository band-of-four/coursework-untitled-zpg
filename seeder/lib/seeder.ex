defmodule Seeder do
  alias Seeder.{Repo, Lesson, Room}

  def run do
    Lesson.records()
    |> Map.values()
    |> List.flatten()
    |> Enum.each(&Repo.insert!/1)
  end
end
