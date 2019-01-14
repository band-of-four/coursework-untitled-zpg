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

  def records do
    Seeder.Lesson.records()
    |> Enum.map(fn {k, v} -> 
      List.foldl(v, [], fn l, acc ->
        [%Room{kind: "Classroom", level: k, lesson: l} | acc]
      end)
    end)
    |> Enum.map(fn arr ->
      [%Room{kind: "Library", level: List.first(arr).level} | arr]
    end)
    |> Enum.map(fn arr ->
      [%Room{kind: "Infirmary", level: List.first(arr).level} | arr]
    end)
    |> Enum.map(fn arr ->
      {List.first(arr).level, Enum.shuffle(arr)}
    end)
    |> Map.new
  end
end
