defmodule Orion do
  @chat_id 487867125

  def run do 
    last_update_id = start()
    keyboards = get_keyboards()
    Nadia.send_message(@chat_id, "Привет!")
    text_processing(last_update_id)
  end

  def get_keyboards() do
    start_button = [[%Nadia.Model.KeyboardButton{text: "Поехали!"}]]
    approve_buttons = [[%Nadia.Model.KeyboardButton{text: "Одобрить"}, %Nadia.Model.KeyboardButton{text: "Отклонить"}], [%Nadia.Model.KeyboardButton{text: "Редактировать"}], [%Nadia.Model.KeyboardButton{text: "Следующая фраза"}]]
    %{approve: %Nadia.Model.ReplyKeyboardMarkup{keyboard: approve_buttons},
      start: %Nadia.Model.ReplyKeyboardMarkup{keyboard: start_button}}
  end

  def text_processing(last_update_id) do
    monster = get_unapproved_creature()
    text = ~s(Сегодня на повестке дня - монстр по имени #{monster.name}!)
    Nadia.send_message(@chat_id, text, [{:reply_markup, get_keyboards().start}])
    update = get_update(last_update_id + 1)
    new_notes = process_creature_notes(monster.notes, last_update_id)
  end

  def process_creature_notes([head_note | notes], last_update_id) do
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
    {new_text, is_approved} = process_text(head_text, mid_text, text, last_update_id + 1)
    processed_note = %{id: id, text: new_text, is_approved: is_approved}
    [processed_note | process_creature_notes(notes, last_update_id + 1)]
  end
  def process_creature_notes([], _), do: []

  def process_text(head_text, mid_text, text, last_update_id) do
    Nadia.send_message(@chat_id, [head_text | [mid_text | "-- #{text}\n"]], [reply_markup: get_keyboards().approve])
    update = get_update(last_update_id + 1)
    case update.message.text do
      "Редактировать" ->
        {last_update_id, new_text} = edit_message(head_text, mid_text, update)
        process_text(head_text, mid_text, new_text, last_update_id)
      "Одобрить" ->
        {text, true}
      "Отклонить" ->
        {text, false}
    end
  end

  def edit_message(head_text, mid_text, update) do
    Nadia.send_message(@chat_id, "Напишите новую версию фразы")
    update = get_update(update.update_id  + 1)
    new_text = update.message.text
    {update.update_id, new_text}
  end

  def get_update(last_update_id \\ -1) do
    case Nadia.get_updates([offset: last_update_id, limit: 1, timeout: 30]) do
      {:ok, [update]} -> update
      _ -> get_update(last_update_id)
    end
  end

  def start() do
    update = get_update()
    case update.message.text do
      "/start" -> update.update_id
      _ -> start()
    end
  end

  def get_unapproved_creature() do
    string = ~s({"name": "Сама смерть", "notes": [{"id": 1, "stage": "Fight", 
      "gender": "Female", "text": "Ну вот и все."},
      {"id": 2, "stage": "FightWon", "gender": "Female", "text": "Я теперь бессмертна?"}]})
    Poison.Parser.parse!(string, %{keys: :atoms})
  end

  #def build_mesage(%{lesson: lesson, text: text, gender: gender})

  #def build_message(%{club: club, text: text, gender: gender})

  #def build_message(%{travel: true, text: text, gender: gender})
end
