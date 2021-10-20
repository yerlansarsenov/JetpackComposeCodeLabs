package com.example.jetpackcomposepart2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposepart2.models.CardModel
import com.example.jetpackcomposepart2.ui.theme.JetpackComposePart2Theme
import com.example.jetpackcomposepart2.ui.views.CardsScreen
import com.example.jetpackcomposepart2.ui.views.DropDown

class MainActivity : ComponentActivity() {

    private val cardsScreenViewModel by viewModels<CardsScreenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposePart2Theme {
                // A surface container using the 'background' color from the theme
                CardsScreen(viewModel = cardsScreenViewModel)
//                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    var shouldShowOnboarding by remember { mutableStateOf(true) }

    if (shouldShowOnboarding) {
        OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
    } else {
        Greetings()
    }
}

@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit) {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to the Basics Codelab!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked
            ) {
                Text("Continue")
            }

        }
    }
}

@Composable
fun Greetings(
    names: List<CardModel> = List(1000) {
        CardModel(it, "$it")
    }
) {
    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
        items(items = names) { model ->
            //region ThreeDimensionDropDown
            DropDown(title = model.title) {
                Greeting(name = model.title)
            }
            //endregion
            //region CardContent
//            Card(
//                backgroundColor = MaterialTheme.colors.primary,
//                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
//            ) {
//                CardContent(model.title)
//            }
            //endregion

            //region Greeting
//            Greeting(name = model.title)
            //endregion
        }
    }
}

@Composable
fun Greeting(name: String) {
    val expanded = remember {
        mutableStateOf(false)
    }
    val extraPadding by animateDpAsState(
        targetValue = if (expanded.value) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    Surface(
        color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
        ) {
            Column(modifier = Modifier
                .weight(1F)
                .padding(bottom = extraPadding.coerceAtLeast(0.dp))
            ) {
                Text(text = "Hello, ")
                Text(text = name, style = MaterialTheme.typography.h4)
            }
            OutlinedButton(
                onClick = { expanded.value = !expanded.value }
            ) {
                Text(if (expanded.value) "Show less" else "Show more")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetpackComposePart2Theme {
        MyApp()
    }
}
