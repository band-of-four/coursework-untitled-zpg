defmodule Orion do
  alias Orion.Messenger

  def run do 
    Messenger.send_message("Привет!", :start)
    text_processing()
  end

  def text_processing() do
    monster = get_unapproved_creature()
    text = ~s(Сегодня на повестке дня - монстр по имени #{monster.name}!)
    Messenger.send_message(text, :start)
    text = Messenger.next_text()
    new_notes = process_creature_notes(monster.notes)
    IO.inspect new_notes
    new_monster = process_creature(monster, new_notes)
    IO.inspect new_monster
  end

  def process_creature(monster, notes) do
    result = Enum.reduce(
      notes,
      %{
        has_female_fight: false,
        has_female_fight_won: false,
        has_female_fight_lose: false,
        has_male_fight: false,
        has_male_fight_won: false,
        has_male_fight_lose: false
      },
      fn
        %{stage: "Fight", gender: "Female", is_approved: true}, acc -> %{acc | has_female_fight: true}
        %{stage: "FightWon", gender: "Female", is_approved: true}, acc -> %{acc | has_female_fight_won: true}
        %{stage: "FightLose", gender: "Female", is_approved: true}, acc -> %{acc | has_female_fight_lose: true}
        %{stage: "Fight", gender: "Male", is_approved: true}, acc -> %{acc | has_male_fight: true}
        %{stage: "FightWon", gender: "Male", is_approved: true}, acc -> %{acc | has_male_fight_won: true}
        %{stage: "FightLose", gender: "Male", is_approved: true}, acc -> %{acc | has_male_fight_lose: true}
      end
    )
    |> Enum.find(fn {k, v} -> !v end)
    |> case do
      nil -> %{id: monster.id, is_approved: true}
      _ ->
        Messenger.send_message("Не могу одобрить монстра - не хватает одобренных фраз")
        notes = Enum.map(notes, fn note -> %{note | is_approved: false} end)
        %{id: monster.id, is_approved: false}
      end
    if result.is_approved == true do
      process_creature_name(monster)
    else
      %{id: monster.id, name: monster.name, is_approved: false}
    end
  end

  def edit_monster_name() do
    Messenger.send_message("Введите новое имя монстра")
    Messenger.next_text()
  end

  def process_creature_name(monster) do
    Messenger.send_message("Ну, что. Мы почти закончили с этим парнем.
      Осталось последнее одобрение. Итак, монстр, по имени #{monster.name}.
      Что думаете?", :approve)
    case Messenger.next_text() do
      "Редактировать" ->
        name = edit_monster_name()
        process_creature_name(%{monster | name: name})
      "Одобрить" -> %{id: monster.id, name: monster.name, is_approved: true}
      "Отклонить" -> %{id: monster.id, name: monster.name, is_approved: false}
    end
  end

  def process_creature_notes([head_note | notes]) do
    %{id: id, stage: stage, gender: gender, text: text} = head_note
    head_text = 
      case gender do
        "Female" -> "Когда девочки "
        "Male" -> "Когда мальчики "
      end
    mid_text =
      case stage do
        "Fight" -> "дерутся с монстром, они говорят:\n"
        "FightWon" -> "побеждают монстра, они говорят:\n"
        "FightLost" -> "пали от руки монстра, они говорят:\n"
      end
    {new_text, is_approved} = process_text(head_text, mid_text, text)
    processed_note = %{id: id, text: new_text, is_approved: is_approved, stage: stage, gender: gender}
    [processed_note | process_creature_notes(notes)]
  end
  def process_creature_notes([]), do: []

  def process_text(head_text, mid_text, text) do
    Messenger.send_message([head_text | [mid_text | "-- #{text}\n"]], :approve)
    case Messenger.next_text() do
      "Редактировать" ->
        new_text = Messenger.ask("Напишите новую версию фразы")
        process_text(head_text, mid_text, new_text)
      "Одобрить" ->
        {text, true}
      "Отклонить" ->
        {text, false}
    end
  end

  def edit_message() do
  end

  def get_unapproved_creature() do
    string = ~s({"name": "Сама смерть", "notes": [{"id": 1, "stage": "Fight", 
      "gender": "Female", "text": "Ну вот и все."},
      {"id": 2, "stage": "FightWon", "gender": "Female", "text": "Я теперь бессмертна?"}]})
    Poison.Parser.parse!(string, %{keys: :atoms})
  end

  #def build_message(%{lesson: lesson, text: text, gender: gender})

  #def build_message(%{club: club, text: text, gender: gender})

  #def build_message(%{travel: true, text: text, gender: gender})
end
