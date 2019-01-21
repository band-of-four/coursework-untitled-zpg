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

## Deployment

Build a packaged *.tgz* version:

```bash
sbt universal:packageZipTarball
```

Copy the package over to the target server and untar it:

```bash
scp target/universal/sumaju-nikki-1.0.0.tgz helios:~
tar xf sumaju-nikki-1.0.0.tgz
cd sumaju-nikki-1.0.0
```

Create a production config at *conf/application.prod.conf*.
Here's a minimal example:

```
include "application.conf"

http.port = 9090
db.default.url = "postgres://username:password@pg/studs"
play.http.secret.key = "change-to-secret-production-key"
```

Start the application:

```bash
bin/sumaju-nikki -no-version-check -Dconfig.resource=application.prod.conf
```

The runner may complain about missing database evolutions — run them with
```bash
bin/sumaju-nikki -no-version-check -Dconfig.resource=application.prod.conf -Dplay.evolutions.db.default.autoApply=true
```
