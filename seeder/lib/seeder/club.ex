defmodule Seeder.Club do
  use Ecto.Schema
  alias Seeder.Club

  @data YamlElixir.read_from_file!("clubs.yaml", atoms: true)

  @primary_key {:id, :id, autogenerate: true}
  schema "clubs" do
    field :name, :string
  end

  def records do
    @data
    |> Enum.map( fn {level, clubs} ->
      {level, Enum.map(clubs, fn club ->
       {%Club{name: club.name}, club.notes } end)}
    end)
  end
end
