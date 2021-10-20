package com.example.jetpackcomposepart2.ui.views

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposepart2.R
import com.example.jetpackcomposepart2.CardsScreenViewModel
import com.example.jetpackcomposepart2.models.CardModel
import kotlin.math.roundToInt

const val MIN_DRAG_AMOUNT = 6
const val ACTION_ITEM_SIZE = 56
const val CARD_HEIGHT = 56
const val CARD_OFFSET = 168f

fun Float.dp(): Float = this * density + 0.5f

val density: Float
    get() = Resources.getSystem().displayMetrics.density

val cardCollapsedBackgroundColor = Color(0xFFBDE7EC)
val cardExpandedBackgroundColor = Color(0xFFD1A3FF)

@Composable
fun CardsScreen(viewModel: CardsScreenViewModel) {
    val cards = viewModel.cards.collectAsState()
    val revealedCardIds = viewModel.revealedCardIdsList.collectAsState()

    Scaffold(backgroundColor = Color.White) {
        LazyColumn {
            itemsIndexed(cards.value) { _, card ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    ActionsRow(
                        actionIconSize = ACTION_ITEM_SIZE.dp,
                        onDelete = {
                            /**
                             * not work properly
                             */
//                            viewModel.onItemDeleted(card.id)
                                   },
                        onEdit = { /*TODO*/ },
                        onFavorite = { /*TODO*/ }
                    )
                    DraggableCard(
                        card = card,
                        isRevealed = revealedCardIds.value.contains(card.id),
                        cardHeight = CARD_HEIGHT.dp,
                        cardOffset = CARD_OFFSET.dp(),
                        onExpand = { viewModel.onItemExpanded(card.id) },
                        onCollapse = { viewModel.onItemCollapsed(card.id) },
                    )
                }
            }
        }
    }
}

@Composable
fun ActionsRow(
    actionIconSize: Dp,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onFavorite: () -> Unit,
) {
    Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        IconButton(
            modifier = Modifier.size(actionIconSize),
            onClick = {
                onDelete()
            },
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bin),
                    tint = Color.Gray,
                    contentDescription = "delete action",
                )
            }
        )
        IconButton(
            modifier = Modifier.size(actionIconSize),
            onClick = onEdit,
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    tint = Color.Gray,
                    contentDescription = "edit action",
                )
            },
        )
        IconButton(
            modifier = Modifier.size(actionIconSize),
            onClick = onFavorite,
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favorite),
                    tint = Color.Red,
                    contentDescription = "Expandable Arrow",
                )
            }
        )
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DraggableCard(
    card: CardModel,
    cardHeight: Dp,
    isRevealed: Boolean,
    cardOffset: Float,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
) {
    val offsetX = remember { mutableStateOf(0f) }
    val transitionState = remember {
        MutableTransitionState(isRevealed).apply {
            targetState = !isRevealed
        }
    }
    val transition = updateTransition(transitionState, label = "")
    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = 300) },
        targetValueByState = { if (isRevealed) cardOffset - offsetX.value else -offsetX.value },
    )

    Card(
        modifier = Modifier
            .offset { IntOffset((offsetX.value + offsetTransition).roundToInt(), 0) }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(cardHeight)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    val original = Offset(offsetX.value, 0f)
                    val summed = original + Offset(x = dragAmount, y = 0f)
                    val newValue = Offset(x = summed.x.coerceIn(0f, cardOffset), y = 0f)
                    if (newValue.x >= 10) {
                        onExpand()
                        return@detectHorizontalDragGestures
                    } else if (newValue.x <= 0) {
                        onCollapse()
                        return@detectHorizontalDragGestures
                    }
                    change.consumePositionChange()
                    offsetX.value = newValue.x
                }
            },
        content = { CardTitle(cardTitle = card.title) },
        backgroundColor = Color.Blue
    )
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DraggableCardSimple(
    card: CardModel,
    cardHeight: Dp,
    isRevealed: Boolean,
    cardOffset: Float,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
) {
    val transitionState = remember {
        MutableTransitionState(isRevealed).apply {
            targetState = !isRevealed
        }
    }
    val transition = updateTransition(transitionState, "cardTransition")
    val cardBgColor by transition.animateColor(
        label = "cardBgColorTransition",
        transitionSpec = { tween(durationMillis = 300) },
        targetValueByState = {
            if (isRevealed) cardExpandedBackgroundColor else cardCollapsedBackgroundColor
        }
    )
    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = 300) },
        targetValueByState = { if (isRevealed) cardOffset else 0f },

        )
    val cardElevation by transition.animateDp(
        label = "cardElevation",
        transitionSpec = { tween(durationMillis = 300) },
        targetValueByState = { if (isRevealed) 40.dp else 2.dp }
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(cardHeight)
            .offset { IntOffset(offsetTransition.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    when {
                        dragAmount >= MIN_DRAG_AMOUNT -> onExpand()
                        dragAmount < -MIN_DRAG_AMOUNT -> onCollapse()
                    }
                }
            },
        backgroundColor = cardBgColor,
        shape = RoundedCornerShape(0.dp),
        elevation = cardElevation,
        content = { CardTitle(cardTitle = card.title) }
    )
}

@Composable
fun CardTitle(cardTitle: String) {
    Text(
        text = cardTitle,
        color = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textAlign = TextAlign.Center,
    )
}
