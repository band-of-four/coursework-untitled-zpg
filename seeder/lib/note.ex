defmodule Seeder.Note do
  use Ecto.Schema
  import EctoEnum
  alias Seeder.{Note, Creature, Lesson}

  defenum StudentStage, :student_stage, ["Fight", "FightWon", "FightLost", "Lesson", "Travel", "Library", "Infirmary"]
  defenum StudentGender, :student_gender, ["Female", "Male"]

  @data YamlElixir.read_from_file!("notes.yaml", atoms: true)

  @primary_key {:id, :id, autogenerate: true}
  schema "notes" do
    field :stage, StudentStage
    field :text, :string
    field :text_gender, StudentGender
    field :is_approved, :boolean
    belongs_to :creature, Creature
    belongs_to :lesson, Lesson
  end

  def records do
    Enum.map(@data, &build/1)
  end

  def build(%{stage: stage, text: text, gender: gender}, %Creature{} = creature) do
    %Note{stage: stage, text: text, text_gender: gender, is_approved: true, creature: creature}
  end

  def build(%{stage: stage, text: text, gender: gender}, %Lesson{} = lesson) do
    %Note{stage: stage, text: text, text_gender: gender, is_approved: true, lesson: lesson}
  end

  def build(%{stage: stage, text: text, gender: gender}) do
    %Note{stage: stage, text: text, text_gender: gender, is_approved: true}
  end
end
