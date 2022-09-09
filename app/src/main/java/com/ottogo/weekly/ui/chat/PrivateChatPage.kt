package com.ottogo.weekly.ui.chat

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.widget.EmojiTextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GiphyGridView
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.ChatMessage
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.login.coloredShadow
import com.ottogo.weekly.ui.theme.Black
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient


@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun PrivateChatPage(navController: NavController, userViewModel: UserViewModel, userId: Int, webSocket: WebSocketClient?) {


    var sheetSwipeableState = rememberSwipeableState(initialValue = "none")
    val coroutineScope = rememberCoroutineScope()

    GiphyBottomModalSheet(sheetSwipeableState, webSocket, userViewModel, coroutineScope, userId, false) {
        PrivateChatPageContent(navController, userViewModel, userId, webSocket) {
            coroutineScope.launch {
                sheetSwipeableState.animateTo("half")
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PrivateChatPageContent(
    navController: NavController,
    userViewModel: UserViewModel,
    userId: Int,
    webSocket: WebSocketClient?,
    toggleSwipeState: () -> Unit
) {

    var message by remember { mutableStateOf("") }
    val friend = userViewModel.friends[userId]

    Column() {
        Log.d("recomp", "recomp1")


        TitleBar(navController = navController, title = friend?.name ?: "")

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        ChatMessages(messages = userViewModel.friends[userId]?.messages ?: listOf(), userId = userId, currentUserId = userViewModel.profile!!.user_id, modifier = Modifier.weight(1F))

//        Text(userViewModel.friends[userId]?.messages.toString())

        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {

            IconButton(onClick = {
                toggleSwipeState.invoke()
            }){
                Icon(
                    painter = painterResource(id = R.drawable.ic_file_gif_line),
                    contentDescription = "gif",
                    modifier = Modifier.height(24.dp)
                )
            }

            TextField(
                value = message,
                onValueChange = { message = it }, Modifier.weight(1F),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = ExtendedTheme.colors.LightGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                visualTransformation = VisualTransformation.None,
                placeholder = { Text("Message...") },
                shape = RoundedCornerShape(28.dp),
                singleLine = false,
                keyboardActions = KeyboardActions(onSend = {

                }),
            )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = {
                webSocket?.send("{\"recipient\": $userId, \"message\": \"$message\"}")
                userViewModel.addPrivateMessage(
                    message = ChatMessage(
                        user_id = userViewModel.profile!!.user_id,
                        message = message,
                        recipient = userId
                    )
                )
                message = ""

            }, Modifier.clip(CircleShape)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send_plane_fill),
                    contentDescription = "send",
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier
                        .background(MaterialTheme.colors.primary)
                        .padding(12.dp)
                        .size(24.dp)
                )
            }
        }
    }
}

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun GiphyBottomModalSheet(sheetSwipeableState: SwipeableState<String>, webSocket: WebSocketClient?, userViewModel: UserViewModel, coroutineScope: CoroutineScope, userId: Int, isGiphyView: Boolean = true, mainContent: @Composable () -> Unit) {

    Giphy.configure(LocalContext.current, "OGYiQs1RQKTbdR0jAGA0RyqkWD5GEY0z")
    var giphySheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )


    val isKeyboardOpen by keyboardAsState()
    var fullyExpandedHeight = LocalConfiguration.current.screenHeightDp - 10
    var halfExpandedHeight = fullyExpandedHeight / 2

    val anchors = if (isKeyboardOpen == Keyboard.Closed) {
        mapOf(0f to "none", halfExpandedHeight.toFloat() to "half", fullyExpandedHeight.toFloat() to "full")
    } else {
        mapOf(0f to "none", halfExpandedHeight.toFloat() to "half", (halfExpandedHeight + 1).toFloat() to "full")
    }


    var search by remember {  mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current


    // Hides keyboard and resets gif search if user closes bottom sheet with search bar focused
    val focusRequester = remember { FocusRequester() }
    var searchFocused by remember { mutableStateOf(false) }
    if (sheetSwipeableState.currentValue == "none" && searchFocused) {
        keyboardController?.hide()
        search = ""
    }

    val emojiResults = remember { mutableStateListOf<CategoryUnicodes>() }

    BottomSheetScaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                coroutineScope.launch {
                    sheetSwipeableState.animateTo("none")
                }
            })
        },
        scaffoldState = giphySheetState,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetPeekHeight = sheetSwipeableState.offset.value.dp,
        sheetContent = {

            Box(modifier= Modifier
                .fillMaxWidth()
                .height(24.dp)
                .swipeable(
                    state = sheetSwipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.5f) },
                    orientation = Orientation.Vertical,
                    reverseDirection = true
                )
            ) { Box(modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .width(48.dp)
                .height(4.dp)
                .align(Alignment.Center)
                .background(color = Color.Gray)) }


            SearchBar(searchText = search,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { searchFocused = it.isFocused }
                    .padding(start = 12.dp, end = 12.dp)) { search = it }

            EmojiCategoryBar {
                emojiResults.clear()
                emojiResults.addAll(it)
            }

            if (isGiphyView) {
                GiphyView(search) {
                    coroutineScope.launch {
                        sheetSwipeableState.animateTo("none")
                    }
                    webSocket?.send("{\"recipient\": $userId, \"gif\": \"$it\"}")
                    userViewModel.addPrivateMessage(
                        message = ChatMessage(
                            user_id = userViewModel.profile!!.user_id,
                            recipient = userId,
                            gif = it
                        )
                    )
                    keyboardController?.hide()
                }
            } else {


                emojiResults.clear()

                filterEmojis(AllEmojiUnicodes.values().asList(), search) { emojiResults.addAll(it) }

                EmojiView(emojiResults) {
                coroutineScope.launch {
                    sheetSwipeableState.animateTo("none")
                }
                    // TODO: return result here
                    search = it
                }
            }

        },
        sheetGesturesEnabled = false) {

        mainContent()
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmojiView(results: List<CategoryUnicodes>, toggleSheet: (String) -> Unit) {

    LazyVerticalGrid(columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(5), horizontalArrangement = Arrangement.Center, modifier = Modifier
        .fillMaxWidth()
        .padding(start = 12.dp, end = 12.dp, bottom = 12.dp), content = {
        items(results.size) { index ->
            AndroidView(factory = { context ->
                EmojiTextView(context).apply {
                    setTextColor(Black.toArgb())
                    text = results[index].unicode
                    textSize = 48.0F
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setOnClickListener {
                        toggleSheet.invoke(results[index].unicode)
                    }
                }
            })
        }
    })

}

@Composable
fun EmojiCategoryBar(changeEmoji: (List<CategoryUnicodes>) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .coloredShadow(
            color = Color(0xFF000000),
            alpha = 0.03f,
            offsetX = 3.dp,
            offsetY = 8.dp
        )
        .background(Color.White), horizontalArrangement = Arrangement.Center) {
        var selectedCategories = remember { mutableStateListOf<Boolean>(false, false, false, false, false, false, false, false) }
        EmojiCategoryButton(R.drawable.ic_emotion_line, "SmileysPeopleCategory", selectedCategories[0]) {
            if (selectedCategories[0]) {
                selectedCategories.fill(false)
                changeEmoji(AllEmojiUnicodes.values().asList())
            } else {
                selectedCategories.fill(false)
                selectedCategories[0] = true
                changeEmoji(SmileysPeopleCategoryUnicodes.values().asList())
            }
        }
        EmojiCategoryButton(R.drawable.ic_bear_smile_line, "AnimalsNatureCategory", selectedCategories[1]) {
            if (selectedCategories[1]) {
                selectedCategories.fill(false)
                changeEmoji(AllEmojiUnicodes.values().asList())
            } else {
                selectedCategories.fill(false)
                selectedCategories[1] = true
                changeEmoji(AnimalsNatureCategoryUnicodes.values().asList())
            }
        }
        EmojiCategoryButton(R.drawable.ic_cake_3_line, "FoodDrinkCategory", selectedCategories[2]) {
            if (selectedCategories[2]) {
                selectedCategories.fill(false)
                changeEmoji(AllEmojiUnicodes.values().asList())
            } else {
                selectedCategories.fill(false)
                selectedCategories[2] = true
                changeEmoji(FoodDrinkCategoryUnicodes.values().asList())
            }
        }
        EmojiCategoryButton(R.drawable.ic_football_line, "ActivityCategory", selectedCategories[3]) {
            if (selectedCategories[3]) {
                selectedCategories.fill(false)
                changeEmoji(AllEmojiUnicodes.values().asList())
            } else {
                selectedCategories.fill(false)
                selectedCategories[3] = true
                changeEmoji(ActivityCategoryUnicodes.values().asList())
            }
        }
        EmojiCategoryButton(R.drawable.ic_road_map_line, "TravelPlacesCategory", selectedCategories[4]) {
            if (selectedCategories[4]) {
                selectedCategories.fill(false)
                changeEmoji(AllEmojiUnicodes.values().asList())
            } else {
                selectedCategories.fill(false)
                selectedCategories[4] = true
                changeEmoji(TravelPlacesCategoryUnicodes.values().asList())
            }
        }
        EmojiCategoryButton(R.drawable.ic_lightbulb_line, "ObjectsCategoryUnicodes", selectedCategories[5]) {
            if (selectedCategories[5]) {
                selectedCategories.fill(false)
                changeEmoji(AllEmojiUnicodes.values().asList())
            } else {
                selectedCategories.fill(false)
                selectedCategories[5] = true
                changeEmoji(ObjectsCategoryUnicodes.values().asList())
            }
        }
        EmojiCategoryButton(R.drawable.ic_hashtag, "SymbolsCategoryUnicodes", selectedCategories[6]) {
            if (selectedCategories[6]) {
                selectedCategories.fill(false)
                changeEmoji(AllEmojiUnicodes.values().asList())
            } else {
                selectedCategories.fill(false)
                selectedCategories[6] = true
                changeEmoji(SymbolsCategoryUnicodes.values().asList())
            }
        }
        EmojiCategoryButton(R.drawable.ic_flag_line, "FlagsCategoryUnicodes", selectedCategories[7]) {
            if (selectedCategories[7]) {
                selectedCategories.fill(false)
                changeEmoji(AllEmojiUnicodes.values().asList())
            } else {
                selectedCategories.fill(false)
                selectedCategories[7] = true
                changeEmoji(FlagsCategoryUnicodes.values().asList())
            }
        }

    }
}

