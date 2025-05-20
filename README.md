
# Project-Flow

Project-Flow is a project management service and task tracker.

## Edit .env

Before running, add .env file in root folder with some variables:

```
  JWT_SECRET=*secret key for jwt tokens*
  MAIL_USERNAME=*gmail for smtp messages*
  MAIL_PASSWORD=*gmail application password for smtp*
  CLIENT_URL=*base url of client*
  POSTGRES_DB=*
  POSTGRES_USER=*
  POSTGRES_PASSWORD=*
```
## Run Server

You will need Docker, to start application.
To run server, use this command:

```bash
  docker build -f Dockerfile -t 1dev3pl/project-flow-backend .
  docker compose up
```