package by.fakehomer.diplomtest.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import by.fakehomer.diplomtest.R
import by.fakehomer.diplomtest.navigation.Screen
import by.fakehomer.diplomtest.navigation.ShoppingPlannerAppRouter
import by.fakehomer.diplomtest.navigation.SystemBackButtonHandler
import coil.compose.rememberImagePainter
import by.fakehomer.diplomtest.data.wishlist_data.WishlistViewModel
import by.fakehomer.diplomtest.data.wishlist_data.Wishlist
import by.fakehomer.diplomtest.data.wishlist_data.WishItem
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlin.concurrent.thread

fun shareWishlist(context: Context, wishlist: Wishlist) {
    val shareText = buildString {
        append("Вишлист: ${wishlist.name}\n\n")
        wishlist.items.forEach { item ->
            append("Желание: ${item.name}\nЦена: ${item.price} ${item.currency}.\nСсылка: ${item.link}\n\n")
        }
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Поделиться вишлистом"))
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun WishlistScreen(viewModel: WishlistViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val newWishlistName = remember { mutableStateOf("") }
    val showAddItemDialog = remember { mutableStateOf(false) }
    val showEditItemDialog = remember { mutableStateOf(false) }
    val currentWishlist = remember { mutableStateOf<Wishlist?>(null) }
    val currentItem = remember { mutableStateOf<WishItem?>(null) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val wishlistToDelete = remember { mutableStateOf<Wishlist?>(null) }
    val context = LocalContext.current
    var refreshState by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadWishlists()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Желания", color = Color.White) },
                backgroundColor = Color(0xFFF68B2C)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog.value = true },
                backgroundColor = Color(0xFFF68B2C),
                contentColor = Color.White,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить")
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                items(uiState.wishlists) { wishlist ->
                    SwipeToDeleteWishlist(
                        wishlist = wishlist,
                        onDeleteConfirmed = {
                            viewModel.removeWishlist(wishlist.id)
                            showDeleteDialog.value = false
                        },
                        onDeleteRequest = {
                            wishlistToDelete.value = wishlist
                            showDeleteDialog.value = true
                        },
                        onAddItemClick = {
                            currentWishlist.value = wishlist
                            showAddItemDialog.value = true
                        },
                        onShareClick = { shareWishlist(context, wishlist) },
                        onClick = {
                            currentWishlist.value = wishlist
                            ShoppingPlannerAppRouter.navigateTo(Screen.WishlistItemsScreen(wishlist.id)) // Navigate with wishlistId
                        }
                    )
                }
            }
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text(text = "Новый вишлист") },
                    text = {
                        Column {
                            Text("Введите название новго вишлиста:")
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = newWishlistName.value,
                                onValueChange = { newWishlistName.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.addWishlist(newWishlistName.value)
                                showDialog.value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFF68B2C),
                                contentColor = Color.White
                            ),
                        ) {
                            Text("Сохранить")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog.value = false },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF888888),
                                contentColor = Color.White
                            ),
                        ) {
                            Text("Отмена")
                        }
                    }
                )
            }

            if (showAddItemDialog.value) {
                AddItemDialog(
                    onDismiss = { showAddItemDialog.value = false },
                    onSave = { name, price, link, imageUrl, currency ->
                        Log.d("WishlistScreen", "onSave called with: $name, $price, $link, $imageUrl, $currency")
                        currentWishlist.value?.id?.let { wishlistId ->
                            viewModel.addItemToWishlist(wishlistId, name, price, link, imageUrl, currency)
                        }
                        showAddItemDialog.value = false
                    }
                )
            }

            if (showEditItemDialog.value) {
                currentItem.value?.let { item ->
                    EditItemDialog(
                        item = item,
                        onDismiss = { showEditItemDialog.value = false },
                        onSave = { name, price, link, imageUrl, currency ->
                            currentWishlist.value?.id?.let { wishlistId ->
                                viewModel.updateItemInWishlist(
                                    wishlistId,
                                    item.id,
                                    name,
                                    price,
                                    link,
                                    imageUrl,
                                    currency
                                )
                                refreshState = !refreshState
                            }
                            showEditItemDialog.value = false
                        }
                    )
                }
            }

            if (showDeleteDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog.value = false },
                    title = { Text(text = "Подтверждение удаления") },
                    text = { Text(text = "Вы уверены, что хотите удалить этот вишлист?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                wishlistToDelete.value?.let {
                                    viewModel.removeWishlist(it.id)
                                }
                                showDeleteDialog.value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFF68B2C),
                                contentColor = Color.White
                            ),
                        ) {
                            Text("Да")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDeleteDialog.value = false },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF888888),
                                contentColor = Color.White
                            ),
                        ) {
                            Text("Нет")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SwipeToDeleteWishlist(
    wishlist: Wishlist,
    onDeleteConfirmed: () -> Unit,
    onDeleteRequest: () -> Unit,
    onAddItemClick: () -> Unit,
    onShareClick: () -> Unit,
    onClick: () -> Unit
) {
    var isSwiped by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = isSwiped, label = "Swipe Animation")
    val offsetX by transition.animateDp(
        transitionSpec = { tween(durationMillis = 300) },
        label = "Offset Animation"
    ) { swiped -> if (swiped) (-100).dp else 0.dp }
    val backgroundColor by animateColorAsState(targetValue = if (isSwiped) Color.Red else Color(0xFFF68B2C))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (isSwiped) {
                            onDeleteRequest()
                        }
                        isSwiped = false
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        isSwiped = dragAmount < 0
                    }
                )
            }
    ) {
        Card(
            modifier = Modifier
                .offset(x = offsetX)
                .fillMaxWidth()
                .height(100.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp,
            backgroundColor = backgroundColor
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = wishlist.name,
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Желаний: ${wishlist.items.size}", color = Color.White)
                }
                Row {
                    IconButton(onClick = onAddItemClick) {
                        Icon(Icons.Filled.Add, contentDescription = "Добавить желание", tint = Color.White)
                    }
                    IconButton(onClick = onShareClick) {
                        Icon(Icons.Filled.Share, contentDescription = "Поделиться", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AddItemDialog(onDismiss: () -> Unit, onSave: (String, Double, String, String?, String) -> Unit) {
    val itemName = remember { mutableStateOf("") }
    val itemPrice = remember { mutableStateOf("") }
    val itemLink = remember { mutableStateOf("") }
    val itemImageUrl = remember { mutableStateOf("") }
    val selectedCurrency = remember { mutableStateOf("BYN") }
    var currencyDropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            itemImageUrl.value = it.toString()
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Добавить новое желание") },
        text = {
            Column {
                TextField(
                    value = itemName.value,
                    onValueChange = { itemName.value = it },
                    label = { Text("Наименование желания") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = itemPrice.value,
                        onValueChange = { itemPrice.value = it },
                        label = { Text("Цена") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        Button(onClick = { currencyDropdownExpanded = true }) {
                            Text(selectedCurrency.value)
                        }
                        DropdownMenu(
                            expanded = currencyDropdownExpanded,
                            onDismissRequest = { currencyDropdownExpanded = false },
                        ) {
                            listOf("BYN", "USD", "EUR", "RUB").forEach { currency ->
                                DropdownMenuItem(onClick = {
                                    selectedCurrency.value = currency
                                    currencyDropdownExpanded = false
                                }) {
                                    Text(currency)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = itemLink.value,
                    onValueChange = { itemLink.value = it },
                    label = { Text("Ссылка") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFF68B2C),
                    contentColor = Color.White
                ),
                    onClick = { launcher.launch("image/*") }) {
                    Text("Выбрать изображение")
                }
                itemImageUrl.value.let {
                    if (it.isNotBlank()) {
                        Image(
                            painter = rememberImagePainter(it),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = itemPrice.value.toDoubleOrNull() ?: 0.0
                    onSave(itemName.value, price, itemLink.value, itemImageUrl.value.takeIf { it.isNotBlank() }, selectedCurrency.value)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFF68B2C),
                    contentColor = Color.White
                ),
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }, colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF888888),
                contentColor = Color.White
            )) {
                Text("Отмена")
            }
        }
    )
}



@Composable
fun EditItemDialog(
    item: WishItem,
    onDismiss: () -> Unit,
    onSave: (String, Double, String, String?, String) -> Unit
) {
    val itemName = remember { mutableStateOf(item.name) }
    val itemPrice = remember { mutableStateOf(item.price.toString()) }
    val itemLink = remember { mutableStateOf(item.link) }
    val itemImageUrl = remember { mutableStateOf(item.imageUrl ?: "") }
    val selectedCurrency = remember { mutableStateOf(item.currency) }
    var currencyDropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            itemImageUrl.value = it.toString()
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Редактировать") },
        text = {
            Column {
                TextField(
                    value = itemName.value,
                    onValueChange = { itemName.value = it },
                    label = { Text("Наимование желания") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = itemPrice.value,
                        onValueChange = { itemPrice.value = it },
                        label = { Text("Цена") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        Button(onClick = { currencyDropdownExpanded = true }) {
                            Text(selectedCurrency.value)
                        }
                        DropdownMenu(
                            expanded = currencyDropdownExpanded,
                            onDismissRequest = { currencyDropdownExpanded = false },
                        ) {
                            listOf("BYN", "USD", "EUR", "RUB").forEach { currency ->
                                DropdownMenuItem(onClick = {
                                    selectedCurrency.value = currency
                                    currencyDropdownExpanded = false
                                }) {
                                    Text(currency)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = itemLink.value,
                    onValueChange = { itemLink.value = it },
                    label = { Text("Ссылка") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFF68B2C),
                    contentColor = Color.White
                ), onClick = { launcher.launch("image/*") }) {
                    Text("Выбрать изображение")
                }
                itemImageUrl.value.let {
                    if (it.isNotBlank()) {
                        Image(
                            painter = rememberImagePainter(it),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = itemPrice.value.toDoubleOrNull() ?: 0.0
                    onSave(itemName.value, price, itemLink.value, itemImageUrl.value.takeIf { it.isNotBlank() }, selectedCurrency.value)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFF68B2C),
                    contentColor = Color.White
                ),
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }, colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF888888),
                contentColor = Color.White
            )) {
                Text("Отмена")
            }
        }
    )
}




@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun WishlistItemsScreen(wishlistId: String, viewModel: WishlistViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val wishlist = uiState.wishlists.find { it.id == wishlistId }
    val context = LocalContext.current
    val showAddItemDialog = remember { mutableStateOf(false) }
    val showEditItemDialog = remember { mutableStateOf(false) }
    val currentItem = remember { mutableStateOf<WishItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = wishlist?.name ?: "Вишлист", color = Color.White) },
                backgroundColor = Color(0xFFF68B2C),
                actions = {
                    IconButton(onClick = {
                        showAddItemDialog.value = true
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "Добавить желание", tint = Color.White)
                    }
                    IconButton(onClick = {
                        wishlist?.let { shareWishlist(context, it) }
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Поделиться", tint = Color.White)
                    }
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            wishlist?.let {
                LazyColumn {
                    items(it.items) { item ->
                        WishItemCard(
                            item = item,
                            onDeleteClick = {
                                viewModel.removeItemFromWishlist(wishlist.id, item.id)
                            },
                            onEditClick = {
                                currentItem.value = item
                                showEditItemDialog.value = true
                            },
                            onShareClick = {
                                shareWishItem(context, item)
                            }
                        )
                    }
                }
            }

            SystemBackButtonHandler {
                ShoppingPlannerAppRouter.navigateTo(Screen.HomeScreen)
            }
        }

        if (showAddItemDialog.value) {
            AddItemDialog(
                onDismiss = { showAddItemDialog.value = false },
                onSave = { name, price, link, imageUrl, currency ->
                    wishlist?.id?.let { wishlistId ->
                        viewModel.addItemToWishlist(wishlistId, name, price, link, imageUrl, currency)
                    }
                    showAddItemDialog.value = false
                }
            )
        }

        if (showEditItemDialog.value) {
            currentItem.value?.let { item ->
                EditItemDialog(
                    item = item,
                    onDismiss = { showEditItemDialog.value = false },
                    onSave = { name, price, link, imageUrl, currency ->
                        wishlist?.id?.let { wishlistId ->
                            viewModel.updateItemInWishlist(wishlistId, item.id, name, price, link, imageUrl, currency)
                        }
                        showEditItemDialog.value = false
                    }
                )
            }
        }
    }
}




@Composable
fun WishItemCard(
    item: WishItem,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val link = item.link

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!item.imageUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberImagePainter(item.imageUrl),
                    contentDescription = "Изображение желания",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(bottom = 8.dp)
                )
            } else {
                Image(
                    painter = rememberImagePainter(R.drawable.default_profile),
                    contentDescription = "Изображение по умолчанию",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(bottom = 8.dp)
                )
            }
            Text(
                text = item.name,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Цена: ${item.price} ${item.currency}",
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row {
                Button(
                    onClick = { showDialog.value = true },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFF68B2C),
                        contentColor = Color.White
                    ),
                ) {
                    Text("Ссылка")
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Wish")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Wish")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    onShareClick()
                }) {
                    Icon(Icons.Filled.Share, contentDescription = "Share Wish")
                }
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Перейти по ссылке") },
            text = {
                val annotatedString = buildAnnotatedString {
                    append("Вы перейдете по ссылке: ")
                    pushStringAnnotation(tag = "URL", annotation = link)
                    withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                        append(link)
                    }
                    pop()
                }
                ClickableText(
                    text = annotatedString,
                    onClick = { offset ->
                        annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                            .firstOrNull()?.let { annotation ->
                                openLink(context, annotation.item)
                            }
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        openLink(context, link)
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFF68B2C),
                        contentColor = Color.White
                    )
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF888888),
                        contentColor = Color.White
                    )
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

private fun openLink(context: Context, link: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    context.startActivity(intent)
}



fun shareWishItem(context: Context, item: WishItem) {
    val shareText = buildString {
        append("Желание: ${item.name}\n")
        append("Цена: ${item.price} ${item.currency}\n")
        append("Ссылка: ${item.link}\n")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    item.imageUrl?.let { imageUrl ->
        val imageUri = Uri.parse(imageUrl)
        val file = File(imageUri.path)

        if (file.exists()) {
            // Use the local file
            val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            intent.apply {
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/*"
            }
            context.startActivity(Intent.createChooser(intent, "Поделиться желанием"))
        } else {
            thread {
                try {
                    val bitmap = BitmapFactory.decodeStream(URL(imageUrl).openStream())
                    val savedFile = saveImageToExternalStorage(context, bitmap, "shared_image_${item.id}")
                    val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", savedFile)
                    intent.apply {
                        putExtra(Intent.EXTRA_STREAM, contentUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        type = "image/*"
                    }
                    context.startActivity(Intent.createChooser(intent, "Поделиться желанием"))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    } ?: run {
        context.startActivity(Intent.createChooser(intent, "Поделиться желанием"))
    }
}

fun saveImageToExternalStorage(context: Context, bitmap: Bitmap, imageName: String): File {
    val imagesDir = File(context.getExternalFilesDir(null), "Pictures")
    if (!imagesDir.exists()) {
        imagesDir.mkdirs()
    }
    val imageFile = File(imagesDir, "$imageName.jpg")
    val fos = FileOutputStream(imageFile)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    fos.close()
    return imageFile
}



