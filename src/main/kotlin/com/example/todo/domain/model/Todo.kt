package com.example.todo.domain.model

/**
 * Todo domain entity representing a task that needs to be done.
 * This is the core domain entity and contains business logic.
 */
class Todo private constructor(
    val id: TodoId?,
    private var _title: String,
    private var _description: String?,
    private var _isDone: Boolean,
    private var _user: User?
) {
    // Value object for Todo ID
    data class TodoId(val value: Long)

    // Properties with getters
    val title: String
        get() = _title

    val description: String?
        get() = _description

    val isDone: Boolean
        get() = _isDone

    val user: User?
        get() = _user

    // Business methods
    fun markAsDone() {
        _isDone = true
    }

    fun markAsUndone() {
        _isDone = false
    }

    fun updateTitle(newTitle: String) {
        require(newTitle.isNotBlank()) { "Title cannot be blank" }
        _title = newTitle
    }

    fun updateDescription(newDescription: String?) {
        _description = newDescription
    }

    // Factory methods
    companion object {
        fun create(title: String, description: String? = null, user: User? = null): Todo {
            require(title.isNotBlank()) { "Title cannot be blank" }
            return Todo(
                id = null,
                _title = title,
                _description = description,
                _isDone = false,
                _user = user
            )
        }

        fun reconstitute(id: Long, title: String, description: String?, isDone: Boolean, user: User? = null): Todo {
            require(id > 0) { "ID must be positive" }
            require(title.isNotBlank()) { "Title cannot be blank" }
            return Todo(
                id = TodoId(id),
                _title = title,
                _description = description,
                _isDone = isDone,
                _user = user
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Todo
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Todo(id=$id, title='$_title', description='$_description', isDone=$_isDone)"
    }
}
