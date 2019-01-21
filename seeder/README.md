# Seeder

Prepare a binary release (requires Erlang on the target machine):

```bash
MIX_ENV=prod mix escript.build
```

Run with a custom database URL:

```bash
DATABASE_URL="postgres://username:password@pg/studs" ./seeder
```
