package com.ottogo.weekly.ui.bottomModals

import android.graphics.Rect
import com.ottogo.weekly.ui.bottomModals.Keyboard
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.widget.EmojiTextView
import com.ottogo.weekly.BottomSheetType
import com.ottogo.weekly.BottomSheetViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.chat.SearchBar
import com.ottogo.weekly.ui.theme.Black
import com.ottogo.weekly.ui.theme.Black60
import com.ottogo.weekly.ui.theme.LightGray
import com.ottogo.weekly.viewmodels.emojiunicodes.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun EmojiSheet(bottomSheetViewModel: BottomSheetViewModel) {

    val coroutineScope = rememberCoroutineScope()
    val isKeyboardOpen by keyboardAsState()
    var fullyExpandedHeight = LocalConfiguration.current.screenHeightDp.dp -10.dp
    var halfExpandedHeight = fullyExpandedHeight / 2

//    val anchors = if (isKeyboardOpen == Keyboard.Closed) {
//        mapOf(0f to "none", halfExpandedHeight.toFloat() to "half", fullyExpandedHeight.toFloat() to "full")
//    } else {
//        mapOf(0f to "none", halfExpandedHeight.toFloat() to "half", (halfExpandedHeight + 1).toFloat() to "full")
//    }

    var search by remember {  mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current


    // Hides keyboard and resets search bar if user closes bottom sheet with search bar focused
    val focusRequester = remember { FocusRequester() }
    var searchFocused by remember { mutableStateOf(false) }

    val groupedEmojis: List<List<CategoryUnicodes>> = listOf(
        SmileysPeopleCategoryUnicodes.values().asList(),
        AnimalsNatureCategoryUnicodes.values().asList(),
        FoodDrinkCategoryUnicodes.values().asList(),
        ActivityCategoryUnicodes.values().asList(),
        TravelPlacesCategoryUnicodes.values().asList(),
        ObjectsCategoryUnicodes.values().asList(),
        SymbolsCategoryUnicodes.values().asList(),
        FlagsCategoryUnicodes.values().asList()
    )

    val groupedTitles = listOf(
        "Smileys and People",
        "Animals and Nature",
        "Food and Drink",
        "Activity",
        "Travel and Places",
        "Objects",
        "Symbols",
        "Flags"
    )


    var selectedCategory: Int by remember { mutableStateOf(0) }





    Column(Modifier.height(fullyExpandedHeight)) {
        Box(modifier= Modifier
            .fillMaxWidth()
            .height(24.dp)

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

        EmojiCategoryBar() {
            selectedCategory = it


        }
        Text(text = if (search != "") {
            "Showing results for '$search'"
        } else {groupedTitles[selectedCategory]}, color = Black60, textAlign = TextAlign.Center, modifier = Modifier
            .fillMaxWidth()
            .background(LightGray)
            .padding(start = 8.dp, end = 8.dp))

        EmojiView(if(search != "") {
            searchEmojis(search)} else {groupedEmojis[selectedCategory]}) { emoji ->
            coroutineScope.launch {
                //sheetSwipeableState.animateTo("none")
            }

            bottomSheetViewModel.plotEmoji = emoji
            if (bottomSheetViewModel.plotName != null){
                bottomSheetViewModel.bottomSheetType = BottomSheetType.Planning1
            }
        }
    }

}

fun searchEmojis(search: String): List<CategoryUnicodes> {
    var emojis = mutableListOf<CategoryUnicodes>()
    for (emoji in AllEmojiUnicodes.values().asList()) {
        val emojiName = emoji.name.lowercase()
        if (emojiName.contains(search.lowercase())) {
            emojis.add(emoji)
        }
    }
    return emojis
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmojiView(results: List<CategoryUnicodes>, toggleSheet: (String) -> Unit) {

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    LazyColumn(
        Modifier
            .padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Spacer(Modifier.height(16.dp))
        }
          for (row in 0 until results.size / 5 + 1) {
              item {

                  Row(
                      modifier = Modifier
                          .fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                      for (column in 0 until 5) {
                          val index = row * 5 + column
                          Text((if (index < results.size) results[index].unicode else ""),
                              textAlign = TextAlign.Center,
                              modifier = Modifier
                                  .width((screenWidth - 32.dp) / 5)
                                  .clickable {
                                      toggleSheet.invoke(results[index].unicode)
                                  },
                              style = MaterialTheme.typography.h1
                          )

                      }
                  }
              }
          }
      item {

          Spacer(Modifier.height(16.dp))


      }
    }
}

@Composable
fun EmojiCategoryBar(changeEmoji: (Int) -> Unit) {
    val isKeyboardOpen by keyboardAsState()

    // Keeps track of which icons are selected
    var selected by remember {
        mutableStateOf(0)
    }


//        allButtonsEnabled = if (isKeyboardOpen == com.ottogo.weekly.ui.bottomModals.Keyboard.Opened) {
//            selectedCategories.fill(false)
//            false
//        } else {
//            true
//        }
    val icons = listOf(R.drawable.ic_emotion_line, R.drawable.ic_bear_smile_line, R.drawable.ic_cake_3_line, R.drawable.ic_football_line, R.drawable.ic_road_map_line, R.drawable.ic_lightbulb_line, R.drawable.ic_hashtag, R.drawable.ic_flag_line)
    val contentDescriptions = listOf("SmileysPeopleCategory", "AnimalsNatureCategory", "FoodDrinkCategory", "ActivityCategory", "TravelPlacesCategory", "ObjectsCategoryUnicodes", "SymbolsCategoryUnicodes", "FlagsCategoryUnicodes")

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .background(Color.White), horizontalArrangement = Arrangement.Center
    ) {

        for (i in 0..7){
            // Icons jump to category when clicked

            EmojiCategoryButton(icons[i], contentDescription = contentDescriptions[i],selected == i) {
                changeEmoji(i)
                selected = i
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