@Composable
fun EmojiCategoryButton(imageId: Int, contentDescription: String, isSelected: Boolean, onClick: () -> Unit) {
    var tintColor = if (isSelected) Color.DarkGray else Color.LightGray
    IconButton(onClick = { onClick() }) {
        Image(painter = painterResource(id = imageId), contentDescription = contentDescription, colorFilter = ColorFilter.tint(tintColor))
    }
}

fun filterEmojis(emojisList: List<CategoryUnicodes>, emojiSearch: String, onEmojiFound: (List<CategoryUnicodes>) -> Unit) {
    var foundEmojis: List<CategoryUnicodes> = emptyList()
    for (emoji in emojisList) {
        var emojiName = emoji.name.lowercase()
        if (emojiName.contains(emojiSearch.lowercase())) {
            foundEmojis += emoji
        }
    }
    onEmojiFound(foundEmojis)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GiphyView(gifSearch: String, toggleSheet: (String) -> Unit) {
    
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp, top = 12.dp),
            factory = { context ->
                var gifSearch = ""
                val gridView = GiphyGridView(context)

                if (gifSearch == "") {
                    gridView.content = GPHContent.trendingGifs
                } else {
                    gridView.content = GPHContent.searchQuery(gifSearch)
                }

                gridView.callback = object : GPHGridCallback {
                    override fun contentDidUpdate(resultCount: Int) { }
                    override fun didSelectMedia(media: Media) {
                        media.images.original?.gifUrl?.let {
                            toggleSheet.invoke(it)
                        }
                    }
                }

                gridView.apply { }
            },
            update = {
                if (gifSearch == "") {
                    it.content = GPHContent.trendingGifs
                } else {
                    it.content = GPHContent.searchQuery(gifSearch)
                }
            }
        )
}

