FROM openjdk:8
WORKDIR /usr/src/web

RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ stretch-pgdg main" > /etc/apt/sources.list.d/pgdg.list
RUN wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - &&     apt-get update && apt-get -y install postgresql-10

RUN apt install -y python3-pip
ADD requirements.txt /usr/src/web/requirements.txt
RUN pip3 install -r requirements.txt

ADD build/libs/web.jar /usr/src/web
ADD wait-for-postgres.sh /usr/src/web/wait-for-postgres.sh
ADD wait-for-minio.py /usr/src/web/wait-for-minio.py