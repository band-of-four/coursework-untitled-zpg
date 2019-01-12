defmodule Seeder.Lesson do
  use Ecto.Schema
  alias Seeder.Lesson

  @primary_key {:id, :id, autogenerate: true}
  schema "lessons" do
    field :name, :string
    field :level, :integer
    field :required_attendance, :integer
  end

  def names do
    %{ 
      0 => [
        {"Введение в курс дел", 1}
      ],
      1 => [
        {"Дискретная трансфигурация", 5},
        {"Основы вычислительной травологии", 8},
        {"Языки системного зельеварения", 7},
        {"Уход за магическими приложениями", 6}
      ],
      2 => [
        {"Функциональные чары", 13},
        {"Основы объектно-ориентированной алхимии", 12},
        {"Теория высоких технологий и низкого уровня жизни", 10}
      ],
      3 => [
        {"Диклониусоведение", 250},
        {"Влияние восточной анимации на развитие человечества", 230},
        {"Написание магической документации", 275},
        {"Психология магических разработчиков", 260}
      ]
    }
  end
  
  def records do
    names()
    |> Enum.map(fn {year, lessons} ->
      {year, Enum.map(lessons, fn {name, attendance} -> 
        %Lesson{name: name, level: year, required_attendance: attendance}
      end)}
    end)
    |> Map.new()
  end
end
