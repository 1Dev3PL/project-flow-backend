
# Project-Flow

Project-Flow is a project management service and task tracker.

## Edit .env

Before running, add .env file in root folder with JWT_SECRET variable:

```
  JWT_SECRET=add_your_secret_here
```
## Run Server

You will need Maven, to start application.
To run server, use this commands:

```bash
  mvn install
  docker-compose up
  mvn spring-boot:run
```