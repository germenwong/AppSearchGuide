package com.hgm.appsearch

import android.os.Bundle
import android.widget.CheckBox
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hgm.appsearch.search.MainViewModel
import com.hgm.appsearch.search.Todo
import com.hgm.appsearch.search.TodoSearchManager
import com.hgm.appsearch.ui.theme.AppSearchTheme

class MainActivity : ComponentActivity() {

      private val viewModel: MainViewModel by viewModels(factoryProducer = {
            object : ViewModelProvider.Factory {
                  override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MainViewModel(TodoSearchManager(applicationContext)) as T
                  }
            }
      })


      @OptIn(ExperimentalMaterial3Api::class)
      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                  AppSearchTheme {
                        Surface(
                              modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                        ) {
                              val state = viewModel.state

                              Column(
                                    modifier = Modifier
                                          .fillMaxSize()
                                          .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                              ) {
                                    OutlinedTextField(
                                          value = state.searchQuery,
                                          onValueChange = viewModel::onSearchQuery,
                                          modifier = Modifier.fillMaxWidth()
                                    )

                                    LazyColumn(
                                          modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f),
                                          verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                          items(state.todos) { todo ->
                                                TodoItem(todo = todo, onDoneChange = { isDone ->
                                                      viewModel.onDoneChange(todo, isDone)
                                                })
                                          }
                                    }
                              }
                        }
                  }
            }
      }
}

@Composable
fun TodoItem(
      todo: Todo, onDoneChange: (Boolean) -> Unit, modifier: Modifier = Modifier
) {
      Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
      ) {
            Column {
                  Text(text = todo.title, style = MaterialTheme.typography.titleMedium)
                  Text(text = todo.text, style = MaterialTheme.typography.bodyMedium)
            }

            Checkbox(checked = todo.isDone, onCheckedChange = onDoneChange)
      }
}
