package com.oleohialli.farminventory.views.farmerdetail

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.oleohialli.farminventory.Constants
import com.oleohialli.farminventory.R
import com.oleohialli.farminventory.data.FarmerInfo
import com.oleohialli.farminventory.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail),
    OnMapReadyCallback, CoordinatesDialog.CoordinatesDialogListener {

    private val viewModel: FarmerDetailViewModel by activityViewModels()
    private lateinit var coordinates: CoordinatesDTO
    private lateinit var googleMap: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDetailBinding.bind(view)

        val args = DetailFragmentArgs.fromBundle(requireArguments())
        val currentFarmer = args.farmer

        val mapFragment =
            childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync {
            googleMap = it
            onMapReady(it)
        }

        binding.apply {

            if (currentFarmer != null) {
                viewModel.farmer = currentFarmer
                viewModel.fullName = currentFarmer.farmerName
                viewModel.phone = currentFarmer.farmerPhone
                viewModel.fState = currentFarmer.farmerState
                viewModel.lga = currentFarmer.farmerLga
                viewModel.photoUri = currentFarmer.farmerPhoto.toUri()
                viewModel.farmName = currentFarmer.farmName
                viewModel.farmLocation = currentFarmer.farmLocation
                viewModel.coordinates = currentFarmer.farmCoordinates

                // Set the values into the text fields
                fullNameField.setText(viewModel.farmer?.farmerName)
                phoneField.setText(viewModel.farmer?.farmerPhone)
                stateField.setText(viewModel.farmer?.farmerState)
                lgaField.setText(viewModel.farmer?.farmerLga)
                if (currentFarmer.farmerPhoto != "")
                    photo.setImageURI(viewModel.farmer?.farmerPhoto?.toUri())
                else
                    photo.setImageResource(R.drawable.person_placeholder_transparent)
                farmNameField.setText(currentFarmer.farmName)
                farmLocationField.setText(currentFarmer.farmLocation)
                coordinateField.setText(currentFarmer.farmCoordinates)
                var lat = 0.0
                var lon = 0.0
                if (coordinateField.text.toString().isNotBlank()) {
                    val coord = coordinateField.text.toString().split(',').map { it.trim() }
                    for (c in coord) {
                        lat = c.toDouble()
                        lon = c.toDouble()
                    }
                    coordinates = CoordinatesDTO(lat, lon)
                    mapLayout.visibility = View.VISIBLE
                } else {
                    coordinates = CoordinatesDTO(lat, lon)
                    mapLayout.visibility = View.GONE
                }

            } else {

                viewModel.farmer = null
                viewModel.fullName = ""
                viewModel.phone = ""
                viewModel.fState = ""
                viewModel.lga = ""
                viewModel.photoUri = "".toUri()
                viewModel.farmName = ""
                viewModel.farmLocation = ""
                viewModel.coordinates = ""
                photo.setImageResource(R.drawable.person_placeholder_transparent)
                coordinates = CoordinatesDTO(0.0, 0.0)
            }

            fullNameField.addTextChangedListener { viewModel.fullName = it.toString() }
            phoneField.addTextChangedListener { viewModel.phone = it.toString() }
            stateField.addTextChangedListener { viewModel.fState = it.toString() }
            lgaField.addTextChangedListener { viewModel.lga = it.toString() }
//            photo.addTextChangedListener { viewModel.photo = it.toString() }
            farmNameField.addTextChangedListener { viewModel.farmName = it.toString() }
            farmLocationField.addTextChangedListener { viewModel.farmLocation = it.toString() }
            coordinateField.addTextChangedListener { viewModel.coordinates = it.toString() }

            switchIndicator.setOnClickListener { toggleLayout(farmerDetailLayout, switchIndicator) }
            switchIndicator2.setOnClickListener { toggleLayout(farmDetailLayout, switchIndicator2) }

            val launcher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == Activity.RESULT_OK) {
                        val uri = it.data?.data!!
                        viewModel.photoUri = uri
                        photo.setImageURI(uri)
                    }
                }

            photo.setOnClickListener {
                ImagePicker.with(requireActivity())
                    .galleryMimeTypes(  //Exclude gif images
                        mimeTypes = arrayOf(
                            "image/png",
                            "image/jpg",
                            "image/jpeg"
                        )
                    )
                    .createIntentFromDialog { launcher.launch(it) }

            }

            coordinateField.setOnClickListener { showCoordinatesDialog() }

            //viewModel.testPrint()

            saveButton.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditFarmerEvent.collect { event ->
                when (event) {
                    is FarmerDetailViewModel.AddEditFarmerEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                    is FarmerDetailViewModel.AddEditFarmerEvent.NavigateBackWithResult -> {
                        binding.fullNameField.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun toggleLayout(layoutView: View, switchIcon: ImageView) {
        if (layoutView.isVisible) {
            switchIcon.setImageResource(R.drawable.down_switch)
            layoutView.visibility = View.GONE
        } else {
            switchIcon.setImageResource(R.drawable.up_switch)
            layoutView.visibility = View.VISIBLE
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        println("ddls on map ready...")
        initMap(googleMap)
    }

    private fun showCoordinatesDialog() {
        val dialog = CoordinatesDialog(this)
        dialog.show(
            requireActivity().supportFragmentManager,
            Constants.COORDINATES_DIALOG_FRAGMENT_TAG
        )
    }


    override fun onDialogPositiveClick(coordinates: CoordinatesDTO) {
        this.coordinates = coordinates
        val binding = FragmentDetailBinding.bind(requireView())
        val coordinateString =
            coordinates.latitude.toString() + ", " + coordinates.longitude.toString()
        binding.apply {
            coordinateField.setText(coordinateString)
            coordinateField.clearFocus()
            mapLayout.visibility = View.VISIBLE
        }
        initMap(googleMap)
    }

    private fun initMap(googleMap: GoogleMap) {
        googleMap.clear()
        val latitude = coordinates.latitude
        val longitude = coordinates.longitude
        val location = LatLng(latitude, longitude)

        googleMap.addMarker(
            MarkerOptions()
                .position(location)
                .title("Farm Location")
        )

        googleMap.addPolygon(
            PolygonOptions()
                .clickable(true)
                .add(
                    LatLng(latitude, longitude)
                )
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 18.0f))

    }

    override fun onDialogNegativeClick(dialog: DialogFragment) = dialog.dismiss()

}