package by.fakehomer.diplomtest.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import by.fakehomer.diplomtest.ui.theme.Primary
import by.fakehomer.diplomtest.ui.theme.Secondary
import java.util.*

@Composable
fun DynamicCalendar(onDayClicked: (Calendar) -> Unit) {
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var showAddGroceryDialog by remember { mutableStateOf(false) }

    val daysInMonth = getDaysInMonth(currentMonth)
    val monthName = getMonthName(currentMonth.get(Calendar.MONTH))
    val year = currentMonth.get(Calendar.YEAR)

    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFF68B2C),
                    contentColor = Color.White
                ),
                onClick = {
                    currentMonth = Calendar.getInstance().apply {
                        time = currentMonth.time
                        add(Calendar.MONTH, -1)
                    }
                    selectedDay = null
                }
            ) {
                Text("<")
            }
            Text("$monthName $year", style = MaterialTheme.typography.h6)
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFF68B2C),
                    contentColor = Color.White
                ),
                onClick = {
                    currentMonth = Calendar.getInstance().apply {
                        time = currentMonth.time
                        add(Calendar.MONTH, 1)
                    }
                    selectedDay = null
                }
            ) {
                Text(">")
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(daysInMonth) { day ->
                val isSelected = selectedDay == day
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(4.dp)
                        .background(
                            color = if (isSelected) Color.LightGray.copy(alpha = 0.5f) else Color.Transparent
                        )
                        .clickable {
                            selectedDay = day
                            val dayCalendar = Calendar
                                .getInstance()
                                .apply {
                                    time = currentMonth.time
                                    set(Calendar.DAY_OF_MONTH, day + 1)
                                }
                            onDayClicked(dayCalendar)
                        }
                ) {
                    Text(text = "${day + 1}")
                }
            }
        }
    }


}

fun getMonthName(month: Int): String {
    val monthNames = listOf(
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    )
    return monthNames[month]
}

fun getDaysInMonth(calendar: Calendar): Int {
    val monthStart = calendar.clone() as Calendar
    monthStart.set(Calendar.DAY_OF_MONTH, 1)
    val monthEnd = monthStart.clone() as Calendar
    monthEnd.add(Calendar.MONTH, 1)
    monthEnd.add(Calendar.DAY_OF_MONTH, -1)
    return monthEnd.get(Calendar.DAY_OF_MONTH)
}

@Composable
fun AddGroceryDialog(showDialog: Boolean, onDismiss: () -> Unit, onSave: (List<String>) -> Unit) {
    if (showDialog) {
        var text by remember { mutableStateOf("") }
        var itemList by remember { mutableStateOf(mutableListOf<String>()) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Добавьте товары к покупке") },
            text = {
                Column {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Наименование товара") },
                        singleLine = true
                    )
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFF68B2C),
                            contentColor = Color.White
                        ),
                        onClick = {
                            if (text.isNotEmpty()) {
                                itemList.add(text)
                                text = ""
                            }
                        }
                    ) {
                        Text("Добавить")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Товаров будет добавлено:")
                    itemList.forEach { item ->
                        Text("- $item")
                    }
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFF68B2C),
                        contentColor = Color.White
                    ),
                    onClick = { onSave(itemList); onDismiss() }
                ) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Gray,
                        contentColor = Color.White
                    ),
                    onClick = onDismiss
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DynamicCalendar(onDayClicked = {})
}
