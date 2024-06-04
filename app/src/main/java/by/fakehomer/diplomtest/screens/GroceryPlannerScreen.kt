package by.fakehomer.diplomtest.screens

import by.fakehomer.diplomtest.data.grocerylist_data.GroceryViewModel
import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GroceryPlannerScreen(viewModel: GroceryViewModel = viewModel()) {
    var showAddPlanDialog by remember { mutableStateOf(false) }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var showEditItemDialog by remember { mutableStateOf(false) }
    var selectedItemForEdit by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    val groceryLists by viewModel.groceryLists.observeAsState(mapOf())
    var groceryList by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    var currentListIndex by remember { mutableStateOf(0) }

    // Date format to use as key
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    LaunchedEffect(selectedDate, groceryLists) {
        val dateKey = dateFormat.format(selectedDate.time)
        groceryList = groceryLists[dateKey]?.getOrNull(currentListIndex) ?: listOf()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Списки покупок", color = Color.White) },
                backgroundColor = Color(0xFFF68B2C)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            DynamicCalendar(selectedDate, onDayClicked = { date ->
                selectedDate = date
                val dateKey = dateFormat.format(date.time)
                currentListIndex = 0
                groceryList = groceryLists[dateKey]?.getOrNull(currentListIndex) ?: listOf()
            })

            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFF68B2C),
                    contentColor = Color.White
                ),
                onClick = {
                    showAddPlanDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Добавить план покупок")
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (groceryList.isNotEmpty() || (groceryLists[dateFormat.format(selectedDate.time)]?.size
                    ?: 0) > 1
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (currentListIndex > 0) {
                                currentListIndex--
                                groceryList =
                                    groceryLists[dateFormat.format(selectedDate.time)]!![currentListIndex]
                            }
                        },
                        enabled = currentListIndex > 0
                    ) {
                        Icon(
                            imageVector = Icons.Default.NavigateBefore,
                            contentDescription = "Previous List"
                        )
                    }
                    Text(
                        "${currentListIndex + 1}/${groceryLists[dateFormat.format(selectedDate.time)]?.size ?: 1}",
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    IconButton(
                        onClick = {
                            if (currentListIndex < (groceryLists[dateFormat.format(selectedDate.time)]?.size
                                    ?: 1) - 1
                            ) {
                                currentListIndex++
                                groceryList =
                                    groceryLists[dateFormat.format(selectedDate.time)]!![currentListIndex]
                            }
                        },
                        enabled = currentListIndex < (groceryLists[dateFormat.format(selectedDate.time)]?.size
                            ?: 1) - 1
                    ) {
                        Icon(
                            imageVector = Icons.Default.NavigateNext,
                            contentDescription = "Next List"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (groceryList.isNotEmpty()) {

                val allItemsChecked = groceryList.all { it.second }
                val listBackgroundColor by animateColorAsState(if (allItemsChecked) Color(0xFF7D9339) else Color(0xFFF68B2C))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(listBackgroundColor, shape = MaterialTheme.shapes.medium)
                        .padding(16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFF59785F),
                                    contentColor = Color.White
                                ),
                                onClick = { showAddItemDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Добавить товар")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFF888888),
                                    contentColor = Color.White
                                ),
                                onClick = {
                                    val dateKey = dateFormat.format(selectedDate.time)
                                    val updatedLists =
                                        groceryLists[dateKey]?.toMutableList() ?: mutableListOf()
                                    if (updatedLists.isNotEmpty()) {
                                        updatedLists.removeAt(currentListIndex)
                                        if (currentListIndex > 0) {
                                            currentListIndex--
                                        }
                                        groceryList =
                                            updatedLists.getOrNull(currentListIndex) ?: listOf()
                                        val mutableGroceryLists = groceryLists.toMutableMap()
                                        mutableGroceryLists[dateKey] = updatedLists
                                        viewModel.updateGroceryLists(mutableGroceryLists)
                                        viewModel.saveGroceryList(dateKey, updatedLists)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Удалить список")
                            }
                        }
                        Divider(
                            color = Color.White.copy(alpha = 0.5f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(groceryList) { item ->
                        val dateKey = dateFormat.format(selectedDate.time)
                        val itemIndex = groceryList.indexOf(item)
                        val checked = item.second

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { newChecked ->
                                    val newList = groceryList.toMutableList()
                                    newList[itemIndex] = newList[itemIndex].copy(second = newChecked)
                                    groceryList = newList
                                    val updatedDateList = groceryLists[dateKey]?.toMutableList() ?: mutableListOf()
                                    updatedDateList[currentListIndex] = newList
                                    val mutableGroceryLists = groceryLists.toMutableMap()
                                    mutableGroceryLists[dateKey] = updatedDateList
                                    viewModel.updateGroceryLists(mutableGroceryLists)
                                    viewModel.saveGroceryList(dateKey, updatedDateList)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.White,
                                    uncheckedColor = Color.White
                                )
                            )
                            Text(
                                text = item.first,
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                selectedItemForEdit = item
                                showEditItemDialog = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = {
                                val newList = groceryList.toMutableList()
                                newList.remove(item)
                                if (newList.isEmpty()) {
                                    val updatedLists = groceryLists[dateKey]?.toMutableList() ?: mutableListOf()
                                    updatedLists.removeAt(currentListIndex)
                                    if (currentListIndex > 0) {
                                        currentListIndex--
                                    }
                                    groceryList = updatedLists.getOrNull(currentListIndex) ?: listOf()
                                    val mutableGroceryLists = groceryLists.toMutableMap()
                                    mutableGroceryLists[dateKey] = updatedLists
                                    viewModel.updateGroceryLists(mutableGroceryLists)
                                    viewModel.saveGroceryList(dateKey, updatedLists)
                                } else {
                                    groceryList = newList
                                    val updatedLists = groceryLists[dateKey]?.toMutableList() ?: mutableListOf()
                                    updatedLists[currentListIndex] = newList
                                    val mutableGroceryLists = groceryLists.toMutableMap()
                                    mutableGroceryLists[dateKey] = updatedLists
                                    viewModel.updateGroceryLists(mutableGroceryLists)
                                    viewModel.saveGroceryList(dateKey, updatedLists)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White
                                )
                            }
                        }
                        Divider(
                            color = Color.White.copy(alpha = 0.5f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            if (showAddPlanDialog) {
                AddGroceryDialog(
                    showDialog = showAddPlanDialog,
                    onDismiss = { showAddPlanDialog = false },
                    onSave = { items ->
                        val dateKey = dateFormat.format(selectedDate.time)
                        val itemsWithCheck = items.map { it to false }
                        val mutableGroceryLists = groceryLists.toMutableMap()
                        if (mutableGroceryLists.containsKey(dateKey)) {
                            val updatedLists = mutableGroceryLists[dateKey]?.toMutableList() ?: mutableListOf()
                            updatedLists.add(itemsWithCheck)
                            currentListIndex = updatedLists.size - 1
                            mutableGroceryLists[dateKey] = updatedLists
                        } else {
                            mutableGroceryLists[dateKey] = mutableListOf(itemsWithCheck)
                            currentListIndex = 0
                        }
                        groceryList = itemsWithCheck
                        viewModel.updateGroceryLists(mutableGroceryLists)
                        viewModel.saveGroceryList(dateKey, mutableGroceryLists[dateKey]!!)
                        showAddPlanDialog = false
                    }
                )
            }

            if (showAddItemDialog) {
                AddItemDialog(
                    showDialog = showAddItemDialog,
                    onDismiss = { showAddItemDialog = false },
                    onSave = { item ->
                        val dateKey = dateFormat.format(selectedDate.time)
                        val newList = groceryList.toMutableList()
                        newList.add(item to false)
                        groceryList = newList
                        val mutableGroceryLists = groceryLists.toMutableMap()
                        if (mutableGroceryLists.containsKey(dateKey)) {
                            val updatedLists = mutableGroceryLists[dateKey]?.toMutableList() ?: mutableListOf()
                            updatedLists[currentListIndex] = newList
                            mutableGroceryLists[dateKey] = updatedLists
                        } else {
                            mutableGroceryLists[dateKey] = mutableListOf(newList)
                        }
                        viewModel.updateGroceryLists(mutableGroceryLists)
                        viewModel.saveGroceryList(dateKey, mutableGroceryLists[dateKey]!!)
                        showAddItemDialog = false
                    }
                )
            }

            if (showEditItemDialog && selectedItemForEdit != null) {
                EditItemDialog(
                    showDialog = showEditItemDialog,
                    initialItem = selectedItemForEdit!!,
                    onDismiss = { showEditItemDialog = false },
                    onSave = { editedItem ->
                        val dateKey = dateFormat.format(selectedDate.time)
                        val newList = groceryList.toMutableList()
                        val index = newList.indexOfFirst { it.first == selectedItemForEdit!!.first && it.second == selectedItemForEdit!!.second }
                        newList[index] = editedItem
                        groceryList = newList
                        val mutableGroceryLists = groceryLists.toMutableMap()
                        if (mutableGroceryLists.containsKey(dateKey)) {
                            val updatedLists = mutableGroceryLists[dateKey]?.toMutableList() ?: mutableListOf()
                            updatedLists[currentListIndex] = newList
                            mutableGroceryLists[dateKey] = updatedLists
                        }
                        viewModel.updateGroceryLists(mutableGroceryLists)
                        viewModel.saveGroceryList(dateKey, mutableGroceryLists[dateKey]!!)
                        showEditItemDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun AddGroceryDialog(showDialog: Boolean, onDismiss: () -> Unit, onSave: (List<String>) -> Unit) {
    if (showDialog) {
        var text by remember { mutableStateOf("") }
        var itemList by remember { mutableStateOf(mutableListOf<String>()) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Добавить план покупок") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Наименование товара") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFF68B2C),
                            contentColor = Color.White
                        ),
                        onClick = {
                            if (text.isNotEmpty()) {
                                itemList = itemList.toMutableList().apply { add(text) }
                                text = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Добавить товар")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Товары для добавления:", fontWeight = FontWeight.Bold)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                    ) {
                        items(itemList) { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = item,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        itemList = itemList.toMutableList().apply { remove(item) }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Удалить",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFF68B2C),
                        contentColor = Color.White
                    ),
                    onClick = { onSave(itemList) },
                    modifier = Modifier.fillMaxWidth()
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
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun AddItemDialog(showDialog: Boolean, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    if (showDialog) {
        var text by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Добавить товар") },
            text = {
                Column {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Наименование товара") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF59785F),
                        contentColor = Color.White
                    ),
                    onClick = {
                        if (text.isNotEmpty()) {
                            onSave(text)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
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
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun EditItemDialog(showDialog: Boolean, initialItem: Pair<String, Boolean>, onDismiss: () -> Unit, onSave: (Pair<String, Boolean>) -> Unit) {
    if (showDialog) {
        var text by remember { mutableStateOf(initialItem.first) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Редактировать товар") },
            text = {
                Column {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Наименование товара") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF59785F),
                        contentColor = Color.White
                    ),
                    onClick = {
                        if (text.isNotEmpty()) {
                            onSave(text to initialItem.second)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
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
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun DynamicCalendar(selectedDate: Calendar, onDayClicked: (Calendar) -> Unit) {
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }

    val daysInMonth = getDaysInMonth(currentMonth)
    val monthNames = listOf(
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    )
    val monthName = monthNames[currentMonth.get(Calendar.MONTH)]

    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
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
                }
            ) {
                Text("<")
            }
            Text("$monthName ${currentMonth.get(Calendar.YEAR)}", style = MaterialTheme.typography.h6)
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
                }
            ) {
                Text(">")
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
        ) {
            items(daysInMonth) { day ->
                val dayCalendar = Calendar.getInstance().apply {
                    time = currentMonth.time
                    set(Calendar.DAY_OF_MONTH, day + 1)
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(4.dp)
                        .clickable {
                            onDayClicked(dayCalendar)
                        }
                        .background(
                            if (dayCalendar.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH) &&
                                dayCalendar.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                                dayCalendar.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR)
                            ) {
                                Color.Gray
                            } else {
                                Color.Transparent
                            }
                        )
                ) {
                    Text(text = "${day + 1}")
                }
            }
        }
    }
}

fun getDaysInMonth(calendar: Calendar): List<Int> {
    val monthStart = calendar.clone() as Calendar
    monthStart.set(Calendar.DAY_OF_MONTH, 1)
    val monthEnd = monthStart.clone() as Calendar
    monthEnd.add(Calendar.MONTH, 1)
    monthEnd.add(Calendar.DAY_OF_MONTH, -1)
    return (0 until monthEnd.get(Calendar.DAY_OF_MONTH)).toList()
}
