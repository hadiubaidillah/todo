package me.sknz.ms.tasks.notification.repository;

import me.sknz.ms.tasks.notification.entity.Task
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TaskRepository : JpaRepository<Task, UUID>