defmodule Orion.Nikki do

  @token "trust-me-im-orion"

  def get_unapproved() do
    case HTTPoison.get("localhost:9000/bot/unapproved", [{"Token", @token}]) do
      {:ok, %HTTPoison.Response{status_code: 200, body: body}} ->
        body
      {:ok, %HTTPoison.Response{status_code: 404}} ->
        nil
      e ->
        IO.inspect e
        nil
    end
  end

  def post_approved(data) do
    HTTPoison.post "localhost:9000/bot/approve", Poison.encode!(data), 
      [{"Content-Type", "application/json"}, {"Token", @token}]
  end
end
