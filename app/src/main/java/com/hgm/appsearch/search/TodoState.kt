package com.hgm.appsearch.search


data class TodoState(
      val searchQuery: String = "",
      val todos: List<Todo> = emptyList()
)
