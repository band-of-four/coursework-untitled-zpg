defmodule Seeder.Spell do
  use Ecto.Schema
  import EctoEnum
  alias Seeder.Spell

  defenum SpellKind, :spell_kind, ["Attack", "Defence", "Luck", "Charisma"]

  @data YamlElixir.read_from_file!("spells.yaml", atoms: true)

  @primary_key {:id, :id, autogenerate: true}
  schema "spells" do
    field :name, :string
    field :kind, SpellKind
    field :power, :integer
    field :level, :integer
  end

  def records do
    @data
    |> Enum.map(fn {year, spells} ->
      {year, Enum.map(spells, fn spell -> 
        %Spell{name: spell.name, level: year, power: spell.power, kind: spell.kind}
      end)}
    end)
    |> Map.new()
  end
end
