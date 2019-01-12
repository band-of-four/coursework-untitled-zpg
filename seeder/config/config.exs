use Mix.Config

config :seeder, Seeder.Repo,
  database: "postgres",
  username: "nikki",
  password: "nikki",
  hostname: "localhost"

config :seeder, ecto_repos: [Seeder.Repo]
