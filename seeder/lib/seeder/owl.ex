defmodule Seeder.Owl do
  use Ecto.Schema
  alias Seeder.Owl

  @data YamlElixir.read_from_file!("owls.yaml", atoms: true)

  @primary_key{:impl, :string, []}
  schema "owls" do
    field :display_name, :string
    field :description, :string
    field :applicable_stages, {:array, Seeder.Note.StudentStage}
    field :stages_active, :integer
    field :is_immediate, :boolean
    field :occurrence, :float
    field :level, :integer
  end

  def records do
    @data
    |> Enum.map(fn o ->
      %Owl{impl: o.impl, display_name: o.display_name, description: o.description,       
        applicable_stages: o[:applicable_stages], stages_active: o[:stages_active],
        is_immediate: o.is_immediate, occurrence: o.occurrence, level: o.level}
    end)
  end
end
