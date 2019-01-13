defmodule Seeder.Spell do
  use Ecto.Schema
  import EctoEnum
  alias Seeder.Spell

  defenum SpellKind, :spell_kind, ["Attack", "Defence", "Luck"]

  @primary_key {:id, :id, autogenerate: true}
  schema "spells" do
    field :name, :string
    field :kind, SpellKind
    field :power, :integer
    field :level, :integer
  end

  def names do
    %{
      0 => [
        {"Удар палочкой", "Attack", 5},
        {"Блок", "Defence", 2},
        {"Скрестить пальцы", "Luck", 1}
      ]
    }
  end

  def records do
    names()
    |> Enum.map(fn {year, spells} ->
      {year, Enum.map(spells, fn {name, type, power} -> 
        %Spell{name: name, level: year, power: power, kind: type}
      end)}
    end)
    |> Map.new()
  end
end
