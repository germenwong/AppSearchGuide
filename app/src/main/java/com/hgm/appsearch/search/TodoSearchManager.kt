package com.hgm.appsearch.search

import android.content.Context
import androidx.appsearch.app.AppSearchSession
import androidx.appsearch.app.PutDocumentsRequest
import androidx.appsearch.app.SearchSpec
import androidx.appsearch.app.SetSchemaRequest
import androidx.appsearch.localstorage.LocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TodoSearchManager(
      private val appContext: Context
) {
      // 会话
      private var session: AppSearchSession? = null

      // 初始化
      suspend fun init() {
            withContext(Dispatchers.IO) {
                  // 通过上下文创建数据库
                  val sessionFuture = LocalStorage.createSearchSessionAsync(
                        LocalStorage.SearchContext.Builder(
                              appContext,
                              "todo"
                        ).build()
                  )

                  val setSchemaRequest = SetSchemaRequest.Builder()
                        .addDocumentClasses(Todo::class.java)
                        .build()

                  // 创建完成后会返回会话，并保持打开
                  session = sessionFuture.get()
                  session?.setSchemaAsync(setSchemaRequest)
            }
      }

      // 准备数据
      suspend fun putTodo(todos: List<Todo>): Boolean {
            return withContext(Dispatchers.IO) {
                  session?.putAsync(
                        PutDocumentsRequest.Builder()
                              .addDocuments(todos)
                              .build()
                  )?.get()?.isSuccess == true
            }
      }

      // 搜索
      suspend fun searchTodos(query: String): List<Todo> {
            return withContext(Dispatchers.IO) {
                  val searchSpec = SearchSpec.Builder()
                        .setSnippetCount(10)// 设置搜索结果的前10位
                        .addFilterNamespaces("my_todos")// 过滤的命名空间
                        .setRankingStrategy(SearchSpec.RANKING_STRATEGY_USAGE_COUNT) // 设置排序模式
                        .build()

                  val result = session?.search(
                        query,
                        searchSpec
                  ) ?: return@withContext emptyList()

                  val page = result.nextPageAsync.get()

                  page.mapNotNull {
                        if (it.genericDocument.schemaType == Todo::class.java.simpleName) {
                              it.getDocument(Todo::class.java)
                        } else null
                  }
            }
      }

      // 关闭会话
      fun closeSession() {
            session?.close()
      }
}