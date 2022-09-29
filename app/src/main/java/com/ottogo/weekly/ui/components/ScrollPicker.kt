package com.ottogo.weekly.ui.components

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.Typography
import com.ottogo.weekly.ui.theme.nunitoFamily
import dev.chrisbanes.snapper.*


@Composable
fun ScrollPicker(
    options: List<List<String>>,
    selectItem: List<(Int) -> Unit>,
    modifier: Modifier = Modifier
) {



    Row(modifier.height(125.dp), verticalAlignment = Alignment.CenterVertically) {
        options.forEachIndexed{ index, list ->
            SingleScrollPicker(list = list, modifier = Modifier, selectItem = selectItem[index])
        }
    }
}



@OptIn(ExperimentalSnapperApi::class)
@Composable
fun SingleScrollPicker(list: List<String>, selectItem: (Int) -> Unit, modifier: Modifier = Modifier){
    val lazyListState: LazyListState = rememberLazyListState()
    val mutableList = remember {
        mutableStateListOf<String>()
    }
    LaunchedEffect(key1 = list, block = {
        mutableList.addAll(list)
        mutableList.addAll(list)
    })
    lazyListState.OnBottomReached {
        mutableList.addAll(list)
    }
    lazyListState.OnTopReached {
        mutableList.addAll(list)
    }
    val layoutInfo: LazyListSnapperLayoutInfo = rememberLazyListSnapperLayoutInfo(lazyListState)
    val secondContentPadding = PaddingValues(vertical = 38.dp)


    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (!lazyListState.isScrollInProgress) {
            layoutInfo.currentItem?.let { selectItem(it.index%list.size) }
        }
    }

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        state = lazyListState,
        flingBehavior = rememberSnapperFlingBehavior(
            lazyListState,
            endContentPadding = secondContentPadding.calculateTopPadding()
        ),
        contentPadding = secondContentPadding,
    ) {


        mutableList.forEachIndexed { index, text ->
            item {
                Text(
                    text, textAlign = TextAlign.Center,
                    modifier = Modifier.width(72.dp).height(48.dp),
                    style = TextStyle(
                        fontFamily = nunitoFamily,
                        fontWeight = if (layoutInfo.currentItem?.index == index) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                        fontSize = 28.sp),
                    color = if (layoutInfo.currentItem?.index == index) {
                        MaterialTheme.colors.onBackground
                    } else {
                        ExtendedTheme.colors.Black40
                    }
                )

            }
        }


    }
}


@Composable
fun LazyListState.OnBottomReached(
    loadMore : () -> Unit
){
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true

            lastVisibleItem.index == layoutInfo.totalItemsCount - 1
        }
    }

    // Convert the state into a cold flow and collect
    LaunchedEffect(shouldLoadMore){
        snapshotFlow { shouldLoadMore.value }
            .collect {
                // if should load more, then invoke loadMore
                if (it) loadMore()
            }
    }
}

@Composable
fun LazyListState.OnTopReached(
    loadMore : () -> Unit
){
    val shouldLoadMore = remember {
        derivedStateOf {
            val firstVisibleItem = layoutInfo.visibleItemsInfo.firstOrNull()
                ?: return@derivedStateOf true

            firstVisibleItem.index == layoutInfo.totalItemsCount + 1
        }
    }

    // Convert the state into a cold flow and collect
    LaunchedEffect(shouldLoadMore){
        snapshotFlow { shouldLoadMore.value }
            .collect {
                // if should load more, then invoke loadMore
                if (it) loadMore()
            }
    }
}