package com.oleohialli.farminventory.views.farmerdetail

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.textfield.TextInputLayout
import com.oleohialli.farminventory.R

class CoordinatesDialog(private val listener: CoordinatesDialogListener) : DialogFragment() {

    interface CoordinatesDialogListener {
        fun onDialogPositiveClick(coordinates: CoordinatesDTO)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            val view = inflater.inflate(R.layout.layout_coordinates_dialog, null)
            val txtLatitude = view.findViewById<EditText>(R.id.latitudeField)
            val txtLongitude = view.findViewById<EditText>(R.id.longitudeField)
            val latLayout = view.findViewById<TextInputLayout>(R.id.latInputLayout)
            val longLayout = view.findViewById<TextInputLayout>(R.id.longInputLayout)

            builder.setView(view)
                .setMessage("Enter farm coordinates")
                .setPositiveButton(
                    R.string.ok
                ) { _, _ ->
                    val latitude = txtLatitude.text.toString().trim()
                    val longitude = txtLongitude.text.toString().trim()
                    when {
                        latitude == "" -> {
                            latLayout.error = "Latitude value is required"
                        }
                        longitude == "" -> {
                            longLayout.error = "Longitude field is required"
                        }
                        else -> {
                            val coordinates = CoordinatesDTO(latitude.toDouble(), longitude.toDouble())
                            listener.onDialogPositiveClick(coordinates)
                        }
                    }
                }
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
//                    dialog?.cancel()
                    listener.onDialogNegativeClick(this)
                }
            builder.create()
        } ?: throw IllegalStateException("Fragment cannot be null")
    }

}