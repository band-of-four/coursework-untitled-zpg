defmodule Seeder.Room do
  use Ecto.Schema
  import EctoEnum
  alias Seeder.Room

  defenum RoomKind, :room_kind, ["Classroom", "Clubroom", "Library", "Infirmary"]

  @primary_key {:number, :id, autogenerate: true}
  schema "rooms" do
    field :kind, RoomKind
    field :level, :integer
    belongs_to :lesson, Seeder.Lesson
  end

  def records(level_lessons) do
    level_lessons
    |> Enum.map(fn {level, lessons} -> 
      rooms = Enum.map(lessons, fn lesson ->
        %Room{kind: "Classroom", level: level, lesson: lesson}
      end)
      rooms = [%Room{kind: "Library", level: level} | rooms]
      rooms = [%Room{kind: "Infirmary", level: level} | rooms]
      {level, Enum.shuffle(rooms)}
    end)
    |> Map.new()
  end
end
