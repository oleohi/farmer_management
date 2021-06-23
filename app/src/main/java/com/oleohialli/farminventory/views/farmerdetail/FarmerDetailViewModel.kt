package com.oleohialli.farminventory.views.farmerdetail

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oleohialli.farminventory.ADD_FARMER_RESULT_OK
import com.oleohialli.farminventory.EDIT_FARMER_RESULT_OK
import com.oleohialli.farminventory.data.FarmerDao
import com.oleohialli.farminventory.data.FarmerInfo
import com.oleohialli.farminventory.views.farmerdetail.FarmerDetailViewModel.AddEditFarmerEvent.ShowInvalidInputMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FarmerDetailViewModel"

@HiltViewModel
class FarmerDetailViewModel @Inject constructor(
    private val farmerDao: FarmerDao,
    private val savedState: SavedStateHandle
) : ViewModel() {

    var farmer = savedState.get<FarmerInfo>("farmer")
    var fullName = ""
    var phone = ""
    var fState = ""
    var lga = ""
    var photoUri: Uri = "".toUri()
    var farmName = ""
    var farmLocation = ""
    var coordinates = ""

    fun testPrint() {
        val farmer1 = savedState.get<FarmerInfo>("farmer")
        println("ddls farmer1: " + farmer1)
    }


//    var fullName = savedState.get<String>("fullName") ?: farmer?.farmerName ?: ""
//        set(value) {
//            field = value
//            savedState.set("fullName", value)
//        }
//
//    var phone = savedState.get<String>("farmerPhone") ?: farmer?.farmerPhone ?: ""
//        set(value) {
//            field = value
//            savedState.set("farmerPhone", value)
//        }
//
//    var fState = savedState.get<String>("farmerState") ?: farmer?.farmerState ?: ""
//        set(value) {
//            field = value
//            savedState.set("farmerState", value)
//        }
//
//    var lga = savedState.get<String>("LGA") ?: farmer?.farmerLga ?: ""
//        set(value) {
//            field = value
//            savedState.set("LGA", value)
//        }
//
//    var photo = savedState.get<String>("farmerPhoto") ?: farmer?.farmerPhoto ?: ""
//        set(value) {
//            field = value
//            savedState.set("farmerPhoto", value)
//        }
//
//    var farmName = savedState.get<String>("farmName") ?: farmer?.farmName ?: ""
//        set(value) {
//            field = value
//            savedState.set("farmName", value)
//        }
//
//    var farmLocation = savedState.get<String>("farmLocation") ?: farmer?.farmLocation ?: ""
//        set(value) {
//            field = value
//            savedState.set("farmLocation", value)
//        }
//
//    var coordinates = savedState.get<String>("coordinates") ?: farmer?.farmCoordinates ?: ""
//        set(value) {
//            field = value
//            savedState.set("coordinates", value)
//        }

    private val addEditFarmerEventChannel = Channel<AddEditFarmerEvent>()
    val addEditFarmerEvent = addEditFarmerEventChannel.receiveAsFlow()

    fun onSaveClick() {

        if (fullName.isBlank()) {
            showInvalidInputMessage("Farmer name cannot be empty $fullName")
            return
        }
        if (phone.isBlank()) {
            showInvalidInputMessage("Farmer phone number cannot be empty $phone")
            return
        }
        if (fState.isBlank()) {
            showInvalidInputMessage("Farmer state cannot be empty $fState")
            return
        }
        if (farmName.isBlank()) {
            showInvalidInputMessage("Farm name cannot be empty $farmName")
            return
        }
        if (farmLocation.isBlank()) {
            showInvalidInputMessage("Farm location cannot be empty $farmLocation")
            return
        }

        if (farmer != null) {
            val updatedFarmer = farmer!!.copy(
                farmerName = fullName,
                farmerPhone = phone,
                farmerState = fState,
                farmerLga = lga,
                farmerPhoto = photoUri.toString(),
                farmName = farmName,
                farmLocation = farmLocation,
                farmCoordinates = coordinates
            )
            updateFarmer(updatedFarmer)
            farmer = updatedFarmer

        } else {
            val newFarmer = FarmerInfo(
                farmerName = fullName,
                farmerPhone = phone,
                farmerState = fState,
                farmerLga = lga,
                farmerPhoto = photoUri.toString(),
                farmName = farmName,
                farmLocation = farmLocation,
                farmCoordinates = coordinates
            )
            createFarmer(newFarmer)
            farmer = newFarmer
        }
    }

    private fun showInvalidInputMessage(msg: String) {
        viewModelScope.launch {
            addEditFarmerEventChannel.send(ShowInvalidInputMessage(msg))
        }
    }

    private fun createFarmer(farmer: FarmerInfo) {
        viewModelScope.launch {
            farmerDao.insert(farmer)
            addEditFarmerEventChannel.send(
                AddEditFarmerEvent.NavigateBackWithResult(
                    ADD_FARMER_RESULT_OK
                )
            )
        }
    }

    private fun updateFarmer(farmer: FarmerInfo) {
        viewModelScope.launch {
            farmerDao.update(farmer)
            addEditFarmerEventChannel.send(
                AddEditFarmerEvent.NavigateBackWithResult(
                    EDIT_FARMER_RESULT_OK
                )
            )
        }
    }

    sealed class AddEditFarmerEvent {
        data class ShowInvalidInputMessage(val message: String) : AddEditFarmerEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditFarmerEvent()
    }
}