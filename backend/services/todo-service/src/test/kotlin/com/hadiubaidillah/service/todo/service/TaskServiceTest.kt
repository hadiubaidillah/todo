package com.hadiubaidillah.service.todo.service

import com.hadiubaidillah.service.todo.entity.Task
import com.hadiubaidillah.service.todo.entity.TaskAuthor
import com.hadiubaidillah.service.todo.model.TaskDTO
import com.hadiubaidillah.service.todo.repository.TaskAuthorRepository
import com.hadiubaidillah.service.todo.repository.TaskRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant
import java.time.OffsetDateTime
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class TaskServiceTest {

    @Mock
    private lateinit var taskRepository: TaskRepository

    @Mock
    private lateinit var taskAuthorRepository: TaskAuthorRepository

    @Mock
    private lateinit var notificationPublisher: TaskNotificationPublisher

    private lateinit var taskService: TaskService

    private val userId = UUID.randomUUID()
    private val taskId = UUID.randomUUID()

    private val author = TaskAuthor(
        id = userId,
        email = "hadi@example.com",
        firstName = "Hadi",
        lastName = "Ubaidillah"
    )

    private lateinit var jwt: Jwt

    @BeforeEach
    fun setUp() {
        taskService = TaskService(taskRepository, taskAuthorRepository, notificationPublisher)

        jwt = Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .subject(userId.toString())
            .claim("email", "hadi@example.com")
            .claim("given_name", "Hadi")
            .claim("family_name", "Ubaidillah")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build()
    }

    // =========================================================================
    // createTask() - notification tests
    // =========================================================================

    @Test
    fun `createTask publishes taskCreated notification`() {
        whenever(taskAuthorRepository.findById(userId)).thenReturn(Optional.of(author))
        whenever(taskRepository.save(any<Task>())).thenAnswer { it.getArgument(0) }

        val dto = TaskDTO(name = "New Task", description = "A test task")
        val result = taskService.createTask(jwt, dto)

        assertNotNull(result)
        assertEquals("New Task", result.name)
        verify(notificationPublisher).taskCreated(any(), eq(author))
    }

    // =========================================================================
    // updateTask() - notification tests
    // =========================================================================

    @Test
    fun `updateTask publishes taskUpdated notification`() {
        val existingTask = Task(
            id = taskId,
            name = "Old Name",
            description = "Old desc",
            author = author
        )

        whenever(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask))
        whenever(taskAuthorRepository.findById(userId)).thenReturn(Optional.of(author))
        whenever(taskRepository.save(any<Task>())).thenAnswer { it.getArgument(0) }

        val dto = TaskDTO(name = "New Name", description = "New desc")
        val result = taskService.updateTask(jwt, taskId, dto)

        assertNotNull(result)
        assertEquals("New Name", result.name)
        assertEquals("New desc", result.description)

        // Verify taskUpdated was called with updated task and original author
        verify(notificationPublisher).taskUpdated(
            argThat { name == "New Name" && description == "New desc" },
            eq(author)
        )
    }

    @Test
    fun `updateTask with new deadline publishes notification with endsIn`() {
        val newDeadline = OffsetDateTime.now().plusDays(7)
        val existingTask = Task(id = taskId, name = "Task", author = author)

        whenever(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask))
        whenever(taskAuthorRepository.findById(userId)).thenReturn(Optional.of(author))
        whenever(taskRepository.save(any<Task>())).thenAnswer { it.getArgument(0) }

        val dto = TaskDTO(name = "Task", endsIn = newDeadline)
        taskService.updateTask(jwt, taskId, dto)

        verify(notificationPublisher).taskUpdated(
            argThat { endsIn == newDeadline },
            eq(author)
        )
    }

    @Test
    fun `updateTask publishes taskExtendedFromOverdue when extending overdue task`() {
        val pastDeadline = OffsetDateTime.now().minusDays(1)
        val newDeadline = OffsetDateTime.now().plusDays(7)
        val overdueTask = Task(
            id = taskId,
            name = "Overdue Task",
            endsIn = pastDeadline,
            completed = false,
            author = author
        )

        whenever(taskRepository.findById(taskId)).thenReturn(Optional.of(overdueTask))
        whenever(taskAuthorRepository.findById(userId)).thenReturn(Optional.of(author))
        whenever(taskRepository.save(any<Task>())).thenAnswer { it.getArgument(0) }

        val dto = TaskDTO(name = "Overdue Task", endsIn = newDeadline)
        val result = taskService.updateTask(jwt, taskId, dto)

        assertNotNull(result)
        verify(notificationPublisher).taskUpdated(any(), eq(author))
        verify(notificationPublisher).taskExtendedFromOverdue(any(), eq(author))
    }

    @Test
    fun `updateTask returns null and does NOT notify when task not found`() {
        whenever(taskRepository.findById(taskId)).thenReturn(Optional.empty())

        val dto = TaskDTO(name = "Irrelevant")
        val result = taskService.updateTask(jwt, taskId, dto)

        assertNull(result)
        verify(notificationPublisher, never()).taskUpdated(any(), any())
    }

    @Test
    fun `updateTask returns null and does NOT notify when user is not the author`() {
        val otherUserId = UUID.randomUUID()
        val otherAuthor = TaskAuthor(id = otherUserId, email = "other@example.com")
        val existingTask = Task(id = taskId, name = "Task", author = otherAuthor)

        whenever(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask))

        val dto = TaskDTO(name = "Hacked Name")
        val result = taskService.updateTask(jwt, taskId, dto)

        assertNull(result)
        verify(notificationPublisher, never()).taskUpdated(any(), any())
    }

    // =========================================================================
    // deleteTask() - notification tests
    // =========================================================================

    @Test
    fun `deleteTask publishes taskDeleted notification`() {
        val existingTask = Task(id = taskId, name = "To Delete", author = author)

        whenever(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask))

        val result = taskService.deleteTask(jwt, taskId)

        assertTrue(result)
        verify(notificationPublisher).taskDeleted(existingTask, author)
    }

    @Test
    fun `deleteTask returns false and does NOT notify when task not found`() {
        whenever(taskRepository.findById(taskId)).thenReturn(Optional.empty())

        val result = taskService.deleteTask(jwt, taskId)

        assertTrue(!result)
        verify(notificationPublisher, never()).taskDeleted(any(), any())
    }

    // =========================================================================
    // toggleComplete() - notification tests
    // =========================================================================

    @Test
    fun `toggleComplete publishes taskCompleted notification when completing in-progress task`() {
        val futureDeadline = OffsetDateTime.now().plusDays(7)
        val existingTask = Task(
            id = taskId,
            name = "In Progress Task",
            completed = false,
            endsIn = futureDeadline,
            author = author
        )

        whenever(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask))
        whenever(taskAuthorRepository.findById(userId)).thenReturn(Optional.of(author))
        whenever(taskRepository.save(any<Task>())).thenAnswer { it.getArgument(0) }

        val result = taskService.toggleComplete(jwt, taskId)

        assertNotNull(result)
        assertTrue(result.completed)
        verify(notificationPublisher).taskCompleted(any(), eq(author), eq(false))
    }

    @Test
    fun `toggleComplete publishes taskCompleted notification with wasOverdue=true when completing overdue task`() {
        val pastDeadline = OffsetDateTime.now().minusDays(1)
        val overdueTask = Task(
            id = taskId,
            name = "Overdue Task",
            completed = false,
            endsIn = pastDeadline,
            author = author
        )

        whenever(taskRepository.findById(taskId)).thenReturn(Optional.of(overdueTask))
        whenever(taskAuthorRepository.findById(userId)).thenReturn(Optional.of(author))
        whenever(taskRepository.save(any<Task>())).thenAnswer { it.getArgument(0) }

        val result = taskService.toggleComplete(jwt, taskId)

        assertNotNull(result)
        assertTrue(result.completed)
        verify(notificationPublisher).taskCompleted(any(), eq(author), eq(true))
    }

    @Test
    fun `toggleComplete does NOT publish notification when reopening completed task`() {
        val completedTask = Task(
            id = taskId,
            name = "Completed Task",
            completed = true,
            author = author
        )

        whenever(taskRepository.findById(taskId)).thenReturn(Optional.of(completedTask))
        whenever(taskRepository.save(any<Task>())).thenAnswer { it.getArgument(0) }

        val result = taskService.toggleComplete(jwt, taskId)

        assertNotNull(result)
        assertTrue(!result.completed)
        verify(notificationPublisher, never()).taskCompleted(any(), any(), any())
    }
}
