defmodule Seeder.Repo do
  use Ecto.Repo,
    otp_app: :seeder,
    adapter: Ecto.Adapters.Postgres
end
