# PREREQUISITES
- Java
- Docker | https://docs.docker.com/engine/install/ubuntu/ 

# DOCKER
- RUNNING POSTGRES
- create:
docker run --name postgres -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=userDB -d postgres:alpine
- stop:
docker stop postgres
- start:
docker start postgres