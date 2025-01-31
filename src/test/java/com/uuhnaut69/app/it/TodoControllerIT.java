package com.uuhnaut69.app.it;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.uuhnaut69.app.todo.model.Todo;
import com.uuhnaut69.app.todo.model.dto.TodoRequest;
import com.uuhnaut69.app.todo.repository.TodoRepository;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * @author uuhnaut
 */
@Slf4j
public class TodoControllerIT extends BaseIT {

  private static final int THREAD_COUNT = 150;

  @Autowired
  private TodoRepository todoRepository;

  @Test
  @Order(1)
  @Sql({"/user.sql", "/clean_todo.sql"})
  void createNewTodoSuccess() {
    var todoRequest = new TodoRequest("Test", 1L);

    var response = testRestTemplate.postForEntity("/todos", todoRequest, Todo.class);
    var body = response.getBody();

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Assertions.assertNotNull(body);
    assertEquals("Test", body.getTask());
  }

  @Test
  @Order(2)
  @Sql({"/user.sql", "/clean_todo.sql"})
  void createNewTodoFailedByTaskIsBlank() {
    var todoRequest = new TodoRequest("", 1L);

    var response = testRestTemplate.postForEntity("/todos", todoRequest, Todo.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(3)
  @Sql({"/user.sql", "/clean_todo.sql"})
  void createNewTodoFailedByUserIdIsNull() {
    var todoRequest = new TodoRequest("", null);

    var response = testRestTemplate.postForEntity("/todos", todoRequest, Todo.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(4)
  @Sql({"/user.sql", "/clean_todo.sql"})
  void createNewTodoFailedByTaskIsBlankAndUserIdIsNull() {
    var todoRequest = new TodoRequest("", null);

    var response = testRestTemplate.postForEntity("/todos", todoRequest, Todo.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(5)
  @Sql({"/user.sql", "/dummy_todo.sql"})
  void createNewTodoFailedByReachLimit() {
    var todoRequest = new TodoRequest("Test", 1L);

    var response = testRestTemplate.postForEntity("/todos", todoRequest, Todo.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @Order(6)
  @Sql({"/user.sql", "/clean_todo.sql"})
  void createNewTodoParallelExecution() {
    assertEquals(0L, todoRepository.countNumberOfCreatedTodosTodayByUserId(1L));

    var allFutures = new LinkedList<CompletableFuture<ResponseEntity<Todo>>>();

    IntStream.range(0, THREAD_COUNT)
        .forEach(value -> {
          var todoRequest = new TodoRequest("Test", 1L);
          var response = testRestTemplate.postForEntity("/todos", todoRequest, Todo.class);
          allFutures.add(CompletableFuture.completedFuture(response));
        });

    CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();

    assertEquals(10L, todoRepository.countNumberOfCreatedTodosTodayByUserId(1L));
  }

  @Test
  @Order(7)
  @Sql({"/user.sql", "/dummy_todo.sql"})
  void findAllTodo() {
    var response = testRestTemplate.getForEntity("/todos", Todo[].class);
    var body = response.getBody();

    Assertions.assertNotNull(body);
    Assertions.assertTrue(body.length > 1);
  }

  @Test
  @Order(8)
  @Sql({"/user.sql", "/dummy_todo.sql"})
  void findAllTodoByUserId() {
    var response = testRestTemplate.getForEntity("/todos?userId=1", Todo[].class);
    var body = response.getBody();

    Assertions.assertNotNull(body);
    Assertions.assertTrue(body.length > 1);
  }
}
