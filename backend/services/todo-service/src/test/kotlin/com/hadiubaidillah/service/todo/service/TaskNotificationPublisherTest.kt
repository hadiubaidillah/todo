package com.hadiubaidillah.service.todo.service

import com.hadiubaidillah.service.todo.entity.Task
import com.hadiubaidillah.service.todo.entity.TaskAuthor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.test.util.ReflectionTestUtils
import java.time.OffsetDateTime
import java.util.UUID
import java.util.function.Function
import java.util.function.Supplier
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class TaskNotificationPublisherTest {

    @Mock
    private lateinit var rabbitTemplate: RabbitTemplate

    @Mock
    private lateinit var circuitBreakerFactory: CircuitBreakerFactory<*, *>

    @Captor
    private lateinit var exchangeCaptor: ArgumentCaptor<String>

    @Captor
    private lateinit var routingKeyCaptor: ArgumentCaptor<String>

    @Captor
    private lateinit var messageCaptor: ArgumentCaptor<Any>

    private lateinit var publisher: TaskNotificationPublisher

    private val exchange = "platform.notifications"
    private val eventsRoutingKey = "notification.event"
    private val cancelRoutingKey = "notification.cancel"

    private val authorId = UUID.randomUUID()
    private val taskId = UUID.randomUUID()
    private val author = TaskAuthor(
        id = authorId,
        email = "hadi@example.com",
        firstName = "Hadi",
        lastName = "Ubaidillah"
    )

    /** CircuitBreaker that directly executes the supplier */
    private val directCircuitBreaker = object : CircuitBreaker {
        override fun <T> run(toRun: Supplier<T>): T = toRun.get()
        override fun <T> run(toRun: Supplier<T>, fallback: Function<Throwable?, T>): T = toRun.get()
    }

    /** CircuitBreaker that always triggers the fallback */
    private val fallbackCircuitBreaker = object : CircuitBreaker {
        override fun <T> run(toRun: Supplier<T>): T = throw RuntimeException("RabbitMQ connection refused")
        override fun <T> run(toRun: Supplier<T>, fallback: Function<Throwable?, T>): T {
            return fallback.apply(RuntimeException("RabbitMQ connection refused"))
        }
    }

    @BeforeEach
    fun setUp() {
        publisher = TaskNotificationPublisher(rabbitTemplate, circuitBreakerFactory)
        ReflectionTestUtils.setField(publisher, "exchange", exchange)
        ReflectionTestUtils.setField(publisher, "notificationEventsRoutingKey", eventsRoutingKey)
        ReflectionTestUtils.setField(publisher, "notificationCancelRoutingKey", cancelRoutingKey)

        org.mockito.Mockito.lenient().`when`(circuitBreakerFactory.create(any())).thenReturn(directCircuitBreaker)
    }

    // =========================================================================
    // taskCreated() tests
    // =========================================================================

    @Test
    fun `taskCreated publishes CREATION event to notification events routing key`() {
        val task = Task(id = taskId, name = "Belajar Kotlin", author = author)

        publisher.taskCreated(task, author)

        verify(rabbitTemplate, times(1)).convertAndSend(
            exchangeCaptor.capture(),
            routingKeyCaptor.capture(),
            messageCaptor.capture()
        )

        assertEquals(exchange, exchangeCaptor.value)
        assertEquals(eventsRoutingKey, routingKeyCaptor.value)

        @Suppress("UNCHECKED_CAST")
        val event = messageCaptor.value as Map<String, Any?>
        assertEquals(authorId, event["userId"])
        assertEquals("todo-service", event["sourceService"])
        assertEquals(taskId.toString(), event["sourceId"])
        assertEquals("CREATION", event["type"])
        assertEquals("Task Created: Belajar Kotlin", event["title"])
        assertTrue((event["message"] as String).contains("Belajar Kotlin"))
        assertEquals(false, event["scheduleEmail"])
        assertNull(event["emailDeliveryAt"])
        assertEquals("hadi@example.com", event["userEmail"])
        assertEquals("Hadi Ubaidillah", event["userName"])
    }

    @Test
    fun `taskCreated with endsIn schedules email`() {
        val dueDate = OffsetDateTime.now().plusDays(3)
        val task = Task(id = taskId, name = "Task Deadline", endsIn = dueDate, author = author)

        publisher.taskCreated(task, author)

        verify(rabbitTemplate).convertAndSend(
            eq(exchange), eq(eventsRoutingKey), messageCaptor.capture()
        )

        @Suppress("UNCHECKED_CAST")
        val event = messageCaptor.value as Map<String, Any?>
        assertEquals(true, event["scheduleEmail"])
        assertEquals(dueDate, event["emailDeliveryAt"])
    }

    // =========================================================================
    // taskUpdated() tests
    // =========================================================================

    @Test
    fun `taskUpdated publishes UPDATE event without cancel`() {
        val task = Task(id = taskId, name = "Updated Task", author = author)

        publisher.taskUpdated(task, author)

        // Should send only 1 message: update event (no cancel to avoid race condition)
        verify(rabbitTemplate, times(1)).convertAndSend(
            exchangeCaptor.capture(),
            routingKeyCaptor.capture(),
            messageCaptor.capture()
        )

        assertEquals(exchange, exchangeCaptor.value)
        assertEquals(eventsRoutingKey, routingKeyCaptor.value)

        @Suppress("UNCHECKED_CAST")
        val updateEvent = messageCaptor.value as Map<String, Any?>
        assertEquals(authorId, updateEvent["userId"])
        assertEquals("todo-service", updateEvent["sourceService"])
        assertEquals(taskId.toString(), updateEvent["sourceId"])
        assertEquals("UPDATE", updateEvent["type"])
        assertEquals("Task Updated: Updated Task", updateEvent["title"])
        assertTrue((updateEvent["message"] as String).contains("Updated Task"))
    }

    @Test
    fun `taskUpdated does not send cancel event`() {
        val task = Task(id = taskId, name = "Updated Task", author = author)

        publisher.taskUpdated(task, author)

        // Verify cancel routing key is never used
        verify(rabbitTemplate, never()).convertAndSend(eq(exchange), eq(cancelRoutingKey), any<Any>())
    }

    @Test
    fun `taskUpdated with endsIn schedules email`() {
        val newDueDate = OffsetDateTime.now().plusDays(5)
        val task = Task(id = taskId, name = "Rescheduled Task", endsIn = newDueDate, author = author)

        publisher.taskUpdated(task, author)

        verify(rabbitTemplate).convertAndSend(
            any<String>(), any<String>(), messageCaptor.capture()
        )

        @Suppress("UNCHECKED_CAST")
        val updateEvent = messageCaptor.value as Map<String, Any?>
        assertEquals(true, updateEvent["scheduleEmail"])
        assertEquals(newDueDate, updateEvent["emailDeliveryAt"])
    }

    @Test
    fun `taskUpdated without endsIn does not schedule email`() {
        val task = Task(id = taskId, name = "No Deadline Task", endsIn = null, author = author)

        publisher.taskUpdated(task, author)

        verify(rabbitTemplate).convertAndSend(
            any<String>(), any<String>(), messageCaptor.capture()
        )

        @Suppress("UNCHECKED_CAST")
        val updateEvent = messageCaptor.value as Map<String, Any?>
        assertEquals(false, updateEvent["scheduleEmail"])
        assertNull(updateEvent["emailDeliveryAt"])
    }

    // =========================================================================
    // taskDeleted() tests
    // =========================================================================

    @Test
    fun `taskDeleted publishes DELETION event`() {
        val task = Task(id = taskId, name = "To Delete", author = author)

        publisher.taskDeleted(task, author)

        verify(rabbitTemplate, times(1)).convertAndSend(
            exchangeCaptor.capture(),
            routingKeyCaptor.capture(),
            messageCaptor.capture()
        )

        assertEquals(exchange, exchangeCaptor.value)
        assertEquals(eventsRoutingKey, routingKeyCaptor.value)

        @Suppress("UNCHECKED_CAST")
        val deletionEvent = messageCaptor.value as Map<String, Any?>
        assertEquals(authorId, deletionEvent["userId"])
        assertEquals("todo-service", deletionEvent["sourceService"])
        assertEquals(taskId.toString(), deletionEvent["sourceId"])
        assertEquals("DELETION", deletionEvent["type"])
        assertEquals("Task Deleted: To Delete", deletionEvent["title"])
    }

    // =========================================================================
    // Circuit breaker / error handling tests
    // =========================================================================

    @Test
    fun `taskUpdated does not throw when circuit breaker fallback is triggered`() {
        whenever(circuitBreakerFactory.create(any())).thenReturn(fallbackCircuitBreaker)

        val task = Task(id = taskId, name = "Failing Task", author = author)

        // Should not throw — fallback logs the error
        publisher.taskUpdated(task, author)

        // RabbitTemplate should NOT be called since circuit breaker went to fallback
        verify(rabbitTemplate, never()).convertAndSend(any<String>(), any<String>(), any<Any>())
    }
}
