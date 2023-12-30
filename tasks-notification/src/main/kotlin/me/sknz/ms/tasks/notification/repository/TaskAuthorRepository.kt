package me.sknz.ms.tasks.notification.repository;

import me.sknz.ms.tasks.notification.entity.TaskAuthor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TaskAuthorRepository : JpaRepository<TaskAuthor, UUID>