package com.hadiubaidillah.service.todo.repository

import com.hadiubaidillah.service.todo.entity.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
interface TaskRepository : JpaRepository<Task, UUID> {
    fun findByAuthorIdOrderByCreatedAtDesc(authorId: UUID): List<Task>

    @Query("""
        SELECT t FROM Task t
        JOIN FETCH t.author
        WHERE t.completed = false
        AND t.overdueNotified = false
        AND t.endsIn IS NOT NULL
        AND t.endsIn < :now
    """)
    fun findTasksNeedingOverdueNotification(now: OffsetDateTime): List<Task>
}
