package com.ottogo.weekly.ui.chat

import android.Manifest
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import android.util.TypedValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.*
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GiphyGridView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.ottogo.weekly.viewmodels.UserViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.ChatMessage
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.calendar.DateFunctions.isSameDay
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.login.getFile
import com.ottogo.weekly.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.java_websocket.client.WebSocketClient
import retrofit2.HttpException
import java.io.File
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PrivateChatPage(navController: NavController, userViewModel: UserViewModel, userId: Int, webSocket: WebSocketClient?, openSheet: (profile: Profile?) -> Unit) {


    var giphySheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    GiphyBottomModalSheet(giphySheetState, webSocket, userViewModel, coroutineScope, userId = userId) {
        PrivateChatPageContent(navController, userViewModel, userId, webSocket, toggleSwipeState =  {
            coroutineScope.launch {
                giphySheetState.show()
            }
        }, openSheet = openSheet)
    }

}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PrivateChatPageContent(navController: NavController, userViewModel: UserViewModel, userId: Int, webSocket: WebSocketClient?, toggleSwipeState: () -> Unit, openSheet: (profile: Profile?) -> Unit) {

    var message by remember {
        mutableStateOf("")
    }
    val friend = userViewModel.friends[userId]
//    val focusRequester = FocusRequester()
//
//
//    DisposableEffect(Unit) {
//        focusRequester.requestFocus()
//        onDispose { }
//    }
    var context = LocalContext.current

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    val size: Dp = (screenWidth * context.resources.displayMetrics.density*3/5)
    val scope = rememberCoroutineScope()



    LaunchedEffect(key1 = userId, block = {
        if ((webSocket as WebSocketClient).isClosed) {
            (webSocket as WebSocketClient).connect()
            webSocket?.send("{\"recipient\": $userId}")
            userViewModel.seenChatMessages(userId = userId)
        } else {
            webSocket?.send("{\"recipient\": $userId}")
            userViewModel.seenChatMessages(userId = userId)
        }


    })

    var imageUri: Uri? by remember { mutableStateOf(null) }
    var file: File? by remember { mutableStateOf(null) }
    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val storagePermissionStatus = rememberPermissionState(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
                uri: Uri? -> imageUri = uri
            if (imageUri != null) {
                file = getFile(imageUri = imageUri!!, context)
                bitmap = BitmapFactory.decodeFile(file?.path)
            }
        }
    )

    Column() {


            TitleBar(navController = navController, title = friend?.name ?: "", spot = { ProfilePicture(
                url = friend?.profile_picture, size = 36
            )}, onTitleTap = { openSheet(friend) })

            Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

            ChatMessages(messages = userViewModel.friends[userId]?.messages ?: listOf(), userId = userId, currentUserId = userViewModel.profile!!.user_id, modifier = Modifier.weight(1F))

//        Text(userViewModel.friends[userId]?.messages.toString())



        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Profile Photo",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .height(128.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp)

            )
        }

            Row(modifier = Modifier.padding(top = 12.dp, bottom = 16.dp, start = 16.dp, end = 16.dp), verticalAlignment = Alignment.Bottom) {
//                IconButton(onClick = {
//
//                    when (storagePermissionStatus.status) {
//                        // If the camera permission is granted, then show screen with the feature enabled
//                        PermissionStatus.Granted -> {
//                            galleryLauncher.launch("image/")
//                        }
//                        is PermissionStatus.Denied -> {
//                            storagePermissionStatus.launchPermissionRequest()
//
//                        }
//                    }
//
//
//                }, Modifier.clip(CircleShape)) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_image_2_line),
//                        contentDescription = "image",
//                        tint = MaterialTheme.colors.onBackground,
//                        modifier = Modifier
//                            .background(MaterialTheme.colors.background)
//                            .padding(10.dp)
//                            .size(28.dp)
//                    )
//
//                }



                IconButton(onClick = {
                    toggleSwipeState.invoke()

                }, Modifier.clip(CircleShape)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_file_gif_line),
                        contentDescription = "gif",
                        tint = MaterialTheme.colors.onBackground,
                        modifier = Modifier
                            .background(MaterialTheme.colors.background)
                            .padding(10.dp)
                            .size(28.dp)
                    )

                }

                Spacer(modifier = Modifier.width(8.dp))


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
                    //modifier = Modifier.focusRequester(focusRequester),
                    visualTransformation = VisualTransformation.None,
                    placeholder = { Text("Message...") },
                    shape = RoundedCornerShape(28.dp),
                    singleLine = false,
                    keyboardActions = KeyboardActions(onSend = {
                        webSocket?.send("{\"recipient\": $userId, \"message\": \"$message\"}")
                        userViewModel.addPrivateMessage(
                            message = ChatMessage(
                                user_id = userViewModel.profile!!.user_id,
                                message = message,
                                recipient = userId,
                                seen = true
                            )
                        )
                        message = ""
                    }),
                )
                
                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = {
                    scope.launch {
                        if (file != null){
                            try {
                                var image: MultipartBody.Part? = null

                                val reqFile = file!!.asRequestBody("image/*".toMediaTypeOrNull())
                                image = MultipartBody.Part.createFormData(
                                    "chat_image",
                                    file?.name, reqFile
                                )

                                val chatMessage = friend?.relationship_id?.let {
                                    WeeklyApi.retrofitService.uploadChatImage(
                                        mapOf("Authorization" to "token ${userViewModel.token}"),
                                        it,
                                        userId,
                                        message,
                                        image
                                    )
                                }
                                Log.d("pcerror", chatMessage.toString())


                                webSocket?.send("{\"recipient\": $userId, \"image\": \"${chatMessage!!.image}\"" + if (message.isNullOrEmpty()) {""} else {", \"message\": \"$message\"\n"} +"}")
                                Log.d("pcerror", "2")

                                userViewModel.addPrivateMessage(
                                    message = ChatMessage(
                                        user_id = userViewModel.profile!!.user_id,
                                        message = if (message.isEmpty()){null}else {message},
                                        image = chatMessage?.image,
                                        recipient = userId,
                                        seen = true
                                    )
                                )

                            } catch (e: Exception) {
                                Log.d("pcerror", e.toString())
                                if (e is HttpException) {
                                    Log.d("pcerror", e.response().toString())

                                    Log.d("pcerror", e.message())


                                    if (e.code() >= 400) {
                                        "Issue Sending Image.\n Please Try again."
                                    }
                                }
                            }

                        } else {
                            Log.d("msggg", message)
                            webSocket?.send("{\"recipient\": $userId, \"message\": \"$message\"}")
                            userViewModel.addPrivateMessage(
                                message = ChatMessage(
                                    user_id = userViewModel.profile!!.user_id,
                                    message = message,
                                    recipient = userId,
                                    seen = true
                                )
                            )
                        }

                        message = ""

                    }



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



@Composable
fun ChatMessages (messages: List<ChatMessage>, userId: Int, currentUserId: Int, modifier: Modifier = Modifier){
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    val size: Dp = (screenWidth * context.resources.displayMetrics.density*3/5)



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

            val messageAlignment = if (messages[index].user_id == currentUserId) {
                Alignment.CenterEnd
            } else {
                Alignment.CenterStart
            }

            item {
                Log.d("MSG", nextMessage?.timestamp.toString())
                Log.d("MSG", messages[index].timestamp.toString())
                Log.d("MSG", previousMessage?.timestamp.toString())




                    Box(modifier = Modifier.fillMaxWidth()) {


                        messages[index].gif?.let {
                            val imageLoader = ImageLoader.Builder(context)
                                .components {
                                    if (SDK_INT >= 28) {
                                        add(ImageDecoderDecoder.Factory())
                                    } else {
                                        add(GifDecoder.Factory())
                                    }
                                }
                                .build()
                            val painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current).data(data = it)
                                    .apply(block = fun ImageRequest.Builder.() {
                                        size(size.value.toInt())
                                        scale(Scale.FIT)
                                        precision(Precision.EXACT)
                                    }).build(), imageLoader = imageLoader
                            )

                            Image(
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .align(
                                        messageAlignment
                                    )
                                    .padding(vertical = 2.dp, horizontal = 16.dp)
                                    .width(screenWidth * 3 / 5)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(3.dp))
                            )

                            if (painter.state !is AsyncImagePainter.State.Success) {
                                Box(
                                    modifier = Modifier
                                        .align(
                                            messageAlignment
                                        )
                                        .padding(vertical = 2.dp, horizontal = 16.dp)
                                        .width(screenWidth * 3 / 5)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(LightGray)

                                )
                            }
                        }

                        messages[index].image?.let {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(it)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = stringResource(R.string.content_profile_icon),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .align(
                                        messageAlignment
                                    )
                                    .padding(vertical = 2.dp, horizontal = 16.dp)
                                    .width(screenWidth * 3 / 5)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                        }


                        messages[index].message?.let {

                            Text(
                                text = it,
                                color = if (messages[index].user_id == currentUserId) {
                                    MaterialTheme.colors.onPrimary
                                } else {
                                    MaterialTheme.colors.onBackground
                                },
                                modifier = Modifier
                                    .align(
                                        messageAlignment
                                    )
                                    .padding(vertical = 2.dp, horizontal = 16.dp)
                                    .widthIn(min = 40.dp, max = screenWidth * 3 / 5)
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


                if (!isSameDay(nextMessage?.timestamp, messages[index].timestamp)) {
                    Text(
                        SimpleDateFormat("MMMM d").format(messages[index].timestamp),
                        color = ExtendedTheme.colors.Black60,
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
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

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun GiphyBottomModalSheet(giphySheetState: ModalBottomSheetState, webSocket: WebSocketClient?, userViewModel: UserViewModel, coroutineScope: CoroutineScope, groupId: Int? = null, userId: Int? = null, mainContent: @Composable () -> Unit) {

    Giphy.configure(LocalContext.current, "OGYiQs1RQKTbdR0jAGA0RyqkWD5GEY0z")

    var gifSearch by remember {  mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current


    if (!giphySheetState.isVisible) {
        keyboardController?.hide()
        gifSearch = ""
    }


    ModalBottomSheetLayout(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                coroutineScope.launch {
                    giphySheetState.hide()
                }
            })
        },
        sheetState = giphySheetState,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContent = {

            Box(modifier= Modifier
                .fillMaxWidth()
                .height(24.dp)
            ) { Box(modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .width(48.dp)
                .height(4.dp)
                .align(Alignment.Center)
                .background(color = Color.Gray)) }


            SearchBar(searchText = gifSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp)) { gifSearch = it }

            GiphyView(gifSearch) {
                coroutineScope.launch {
                    giphySheetState.hide()
                }
                if (userId != null){
                    webSocket?.send("{\"recipient\": $userId, \"gif\": \"$it\"}")

                    userViewModel.addPrivateMessage(
                        message = ChatMessage(
                            user_id = userViewModel.profile!!.user_id,
                            recipient = userId,
                            gif = it
                        )
                    )
                } else if (groupId != null){
                    webSocket?.send("{\"group\": $groupId, \"gif\": \"$it\"}")
                    userViewModel.addGroupMessage(
                        message = ChatMessage(
                            user_id = userViewModel.profile!!.user_id,
                            group = groupId,
                            gif = it
                        )
                    )

                }else {}

                keyboardController?.hide()
            }
        }) {

        mainContent()
    }
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


                    (media.images.original?.gifUrl)?.let { toggleSheet.invoke(it) }
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
val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics)
//@Composable
//fun keyboardAsState(): State<Keyboard> {
//    val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
//    val view = LocalView.current
//    DisposableEffect(view) {
//        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
//            val rect = Rect()
//            view.getWindowVisibleDisplayFrame(rect)
//            val screenHeight = view.rootView.height
//            val keypadHeight = screenHeight - rect.bottom
//            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
//                Keyboard.Opened
//            } else {
//                Keyboard.Closed
//            }
//        }
//        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)
//
//        onDispose {
//            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
//        }
//    }
//
//    return keyboardState
//}



