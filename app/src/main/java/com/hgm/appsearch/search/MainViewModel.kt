package com.hgm.appsearch.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID


class MainViewModel(
      private val todoSearchManager: TodoSearchManager
) : ViewModel() {

      var state by mutableStateOf(TodoState())
            private set

      private var searchJob: Job? = null

      init {
            viewModelScope.launch {
                  todoSearchManager.init()
                  // 初始完数据之后就注释掉，否则有重复数据
                  val todos = (1..100).map {
                        Todo(
                              namespace = "my_todos",
                              id = UUID.randomUUID().toString(),
                              score = 1,
                              title = "Todo ${it + 1}",
                              text = "desc ${it + 1}",
                              isDone = kotlin.random.Random.nextBoolean()
                        )
                  }
                  todoSearchManager.putTodo(todos)
            }
      }


      fun onSearchQuery(query: String) {
            state = state.copy(searchQuery = query)

            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                  delay(500L)// 防抖
                  val todos = todoSearchManager.searchTodos(query)
                  state = state.copy(todos = todos)
            }
      }


      fun onDoneChange(todo: Todo, isDone: Boolean) {
            viewModelScope.launch {
                  todoSearchManager.putTodo(
                        listOf(todo.copy(isDone = isDone))
                  )

                  state = state.copy(
                        todos = state.todos.map {
                              if (it.id == todo.id) {
                                    it.copy(isDone = isDone)
                              }else it
                        }
                  )
            }
      }

      override fun onCleared() {
            super.onCleared()
            todoSearchManager.closeSession()
      }
}