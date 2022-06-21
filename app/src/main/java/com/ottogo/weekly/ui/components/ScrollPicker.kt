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
    modifier: Modifier = Modifier
) {



    Row(modifier.height(125.dp), verticalAlignment = Alignment.CenterVertically) {
        options.forEach{ list ->
            SingleScrollPicker(list = list, modifier = Modifier.weight(1F))
        }
    }
}



@OptIn(ExperimentalSnapperApi::class)
@Composable
fun SingleScrollPicker(list: List<String>, modifier: Modifier = Modifier){
    val lazyListState: LazyListState = rememberLazyListState()
    val layoutInfo: LazyListSnapperLayoutInfo = rememberLazyListSnapperLayoutInfo(lazyListState)
    val contentPadding = PaddingValues(vertical = 62.dp)

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        state = lazyListState,
        flingBehavior = rememberSnapperFlingBehavior(
            lazyListState = lazyListState,
            snapOffsetForItem = SnapOffsets.Start,
            endContentPadding = contentPadding.calculateBottomPadding(),
        ),
        contentPadding = contentPadding,
    ) {


        list.forEachIndexed { index, text ->
            item {
                Text(
                    text, textAlign = TextAlign.Center,
                    modifier = Modifier.height(25.dp),
                    style = TextStyle(
                        fontFamily = nunitoFamily,
                        fontWeight = if (layoutInfo.currentItem?.index == index) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                        fontSize = 22.sp),
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