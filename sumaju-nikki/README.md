# sumaju-nikki

_Lasciate ogni speranza, voi ch'entrate_

## Prerequisites

We assume you're running a Linux OS, although WSL may do as well.

* [PostgreSQL 9.6+](https://www.postgresql.org/download/linux)
* [sbt](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html)

## Setting up the environment

Create a database user `nikki` with a password `nikki`:

```bash
psql -U postgres -c "CREATE ROLE nikki WITH LOGIN PASSWORD 'nikki' CREATEDB;"
```

## Running the app

```bash
sbt run
```
