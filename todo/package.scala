package dev.nhyne

import dev.nhyne.todo.persistence.TodoItemPersistenceService.TaskPersistence

package object todo {
  type Env = TaskPersistence
}
