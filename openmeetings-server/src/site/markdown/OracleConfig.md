<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->

# Oracle Configuration

## Steps todo

There is a sample configuration for Oracle that ships with every release in:
`${OM_HOME}/webapps/openmeetings/WEB-INF/classes/META-INF/oracle_persistence.xml`

You should modify necessary parameters `Url/Username/Password`

Run OpenMeetings and goto the web-based installer: https://localhost:5443/openmeetings/install

## Steps for Dockerized Oracle XE

NOTE: detailed instruction are here https://github.com/oracle/docker-images/tree/master/OracleDatabase/SingleInstance#running-oracle-database-18c-express-edition-in-a-container

1. Clone this repo `https://github.com/oracle/docker-images/`

2. Go to `docker-images/OracleDatabase/SingleInstance/dockerfiles`

3. run `./buildContainerImage.sh -v 18.4.0 -x`

    - create folder `mkdir ${HOME}/work/tmp/oradata`

    - set rights `chmod -R a+w ${HOME}/work/tmp/oradata`

4. run `docker run -p 1521:1521 -p 5500:5500 -e ORACLE_PWD=openmeetings_test -v ${HOME}/work/tmp/oradata:/opt/oracle/oradata oracle/database:18.4.0-xe`

5. edit `${OM_HOME}/webapps/openmeetings/WEB-INF/classes/META-INF/oracle_persistence.xml` set

    - Url: `jdbc:oracle:thin:@//localhost:1521/XEPDB1`

    - Username: `system`

    - Password: `openmeetings_test`

6. `cp ${OM_HOME}/webapps/openmeetings/WEB-INF/classes/META-INF/oracle_persistence.xml ${OM_HOME}/webapps/openmeetings/WEB-INF/classes/META-INF/persistence.xml`

## Troubleshooting

If you encounter issues, you can drop the db and then run the web based installer again
