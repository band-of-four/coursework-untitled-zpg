defmodule Orion do
  import Nadia
  @chat_id 487867125

  def run do
    button = %Nadia.Model.KeyboardButton{text: "Hello"}
    keyboard = %Nadia.Model.ReplyKeyboardMarkup{keyboard: [[button]]}
    Nadia.send_message(@chat_id, "Hello!", [{:reply_markup, keyboard}])
  end
end