@Composable
fun ChatMessages (messages: List<ChatMessage>, userId: Int, currentUserId: Int, modifier: Modifier = Modifier){
    val lazyListState = rememberLazyListState()

    LazyColumn(state = lazyListState, reverseLayout = true, modifier = modifier) {
        Log.d("recomp", "recomp2")

        for (index in messages.indices) {
            val previousMessage = messages.getOrNull(index - 1)
            val nextMessage = messages.getOrNull(index + 1)
            lateinit var shape: Shape

            if (previousMessage?.user_id ?: -1 != messages[index].user_id){
                shape = if (messages[index].user_id == currentUserId) {
                    RoundedCornerShape(topEnd = 3.dp, topStart = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp)

                } else {
                    RoundedCornerShape(topEnd = 20.dp, topStart = 3.dp, bottomEnd = 20.dp, bottomStart = 20.dp)

                }
            } else if (nextMessage?.user_id ?: -1 != messages[index].user_id) {
                shape = if (messages[index].user_id == currentUserId) {
                    RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp, bottomEnd = 3.dp, bottomStart = 20.dp)

                } else {
                    RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp, bottomEnd = 20.dp, bottomStart = 3.dp)

                }
            } else {
                shape = if (messages[index].user_id == currentUserId) {
                    RoundedCornerShape(topEnd = 3.dp, topStart = 20.dp, bottomEnd = 3.dp, bottomStart = 20.dp)

                } else {
                    RoundedCornerShape(topEnd = 20.dp, topStart = 3.dp, bottomEnd = 20.dp, bottomStart = 3.dp)

                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = messages[index].message?:"",
                        color = if (messages[index].user_id == currentUserId) {
                            MaterialTheme.colors.onPrimary
                        } else {
                            MaterialTheme.colors.onBackground
                        },
                        modifier = Modifier
                            .align(
                                if (messages[index].user_id == currentUserId) {
                                    Alignment.CenterEnd
                                } else {
                                    Alignment.CenterStart
                                }
                            )
                            .padding(vertical = 2.dp, horizontal = 16.dp)
                            .background(
                                if (messages[index].user_id == currentUserId) {
                                    MaterialTheme.colors.primary
                                } else {
                                    ExtendedTheme.colors.LightGray
                                },
                                shape = shape
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),

                        )
                }
            }
        }
    }
}

@Composable
fun LazyListState.OnBottomReached(
    loadMore : () -> Unit
){
    // ...
}

//LazyColumn(modifier = Modifier, state = lazyListState) {
//    userViewModel.friends?.get(userId)?.messages?.forEach{ message ->
////                    if (index != 0 && index == userViewModel.friends?.get(userId)?.messages?.count()) {
////                        var previousMessage = friend?.messages?[index-1]
////                        var nextMessage = friend?.messages?[index+1]
////                    }
//        Text(text = message.message)
////                    TextStyle(color = , fontSize = 16, fontFamily = nunitoFamily)
//    }
//}

enum class Keyboard {
    Opened, Closed
}

@Composable
fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Opened
            } else {
                Keyboard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}