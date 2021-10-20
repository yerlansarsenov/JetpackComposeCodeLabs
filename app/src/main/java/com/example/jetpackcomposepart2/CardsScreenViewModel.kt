package com.example.jetpackcomposepart2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposepart2.models.CardModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CardsScreenViewModel : ViewModel() {

    private val _cards = MutableStateFlow(listOf<CardModel>())
    val cards: StateFlow<List<CardModel>> get() = _cards

    private val _revealedCardIdsList = MutableStateFlow(listOf<Int>())
    val revealedCardIdsList: StateFlow<List<Int>> get() = _revealedCardIdsList

    init {
        getFakeData()
    }

    private fun getFakeData() {
        viewModelScope.launch(Dispatchers.Default) {
            val testList = arrayListOf<CardModel>()
            repeat(20) { testList += CardModel(id = it, title = "Card $it") }
            _cards.emit(testList)
        }
    }

    fun onItemExpanded(cardId: Int) {
        if (_revealedCardIdsList.value.contains(cardId)) return
        _revealedCardIdsList.value = _revealedCardIdsList.value.toMutableList().also { list ->
            list.add(cardId)
        }
    }

    fun onItemCollapsed(cardId: Int) {
        if (!_revealedCardIdsList.value.contains(cardId)) return
        _revealedCardIdsList.value = _revealedCardIdsList.value.toMutableList().also { list ->
            list.remove(cardId)
        }
    }

    fun onItemDeleted(cardId: Int) {
        try {
            val cardToDelete = _cards.value.find { card ->
                card.id == cardId
            }
            _cards.value = _cards.value.toMutableList().also { list ->
                list.remove(cardToDelete)
            }
            _revealedCardIdsList.value = _revealedCardIdsList.value.toMutableList().also { list ->
                list.remove(cardToDelete?.id)
            }
        } catch (e: Exception) {

        }
    }
}
