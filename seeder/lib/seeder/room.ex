defmodule Seeder.Room do
  use Ecto.Schema
  import EctoEnum

  defenum RoomKind, :room_kind, ["Classroom", "Clubroom", "Library", "Infirmary"]

  @primary_key {:number, :id, autogenerate: true}
  schema "rooms" do
    field :kind, RoomKind
    field :level, :integer
    belongs_to :lesson, Seeder.Lesson
  end
end
