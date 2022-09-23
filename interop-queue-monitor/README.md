[![codecov](https://codecov.io/gh/projectronin/interop-queue/branch/master/graph/badge.svg?token=n4Puc73JLa&flag=api)](https://app.codecov.io/gh/projectronin/interop-queue/branch/master)
[![Tests](https://github.com/projectronin/interop-queue/actions/workflows/api_test.yml/badge.svg)](https://github.com/projectronin/interop-queue/actions/workflows/api_test.yml)
[![Lint](https://github.com/projectronin/interop-queue/actions/workflows/lint.yml/badge.svg)](https://github.com/projectronin/interop-queue/actions/workflows/lint.yml)

# interop-queue-monitor

Implements a queue monitor that should be deployed to our persistent environments and will monitor queue health and report metrics to DataDog.

### Metrics
The metrics reported can be found in the DataDog metrics explorer and will generally be of the format  
`interop_queue.tenant.queue-type.metric-reported.metric-type`  
tenant = the current tenant, but will be omitted if the metric is a total across all tenants  
queue-type = currently api or hl7  
metric-reported = currently age or depth  
metric-type = currently only gauge 

For example, the current age of the oldest record in the api queue for PSJ will be a metric named `interop_queue.psj.api.age.gauge`.  The current depth of the hl7 queue across all tenants will be `interop_queue.hl7.depth.gauge`.

### Environment
Requires the following values to be set, either as env vars or in an application.properties file
`queue.monitor.threshold`: The depth beneath which queue stats will be ignored (Defaults to 1).  
`queue.monitor.rate`: Number of milliseconds to wait from the start of one status check to the next (Defaults to 10000).  
`SPRING_QUEUE_DATASOURCE_JDBCURL`: JDBC url where the queue DB can be found.
`DD_AGENT_HOST`: Host the DataDog agent is running on (Defaults to localhost).  
`DD_AGENT_PORT`: Port the DataDog agent is running on (Defaults to 8125).

### Running Locally
To run locally and try things out for yourself, run docker compose with
```shell
./gradlew clean bootJar && docker compose build --no-cache && docker compose up --force-recreate
```
Once everything is up and running, you can connect to the queue DB on port 3306, insert records and watch metrics start appearing in DataDog.
