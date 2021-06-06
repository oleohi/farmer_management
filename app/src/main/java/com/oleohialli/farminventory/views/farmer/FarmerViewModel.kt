package com.oleohialli.farminventory.views.farmer

import androidx.lifecycle.*
import com.oleohialli.farminventory.ADD_FARMER_RESULT_OK
import com.oleohialli.farminventory.EDIT_FARMER_RESULT_OK
import com.oleohialli.farminventory.data.FarmerDao
import com.oleohialli.farminventory.data.FarmerInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmerViewModel @Inject constructor(
    private val farmerDao: FarmerDao,
    private val state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")

    private val farmersData = searchQuery.switchMap { farmerDao.getFarmers(it) }
    val farmers = farmersData

    private val farmerEventChannel = Channel<FarmerEvent>()
    val farmerEvent = farmerEventChannel.receiveAsFlow()

    fun onFarmerSwiped(farmer: FarmerInfo) {
        viewModelScope.launch {
            farmerDao.delete(farmer)
            farmerEventChannel.send(FarmerEvent.ShowUndoDeleteFarmerMessage(farmer))
        }
    }

    fun onAddNewFarmerClick() {
        viewModelScope.launch {
            farmerEventChannel.send(FarmerEvent.NavigateToAddFarmerScreen)
        }
    }

    fun onFarmerSelected(farmer: FarmerInfo) {
        viewModelScope.launch {
            farmerEventChannel.send(FarmerEvent.NavigateToEditFarmerScreen(farmer))
        }
    }

    fun onUndoDeleted(farmer: FarmerInfo) {
        viewModelScope.launch {
            farmerDao.insert(farmer)
        }
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_FARMER_RESULT_OK -> showFarmerSavedConfirmationMessage("Farmer added")
            EDIT_FARMER_RESULT_OK -> showFarmerSavedConfirmationMessage("Farmer updated")
        }
    }

    private fun showFarmerSavedConfirmationMessage(msg: String) = viewModelScope.launch {
        farmerEventChannel.send(FarmerEvent.ShowFarmerSavedConfirmationMessage(msg))
    }

    sealed class FarmerEvent {
        object NavigateToAddFarmerScreen : FarmerEvent()
        data class NavigateToEditFarmerScreen(val farmer: FarmerInfo) : FarmerEvent()
        data class ShowUndoDeleteFarmerMessage(val farmer: FarmerInfo) : FarmerEvent()
        data class ShowFarmerSavedConfirmationMessage(val message: String) : FarmerEvent()
    }
}