package me.sknz.ms.tasks.repository;

import me.sknz.ms.tasks.entity.TaskAuthor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TaskAuthorRepository : JpaRepository<TaskAuthor, UUID>