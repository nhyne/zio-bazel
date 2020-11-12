package dev.nhyne

import dev.nhyne.todo.persistence.NotePersistenceService.NotePersistence
import dev.nhyne.todo.persistence.TodoItemPersistenceService.TaskPersistence
import dev.nhyne.todo.persistence.TodoListPersistenceService.TodoPersistence

package todo {}

package object todo {
  type Env = TaskPersistence with TodoPersistence with NotePersistence
}
