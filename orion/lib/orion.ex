defmodule Orion do
  import Nadia
  @chat_id 487867125

  def run do 
    last_update_id = start()
    text = "Какие-то фразы на одобрение"
    buttons = [[%Nadia.Model.KeyboardButton{text: "Одобрить"}, %Nadia.Model.KeyboardButton{text: "Отклонить"}], [%Nadia.Model.KeyboardButton{text: "Редактировать"}]]
    keyboard = %Nadia.Model.ReplyKeyboardMarkup{keyboard: buttons}
    Nadia.send_message(@chat_id, "Привет!")
    text_processing(text, last_update_id, keyboard)
  end

  def text_processing(text, last_update_id, keyboard) do
    Nadia.send_message(@chat_id, text, [{:reply_markup, keyboard}])
    update = get_update(last_update_id + 1)
    case update.message.text do
      "Редактировать" ->
        Nadia.send_message(@chat_id, "Напишите новую версию фразы")
        update = get_update(update.update_id  + 1)
        text = update.message.text
        text_processing(text, update.update_id, keyboard)
      _ -> Nadia.send_message(@chat_id, "Оки")
    end
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
end
