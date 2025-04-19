
# Project-Flow

Project-Flow is a project management service and task tracker.

## Edit .env

Before running, add .env file in root folder with some variables:

```
  JWT_SECRET=*secret key for jwt tokens*
  MAIL_USERNAME=*gmail for smtp messages*
  MAIL_PASSWORD=*gmail application password for smtp*
```
## Run Server

You will need Maven and Docker, to start application.
To run server, use this commands:

```bash
  mvn install
  docker-compose up
  mvn spring-boot:run
```