defmodule Seeder.Repo do
  use Ecto.Repo,
    otp_app: :seeder,
    adapter: Ecto.Adapters.Postgres

  def init(_type, config) do
    url = System.get_env("DATABASE_URL")
    if url do
      {:ok, [url: url] ++ config}
    else
      {:ok, config}
    end
  end
end
