package com.example.todo.infrastructure.web;

import com.example.todo.application.port.in.GetTodoQuery;
import com.example.todo.application.port.in.ManageTodoCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoViewController.class)
public class TodoViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetTodoQuery getTodoQuery;

    @MockBean
    private ManageTodoCommand manageTodoCommand;

    private MockHttpSession session;

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER_NICKNAME = "TestUser";

    @BeforeEach
    public void setUp() {
        // Create a mock session with a logged-in user
        session = new MockHttpSession();
        session.setAttribute("userId", TEST_USER_ID);
        session.setAttribute("userNickname", TEST_USER_NICKNAME);
    }

    @Test
    public void testListTodos() throws Exception {
        // Given
        GetTodoQuery.TodoDto todo = new GetTodoQuery.TodoDto(1L, "Test Todo", "Test Description", false);
        when(getTodoQuery.getTodosByUser(eq(TEST_USER_ID))).thenReturn(Arrays.asList(todo));

        // When & Then
        mockMvc.perform(get("/todos").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("list"))
                .andExpect(model().attributeExists("todos"))
                .andExpect(model().attributeExists("userNickname"))
                .andExpect(model().attribute("userNickname", TEST_USER_NICKNAME))
                .andExpect(model().attribute("todos", Arrays.asList(todo)));
    }

    @Test
    public void testListTodosEmpty() throws Exception {
        // Given
        when(getTodoQuery.getTodosByUser(eq(TEST_USER_ID))).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/todos").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("list"))
                .andExpect(model().attributeExists("todos"))
                .andExpect(model().attributeExists("userNickname"))
                .andExpect(model().attribute("userNickname", TEST_USER_NICKNAME))
                .andExpect(model().attribute("todos", Collections.emptyList()));
    }

    @Test
    public void testShowCreateForm() throws Exception {
        // When & Then
        mockMvc.perform(get("/todos/create").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("create"))
                .andExpect(model().attributeExists("todo"));
    }

    @Test
    public void testCreateTodo() throws Exception {
        // Given
        ManageTodoCommand.TodoDto createdTodo = new ManageTodoCommand.TodoDto(1L, "New Todo", "New Description", false);
        when(manageTodoCommand.createTodo(any(ManageTodoCommand.CreateTodoCommand.class))).thenReturn(createdTodo);

        // When & Then
        mockMvc.perform(post("/todos")
                .session(session)
                .param("title", "New Todo")
                .param("description", "New Description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"));

        // Verify that createTodo was called with a command that includes the user ID
        verify(manageTodoCommand).createTodo(argThat(command -> 
            command.getTitle().equals("New Todo") &&
            command.getDescription().equals("New Description") &&
            command.getUserId().equals(TEST_USER_ID)
        ));
    }

    @Test
    public void testToggleTodoStatus_WhenComplete() throws Exception {
        // Given
        GetTodoQuery.TodoDto todo = new GetTodoQuery.TodoDto(1L, "Test Todo", "Test Description", true);
        when(getTodoQuery.getTodoById(1L)).thenReturn(todo);

        // When & Then
        mockMvc.perform(post("/todos/1/toggle").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"));

        verify(manageTodoCommand).uncompleteTodo(1L);
    }

    @Test
    public void testToggleTodoStatus_WhenIncomplete() throws Exception {
        // Given
        GetTodoQuery.TodoDto todo = new GetTodoQuery.TodoDto(1L, "Test Todo", "Test Description", false);
        when(getTodoQuery.getTodoById(1L)).thenReturn(todo);

        // When & Then
        mockMvc.perform(post("/todos/1/toggle").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"));

        verify(manageTodoCommand).completeTodo(1L);
    }
}
