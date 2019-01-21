defmodule Orion do
  alias Orion.Messenger
  alias Orion.Nikki

  def run do 
    data_processing()
  end


  def data_processing do
    Messenger.send_message("Надеюсь на продуктивную работу сегодня!\nВыбери тип данных", :type_choice)
    text = Messenger.next_text()
    case text do
      "Загрузить существо" -> creature_processing()
      "Загрузить фразу" -> note_processing()
      _ ->
        Messenger.send_message("Не понимаю тебя, пожалуйста, используй кнопки", :start)
        data_processing()
    end
  end

  def creature_processing() do
    case Nikki.get_unapproved_creature() do
      nil ->
        Messenger.send_message("Нет ни одного существа для одобрения(")
        data_processing()
      monster ->
        text = ~s(Сегодня на повестке дня - монстр по имени #{monster.name}!)
        Messenger.send_message(text, :start)
        text = Messenger.next_text()
        new_notes = process_creature_notes(monster.notes)
        new_monster = process_creature(monster, new_notes)
        new_notes = Enum.map(new_notes, fn note ->
          %{id: note.id, text: note.text, isApproved: note.isApproved}
        end)
        new_monster = Map.put(new_monster, :notes, new_notes)
        Nikki.post_approved_creature(new_monster)
        Messenger.send_message("Готово :)", :start)
        data_processing()
    end
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
        %{stage: "Fight", gender: "Female", isApproved: true}, acc -> %{acc | has_female_fight: true}
        %{stage: "FightWon", gender: "Female", isApproved: true}, acc -> %{acc | has_female_fight_won: true}
        %{stage: "FightLost", gender: "Female", isApproved: true}, acc -> %{acc | has_female_fight_lose: true}
        %{stage: "Fight", gender: "Male", isApproved: true}, acc -> %{acc | has_male_fight: true}
        %{stage: "FightWon", gender: "Male", isApproved: true}, acc -> %{acc | has_male_fight_won: true}
        %{stage: "FightLost", gender: "Male", isApproved: true}, acc -> %{acc | has_male_fight_lose: true}
      end
    )
    |> Enum.find(fn {k, v} -> !v end)
    |> case do
      nil -> %{id: monster.id, isApproved: true}
      _ ->
        Messenger.send_message("Не могу одобрить монстра - не хватает одобренных фраз", :start)
        notes = Enum.map(notes, fn note -> %{note | isApproved: false} end)
        %{id: monster.id, isApproved: false}
      end
    if result.isApproved == true do
      process_creature_name(monster)
    else
      %{id: monster.id, name: monster.name, isApproved: false}
    end
  end

  def edit_monster_name() do
    Messenger.send_message("Введите новое имя монстра")
    Messenger.next_text()
  end

  def process_creature_name(monster) do
    Messenger.send_message("""
      Ну, что. Мы почти закончили с этим парнем.
      Осталось последнее одобрение. Итак, монстр, по имени #{monster.name}.
      Что думаете?
      """, :approve)
    case Messenger.next_text() do
      "Редактировать" ->
        name = edit_monster_name()
        process_creature_name(%{monster | name: name})
      "Одобрить" -> %{id: monster.id, name: monster.name, isApproved: true}
      "Отклонить" -> %{id: monster.id, name: monster.name, isApproved: false}
      _ ->
        Messenger.send_message("Ну и что ты хотел этим сказать? У тебя есть кнопки - используй их!", :start)
        process_creature_name(monster)
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
    {new_text, isApproved} = process_text(head_text, mid_text, text)
    processed_note = %{id: id, text: new_text, isApproved: isApproved, stage: stage, gender: gender}
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
      _ ->
        Messenger.send_message("Ну и что ты хотел этим сказать? У тебя есть кнопки - используй их!", :start)
        process_text(head_text, mid_text, text)
    end
  end

  def note_processing do
    case Nikki.get_unapproved_note() do
      nil ->
        Messenger.send_message("Нет ни одной фразы на одобрение(", :start)
        data_processing()
      note ->
        head_text =
          case note.gender do
            "Female" -> "Когда девочки "
            "Male" -> "Когда мальчики "
          end
        mid_text =
          case note.stage do
            "Club" -> " приходят в клуб " <> note.name <> ", они говорят:\n"
            "Lesson" -> " приходят на урок " <> note.name <> ", они говорят:\n"
            "Travel" -> " путешествуют, они говорят:\n"
            "Library" -> " приходят в библиотеку, они говорят:\n"
            "Infirmary" -> " приходят в медпункт, они говорят:\n"
          end
        {new_text, isApproved} = process_text(head_text, mid_text, note.text)
        new_note = %{id: note.id, text: new_text, isApproved: isApproved}
        Nikki.post_approved_note(new_note)
        Messenger.send_message("Готово :)", :start)
        data_processing()
    end
  end
end
