package com.hadiubaidillah.service.todo.repository

import com.hadiubaidillah.service.todo.entity.TaskAuthor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TaskAuthorRepository : JpaRepository<TaskAuthor, UUID>
