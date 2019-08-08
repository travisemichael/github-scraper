# github-scraper

A dockerized HTTP server for scraping user and repository information from GitHub.

## Setup

- Docker
  - [Install Docker](https://docs.docker.com/install/)
  - OR brew users: run `brew cask install docker`
- sbt
  - [Install sbt](https://www.scala-sbt.org/release/docs/Setup.html)
  - OR brew users: run `brew install sbt`
- Environment variables
  - In order to avoid being rate limited by GitHub's API, the app authenticates using username/token or username/password
  - The access token does not need any additional permissions
  - [Create an access token](https://help.github.com/en/articles/creating-a-personal-access-token-for-the-command-line)
  - run `export GITHUB_USERNAME={your_username}`
  - run `export GITHUB_TOKEN={your_token}`
  - OR (not recommended) run `export GITHUB_PASSWORD={your_password}`
  
## Getting started

```bash
$ make start
```
and you're up and running on http://localhost:8181!

## Make recipes

- `make start` - Start the server
- `make stop` - Stop the server
- `make restart` - Restart the server
- `make drop-db` - Drop the volume holding the database data

## Configuration

### CRON
The server is designed to run Scraping jobs on a cron schedule. The schedule can be modified in [scrape.cron](scrape.cron). A server restart is required to pick up the changes.

### Job configuration
The scraping jobs are configured via 2 json files: [users.json](users.json) and [repos.json](repos.json).

Example users.json configuration
```json
{
  "start": 0,
  "count": 100,
  "additional_users": [
    "foo",
    "bar"
  ]
}
```

This file tells the GitHub User Scraping Job to start at the first ID after 0 and query 100 users. In a perfect world this would be the users with ids 1-100, but some users have been deleted and there are gaps in the IDs.

It will also fetch additional users by username as specified by `additional_users`.

## Rest API

The server has a REST API that allows the querying of scraped data. It will also allow the user to synchronously scrape more data.

### Users API

#### __Get Users__: `GET /users`

Get all persisted user information

__URL__: http://localhost:8181/users

__Code__: `200 OK`

__Response__:

```json
[  
    {
        "login": "travisemichael",
        "id": 7723569,
        "type": "User",
        "name": "Travis E Michael",
        "company": "Caffeine",
        "blog": "",
        "location": "Redwood City, CA",
        "email": "travisemichael@gmail.com",
        "publicRepos": 6,
        "publicGists": 0,
        "followers": 1,
        "following": 0,
        "createdAt": "2014-05-28T12:16:54Z",
        "updatedAt": "2019-08-08T11:35:58Z"
    },
    {
        "login": "Tesorio",
        "id": 8165102,
        "type": "Organization",
        "name": "Tesorio",
        "blog": "https://www.tesorio.com/",
        "location": "San Francisco Bay Area, CA",
        "email": "hello@tesorio.com",
        "publicRepos": 13,
        "publicGists": 0,
        "followers": 0,
        "following": 0,
        "createdAt": "2014-07-15T04:09:06Z",
        "updatedAt": "2018-12-11T19:22:06Z"
    } 
]
```

#### __Get User__: `GET /users/:id`

Get persisted user information by user `id`

__URL__: http://localhost:8181/users/7723569

__Code__: `200 OK`

__Response__:

```json
{
    "login": "travisemichael",
    "id": 7723569,
    "type": "User",
    "name": "Travis E Michael",
    "company": "Caffeine",
    "blog": "",
    "location": "Redwood City, CA",
    "email": "travisemichael@gmail.com",
    "publicRepos": 6,
    "publicGists": 0,
    "followers": 1,
    "following": 0,
    "createdAt": "2014-05-28T12:16:54Z",
    "updatedAt": "2019-08-08T11:35:58Z"
}
```

#### __Get User__: `GET /users/:name`

Get persisted user information by user `name`

__URL__: http://localhost:8181/users/travisemichael

__Code__: `200 OK`

__Response__:

```json
{
    "login": "travisemichael",
    "id": 7723569,
    "type": "User",
    "name": "Travis E Michael",
    "company": "Caffeine",
    "blog": "",
    "location": "Redwood City, CA",
    "email": "travisemichael@gmail.com",
    "publicRepos": 6,
    "publicGists": 0,
    "followers": 1,
    "following": 0,
    "createdAt": "2014-05-28T12:16:54Z",
    "updatedAt": "2019-08-08T11:35:58Z"
}
```

#### __Scrape Users__ `POST /users`

Scrape and persist user information from GitHub, starting with the first user with a valid ID greater than `start` and continuing until `count` repos have been scraped.

__URL__: http://localhost:8181/users?start=0&count=10

__Code__: `200 OK`

#### __Scrape User__: `POST /users/:name`

Scrape and persist user information from GitHub by user `name`

__URL__: http://localhost:8181/users/travisemichael

__Code__: `200 OK`

__Response__:
```json
{
    "login": "travisemichael",
    "id": 7723569,
    "type": "User",
    "name": "Travis E Michael",
    "company": "Caffeine",
    "blog": "",
    "location": "Redwood City, CA",
    "email": "travisemichael@gmail.com",
    "publicRepos": 6,
    "publicGists": 0,
    "followers": 1,
    "following": 0,
    "createdAt": "2014-05-28T12:16:54Z",
    "updatedAt": "2019-08-08T11:35:58Z"
}
```

### Repos API

#### __Get Repos__: `GET /repos`

Get all persisted repo information

__URL__: http://localhost:8181/repos

__Code__: `200 OK`

__Response__:
```json
[
    {
        "id": 87983904,
        "name": "django-saml2-auth",
        "fullName": "Tesorio/django-saml2-auth",
        "ownerId": 8165102,
        "htmlUrl": "https://github.com/Tesorio/django-saml2-auth",
        "description": "Django SAML2 Authentication Made Easy. Easily integrate with SAML2 SSO identity providers like Okta",
        "fork": true
    },
    {
        "id": 124510032,
        "name": "charts",
        "fullName": "google/charts",
        "ownerId": 1342004,
        "htmlUrl": "https://github.com/google/charts",
        "fork": false
    }
]
```

#### __Get Repo__: `GET /repos/:id`

Get persisted repo information using the repo `id`

__URL__: http://localhost:8181/repos/87983904

__Code__: `200 OK`

__Response__:
```json
{
    "id": 87983904,
    "name": "django-saml2-auth",
    "fullName": "Tesorio/django-saml2-auth",
    "ownerId": 8165102,
    "htmlUrl": "https://github.com/Tesorio/django-saml2-auth",
    "description": "Django SAML2 Authentication Made Easy. Easily integrate with SAML2 SSO identity providers like Okta",
    "fork": true
}
```

#### __Get Repo__: `GET /repos/:owner/:name`

Get persisted repo information using the repo's `owner` name and the repo `name`

__URL__: http://localhost:8181/repos/tesorio/django-saml2-auth

__Code__: `200 OK`

__Response__:
```json
{
    "id": 87983904,
    "name": "django-saml2-auth",
    "fullName": "Tesorio/django-saml2-auth",
    "ownerId": 8165102,
    "htmlUrl": "https://github.com/Tesorio/django-saml2-auth",
    "description": "Django SAML2 Authentication Made Easy. Easily integrate with SAML2 SSO identity providers like Okta",
    "fork": true
}
```

#### __Scrape Repos__: `POST /repos`

Scrape and persist repo information from GitHub, starting with the first repo with a valid ID greater than `start` and continuing until `count` repos have been scraped.

__URL__: http://localhost:8181/repos?start=0&count=10

__Code__: `200 OK`

#### __Scrape Repo__: `POST /repos/:owner/:name`

Scrape and persist repo information from GitHub by repo's `owner` name and repo `name`

__URL__: http://localhost:8181/repos/tesorio/django-saml2-auth

__Code__: `200 OK`

__Response__:
```json
{
    "id": 87983904,
    "name": "django-saml2-auth",
    "fullName": "Tesorio/django-saml2-auth",
    "ownerId": 8165102,
    "htmlUrl": "https://github.com/Tesorio/django-saml2-auth",
    "description": "Django SAML2 Authentication Made Easy. Easily integrate with SAML2 SSO identity providers like Okta",
    "fork": true
}
```
