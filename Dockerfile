FROM hseeberger/scala-sbt:8u212_1.2.8_2.12.8
RUN mkdir -p /app
COPY ./ /app
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["sbt", "run"]