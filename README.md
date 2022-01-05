[![Lint](https://github.com/projectronin/interop-queue/actions/workflows/lint.yml/badge.svg)](https://github.com/projectronin/interop-queue/actions/workflows/lint.yml)

# interop-queue-build

Multi-project build containing components related to InterOps Queue services.

### Components

* [interop-queue](interop-queue) - Provides API abstraction and definition of the InterOps queue system.
* [interop-queue-liquibase](interop-queue-liquibase) - Provides Liquibase changelogs defining the InterOps Queue
  database.
* [interop-queue-db](interop-queue-db) - Implementation of the InterOps queueing system utilizing a MySQL database.
