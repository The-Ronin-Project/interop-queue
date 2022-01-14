FROM liquibase/liquibase
WORKDIR /liquibase/changelog

# Pull in the full directory structure and files
COPY src/main/resources/ /liquibase/changelog/

ENV CHANGE_LOG_FILE queue.db.changelog-master.yaml

RUN lpm add mysql --global
CMD liquibase --url=${JDBC_URL} --changeLogFile=queue/db/changelog/$CHANGE_LOG_FILE update
