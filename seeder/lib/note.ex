defmodule Seeder.Note do
  use Ecto.Schema
  import EctoEnum
  alias Seeder.{Note, Creature, Lesson, Club}

  defenum StudentStage, :student_stage, ["Fight", "FightWon", "FightLost", "Lesson", "Travel", "Library", "Infirmary", "Club"]
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
    belongs_to :club, Club
  end

  def records do
    Enum.map(@data, &build/1)
  end

  def build(%{stage: stage, text: text, gender: gender}, %Creature{} = creature) do
    %Note{stage: stage, text: text, text_gender: gender, is_approved: true, creature: creature}
  end

  def build(%{text: text, gender: gender}, %Lesson{} = lesson) do
    %Note{stage: "Lesson", text: text, text_gender: gender, is_approved: true, lesson: lesson}
  end

  def build(%{stage: stage, text: text, gender: gender}) do
    %Note{stage: stage, text: text, text_gender: gender, is_approved: true}
  end

  def build(%{text: text, gender: gender}, %Club{} = club) do
    %Note{stage: "Club", text: text, text_gender: gender, is_approved: true, club: club}
  end
end
