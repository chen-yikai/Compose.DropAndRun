package elias.compose.dropandrun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface {
                SwipeUpToReveal()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeUpToReveal() {
    val scrollState = rememberScrollState()
    var dragOffset = remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    var checker = remember { mutableStateOf(false) }
    var showDialog = remember { mutableStateOf(false) }
    val release = remember { mutableStateOf(true) }
    val animateVal = animateFloatAsState(if (release.value) 0f else dragOffset.value)


    // Observe the current scroll position
    LaunchedEffect(scrollState.value) {
        checker.value = scrollState.value == scrollState.maxValue
    }
    LaunchedEffect(dragOffset.value) {
//        if (dragOffset.value < 0f) {
//            delay(10)
//            dragOffset.value += 10
//        }
//        if (dragOffset.value < -200f) {
//            showDialog.value = true
//            dragOffset.value = 0f
//        }
    }
    if (showDialog.value) {
        AlertDialog(onDismissRequest = { showDialog.value = false },
            confirmButton = { showDialog.value = false },
            title = { Text("Boom!!!") })
    }
    Scaffold(topBar = {
        Text(
            animateVal.value.toString() + " " + checker.value.toString(),
            fontSize = 20.sp,
            modifier = Modifier
                .padding(40.dp)
                .background(
                    Color.White
                )
        )
    }) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(200.dp)
                .background(Color.Green)
        )

        Box(
            modifier = Modifier
                .offset(0.dp, animateVal.value.dp)
                .fillMaxSize()
                .background(Color.Red)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
                    .verticalScroll(scrollState)
            ) {
                repeat(50) { index ->
                    Text("hello " + index, fontSize = 20.sp)
                }
            }
        }
        if (checker.value) {
            Box(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(onDragStart = {
                        dragOffset.value = 0f
                        release.value = false
                    }, onDragEnd = {
                        println("end")
                        release.value = true
                        checker.value = false
                    }, onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        if (dragAmount > 0) {
                            checker.value = false
                        }
                        dragOffset.value += dragAmount
                        if (dragOffset.value < -250f) {
                            release.value = true
                        }
                    })
                })
        }
    }
}
