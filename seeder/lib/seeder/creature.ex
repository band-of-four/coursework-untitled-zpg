defmodule Seeder.Creature do
  use Ecto.Schema
  alias Seeder.Creature

  @data YamlElixir.read_from_file!("creatures.yaml", atoms: true)

  @primary_key {:id, :id, autogenerate: true}
  schema "creatures" do
    field :name, :string
    field :power, :integer
    field :total_hp, :integer
    field :level, :integer
    field :is_approved, :boolean
  end

  def names do
    %{
      0 => [
        {"Статист", 1, 10}
      ],
      1 => [
        {"Слизень", 3, 20},
        {"Адепт ордена X", 7, 35},
        {"Задира", 5, 28}
      ],
      2 => [
        {"Последователь ордена X", 12, 45},
        {"Тролль (маленький)", 15, 50},
        {"Злой куст", 13, 40},
        {"Дверь", 16, 55}
      ],
      3 => [
        {"Мастер ордена X", 25, 85},
        {"Жуконожка", 30, 80},
        {"Тупае", 27, 90}
      ]
    }
  end 
  
  def records_with_notes do
    @data
    |> Enum.map(fn {level, creatures} ->
      {level, Enum.map(creatures, fn creature ->
        {%Creature{name: creature.name, power: creature.power, total_hp: creature.hp, level: creature.level, is_approved: true},
         creature.notes}
      end)}
    end)
    |> Map.new()
  end
end
