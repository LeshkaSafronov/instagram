FROM python:3.6
WORKDIR /usr/src/style-transfer

RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ stretch-pgdg main" > /etc/apt/sources.list.d/pgdg.list
RUN wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - &&     apt-get update && apt-get -y install postgresql-10

ADD requirements.txt /usr/src/style-transfer/requirements.txt
RUN pip3 install -r /usr/src/style-transfer/requirements.txt

ADD server /usr/src/style-transfer/
ADD wait-for-minio.py /usr/src/style-transfer/wait-for-minio.py
ADD wait-for-postgres.sh /usr/src/style-transfer/wait-for-postgres.sh
