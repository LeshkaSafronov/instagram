FROM openjdk:8
WORKDIR /usr/src/web

RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ stretch-pgdg main" > /etc/apt/sources.list.d/pgdg.list
RUN wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - &&     apt-get update && apt-get -y install postgresql-10

COPY build/libs/web.jar /usr/src/web
ADD wait-for-postgres.sh /usr/src/web/wait-for-postgres.sh