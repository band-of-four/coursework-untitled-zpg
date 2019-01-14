defmodule Seeder.Note do
  use Ecto.Schema
  import EctoEnum
  alias Seeder.{Note, Creature}

  defenum StudentStage, :student_stage, ["Fight", "FightWon", "FightLost"]
  defenum StudentGender, :student_gender, ["Female", "Male"]

  @primary_key {:id, :id, autogenerate: true}
  schema "notes" do
    field :stage, StudentStage
    field :text, :string
    field :text_gender, StudentGender
    field :is_approved, :boolean
    belongs_to :creature, Creature
  end

  def build(%{stage: stage, text: text, gender: gender}, %Creature{} = creature) do
    %Note{stage: stage, text: text, text_gender: gender, is_approved: true, creature: creature}
  end
end
