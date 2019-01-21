defmodule Orion.Messenger do
  use GenServer
  
  @chat_id 487867125

  def start_link(_opts) do
    GenServer.start_link(__MODULE__, :ok, [name: Orion.Messenger])
  end

  def send_message(text, keyboard \\ nil) do
    GenServer.cast(Orion.Messenger, {:send_text, text, keyboard})
  end

  def next_text() do
    GenServer.call(Orion.Messenger, :get_next_text, :infinity)
  end

  def ask(text, keyboard \\ nil) do
    GenServer.call(Orion.Messenger, {:ask, text, keyboard}, :infinity)
  end

  # Server

  @impl true
  def init(:ok) do
    {:ok, -1}
  end

  @impl true
  def handle_call({:ask, text, keyboard}, _from, last_update_id) do
    Nadia.send_message(@chat_id, text, build_markup(keyboard))
    %{update_id: update_id, message: %{text: text}} = get_update(last_update_id + 1)
    {:reply, text, update_id}
  end

  @impl true
  def handle_call(:get_next_text, _from, last_update_id) do
    %{update_id: update_id, message: %{text: text}} = get_update(last_update_id + 1)
    {:reply, text, update_id}
  end

  @impl true
  def handle_cast({:send_text, text, keyboard}, last_update_id) do
    Nadia.send_message(@chat_id, text, build_markup(keyboard))
    {:noreply, last_update_id}
  end

  defp get_update(last_update_id) do
    case Nadia.get_updates([offset: last_update_id, limit: 1, timeout: 30]) do
      {:ok, [update]} -> update
      _ -> get_update(last_update_id)
    end
  end
  
  defp build_markup(:start) do
    buttons = [[%Nadia.Model.KeyboardButton{text: "Далее"}]]
    [reply_markup: %Nadia.Model.ReplyKeyboardMarkup{keyboard: buttons}]
  end
  defp build_markup(:approve) do
    buttons = [[%Nadia.Model.KeyboardButton{text: "Одобрить"}, %Nadia.Model.KeyboardButton{text: "Отклонить"}], [%Nadia.Model.KeyboardButton{text: "Редактировать"}]]
    [reply_markup: %Nadia.Model.ReplyKeyboardMarkup{keyboard: buttons}]
  end
  defp build_markup(:type_choice) do
    buttons = [[%Nadia.Model.KeyboardButton{text: "Загрузить существо"}, %Nadia.Model.KeyboardButton{text: "Загрузить фразу"}]]
    [reply_markup: %Nadia.Model.ReplyKeyboardMarkup{keyboard: buttons}]
  end
  defp build_markup(nil), do: []
end
