## About The Project

Simple REST API Todo List Application.

List supported features:
- Create new todo
- Get list todo
- Get list user
- Get user detail

## Prerequisites

- `Java 17+`
- `Docker`
- `Docker-compose`

## Get Started

### Setup environment

```shell
docker-compose up -d
```

### Run project

```shell
./mvnw spring-boot:run -DskipTests=true
```

### Sample Curls

**NOTE:**
Created 1 user for testing purposes:

```shell
id: 1, name: 'uuhnaut69', limit_config: 10
```

Create new todo:

```shell
curl -H "Content-Type: application/json" -X POST -d '{"task":"Do assignment","userId": 1}' http://localhost:8080/todos
```

**NOTE:** Full API documentation can visit [here](http://localhost:8080/swagger-ui/index.html#/)

### Testing

For running Unit Tests:

```shell
 ./mvnw test 
```

For running Integration Tests:

```shell
 ./mvnw verify -Pintegration 
```

### Todo

- [ ] Authentication
- [ ] User limit configuration
- [ ] Web Interface