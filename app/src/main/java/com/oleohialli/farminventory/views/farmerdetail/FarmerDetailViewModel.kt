package com.oleohialli.farminventory.views.farmerdetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oleohialli.farminventory.ADD_FARMER_RESULT_OK
import com.oleohialli.farminventory.EDIT_FARMER_RESULT_OK
import com.oleohialli.farminventory.data.FarmerDao
import com.oleohialli.farminventory.data.FarmerInfo
import com.oleohialli.farminventory.views.farmerdetail.FarmerDetailViewModel.AddEditFarmerEvent.ShowInvalidInputMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FarmerDetailViewModel"

@HiltViewModel
class FarmerDetailViewModel @Inject constructor(
    private val farmerDao: FarmerDao,
    private val state: SavedStateHandle
) : ViewModel() {

    val farmer = state.get<FarmerInfo>("farmer")

    var fullName = state.get<String>("fullName") ?: farmer?.farmerName ?: ""
    set(value) {
        field = value
        state.set("fullName", value)
    }

    var phone = state.get<String>("farmerPhone") ?: farmer?.farmerPhone ?: ""
        set(value) {
            field = value
            state.set("farmerPhone", value)
        }

    var fState = state.get<String>("farmerState") ?: farmer?.farmerState ?: ""
        set(value) {
            field = value
            state.set("farmerState", value)
        }

    var lga = state.get<String>("LGA") ?: farmer?.farmerLga ?: ""
        set(value) {
            field = value
            state.set("LGA", value)
        }

    var photo = state.get<String>("farmerPhoto") ?: farmer?.farmerPhoto ?: ""
        set(value) {
            field = value
            state.set("farmerPhoto", value)
        }

    var farmName = state.get<String>("farmName") ?: farmer?.farmName ?: ""
        set(value) {
            field = value
            state.set("farmName", value)
        }

    var farmLocation = state.get<String>("farmLocation") ?: farmer?.farmLocation ?: ""
        set(value) {
            field = value
            state.set("farmLocation", value)
        }

    var coordinates = state.get<String>("coordinates") ?: farmer?.farmCoordinates ?: ""
        set(value) {
            field = value
            state.set("coordinates", value)
        }

    private val addEditFarmerEventChannel = Channel<AddEditFarmerEvent>()
    val addEditFarmerEvent = addEditFarmerEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (fullName.isBlank()) {
            showInvalidInputMessage("Name cannot be empty $fullName")
            return
        }
        if (farmer != null) {
            println("ddls: farmer exists")
            val updatedFarmer = farmer.copy(
                farmerName = fullName,
                farmerPhone = phone,
                farmerState = fState,
                farmerLga = lga,
                farmerPhoto = photo,
                farmName = farmName,
                farmLocation = farmLocation,
                farmCoordinates = coordinates
            )
            updateFarmer(updatedFarmer)
        } else {
            println("ddls: farmer is null")
            val newFarmer = FarmerInfo(
                farmerName = fullName,
                farmerPhone = phone,
                farmerState = fState,
                farmerLga = lga,
                farmerPhoto = photo,
                farmName = farmName,
                farmLocation = farmLocation,
                farmCoordinates = coordinates
            )
            createFarmer(newFarmer)
        }
    }

    private fun showInvalidInputMessage(msg: String) {
        viewModelScope.launch {
            addEditFarmerEventChannel.send(ShowInvalidInputMessage(msg))
        }
    }

    private fun createFarmer (farmer: FarmerInfo) {
        viewModelScope.launch {
            farmerDao.insert(farmer)
            addEditFarmerEventChannel.send(AddEditFarmerEvent.NavigateBackWithResult(
                ADD_FARMER_RESULT_OK))
        }
    }

    private fun updateFarmer (farmer: FarmerInfo) {
        viewModelScope.launch {
            farmerDao.update(farmer)
            addEditFarmerEventChannel.send(AddEditFarmerEvent.NavigateBackWithResult(EDIT_FARMER_RESULT_OK))
        }
    }

    sealed class AddEditFarmerEvent {
        data class ShowInvalidInputMessage(val message: String) : AddEditFarmerEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditFarmerEvent()
    }
}