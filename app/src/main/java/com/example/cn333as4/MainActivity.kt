package com.example.cn333as4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cn333as4.ui.theme.Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ImageRender(Modifier)
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun KeywordForm(onSubmit: (String, Int, Int) -> Unit) {
    var width by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val keywordList = listOf("dog", "cat", "lion", "tiger", "apple", "orange")
    var keywordSelectedText by remember { mutableStateOf("") }
    var keywordExpanded by remember { mutableStateOf(false) }
    var keywordTextFieldSize by remember { mutableStateOf(Size.Zero)}
    // Up Icon when expanded and down icon when collapsed
    val icon = if (keywordExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Random Image",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
        )
        OutlinedTextField(
            value = keywordSelectedText,
            onValueChange = { keywordSelectedText = it },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    keywordTextFieldSize = coordinates.size.toSize()
                },
            label = {Text("Keyword")},
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { keywordExpanded = !keywordExpanded })
            }
        )
        DropdownMenu(
            expanded = keywordExpanded,
            onDismissRequest = { keywordExpanded = false },
            modifier = Modifier.width(with(LocalDensity.current){
                keywordTextFieldSize.width.toDp()
            })
        ) {
            keywordList.forEach { label ->
                DropdownMenuItem(onClick = {
                    keywordSelectedText = label
                    keywordExpanded = false
                }) {
                    Text(text = label)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = width.toString(),
            onValueChange = { width = it.toIntOrNull() ?: 0 },
            label = { Text(text = "Image Width") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    keyboardController?.hide()
                }
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = height.toString(),
            onValueChange = { height = it.toIntOrNull() ?: 0 },
            label = { Text(text = "Image Height") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSubmit(keywordSelectedText, width, height)
                    keyboardController?.hide()
                }
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                var success = true
                if(width < 128 || height < 128) {
                    success = false
                    Toast.makeText(context, "Width and Hight must be greater than 128x218", Toast.LENGTH_LONG).show()
                }
                if(keywordSelectedText.isEmpty()) {
                    success = false
                    Toast.makeText(context, "Please enter image keyword", Toast.LENGTH_LONG).show()
                }
                if(success) {
                    onSubmit(keywordSelectedText, width, height)
                }
            },
        ) {
            Text(text = "Random!")
        }
    }
}


@Composable
fun ImageRender(modifier: Modifier) {
    var keyword by remember { mutableStateOf("") }
    var width by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }
    val key = keyword.replace(" ", "-")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        KeywordForm { k, w, h ->
            keyword = k
            width = w
            height = h

        }

        if (keyword.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.height(300.dp)) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://loremflickr.com/$width/$height/$key")
                            .crossfade(true)
                            .build(),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Keyword",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Gray
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "$keyword",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = Color.Black
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Theme {
        ImageRender(Modifier)
    }
}