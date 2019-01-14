defmodule Seeder.Note do
  use Ecto.Schema
  import EctoEnum
  alias Seeder.{Note, Creature, Lesson}

  defenum StudentStage, :student_stage, ["Fight", "FightWon", "FightLost", "Lesson", "Travel"]
  defenum StudentGender, :student_gender, ["Female", "Male"]

  @primary_key {:id, :id, autogenerate: true}
  schema "notes" do
    field :stage, StudentStage
    field :text, :string
    field :text_gender, StudentGender
    field :is_approved, :boolean
    belongs_to :creature, Creature
    belongs_to :lesson, Lesson
  end

  def travel_records do
    [
      {"Крыша в огне -- пусть полыхает, шампанское в руке, мне хорошо", "Female"},
      {"Крыша в огне -- пусть полыхает, шампанское в руке, мне хорошо", "Male"}
    ]
    |> Enum.map(fn {text, gender} ->
      %Note{stage: "Travel", text: text, text_gender: gender, is_approved: true}
    end)
  end

  def build(%{stage: stage, text: text, gender: gender}, %Creature{} = creature) do
    %Note{stage: stage, text: text, text_gender: gender, is_approved: true, creature: creature}
  end

  def build(%{stage: stage, text: text, gender: gender}, %Lesson{} = lesson) do
    %Note{stage: stage, text: text, text_gender: gender, is_approved: true, lesson: lesson}
  end
end
