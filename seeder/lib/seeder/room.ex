defmodule Seeder.Room do
  use Ecto.Schema
  import EctoEnum
  alias Seeder.{Room, Lesson, Club}

  defenum RoomKind, :room_kind, ["Classroom", "Clubroom", "Library", "Infirmary"]

  @primary_key {:number, :id, autogenerate: true}
  schema "rooms" do
    field :kind, RoomKind
    field :level, :integer
    belongs_to :lesson, Lesson
    belongs_to :club, Club
  end

  def build(level, %Lesson{} = lesson) do
    %Room{kind: "Classroom", level: level, lesson: lesson}
  end

  def build(level, %Club{} = club) do
    %Room{kind: "Clubroom", level: level, club: club}
  end

  def records(level_lessons, level_clubs) do
    Map.merge(level_lessons, level_clubs, fn _k, v1, v2 ->
      v1 ++ v2
    end)
    |> Enum.map(fn {level, records} -> 
      rooms = Enum.map(records, fn record ->
        build(level, record)
      end)
      rooms = [%Room{kind: "Library", level: level} | rooms]
      rooms = [%Room{kind: "Infirmary", level: level} | rooms]
      {level, Enum.shuffle(rooms)}
    end)
    |> Map.new()
  end
end
