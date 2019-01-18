defmodule Orion.MixProject do
  use Mix.Project

  def project do
    [
      app: :orion,
      version: "0.1.0",
      elixir: "~> 1.7",
      start_permanent: Mix.env() == :prod,
      deps: deps()
    ]
  end

  # Run "mix help compile.app" to learn about applications.
  def application do
    [
      applications: [:nadia],
      extra_applications: [:logger],
      mod: {Orion.Application, []}
    ]
  end

  # Run "mix help deps" to learn about dependencies.
  defp deps do
    [
      {:poison, "~> 3.1"},
      {:nadia, "~> 0.4.4"}
    ]
  end
end
