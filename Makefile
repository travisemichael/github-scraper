.PHONY: start restart stop drop-db

target/docker/stage/Dockerfile: ./build.sbt ./project/plugins.sbt
	sbt docker:stage

start: target/docker/stage/Dockerfile
	docker-compose -p github-scraper up --build -d

restart:
	docker-compose -p github-scraper restart

stop:
	docker-compose -p github-scraper down

drop-db:
	docker volume rm github-scraper_my-db
