package com.oleohialli.farminventory.views.farmerdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.oleohialli.farminventory.R
import com.oleohialli.farminventory.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val viewModel: FarmerDetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDetailBinding.bind(view)

        val args = DetailFragmentArgs.fromBundle(requireArguments())
        val currentFarmer = args.farmer

        binding.apply {

            if (currentFarmer != null) {
                fullNameField.setText(currentFarmer.farmerName)
                phoneField.setText(currentFarmer.farmerPhone)
                stateField.setText(currentFarmer.farmerState)
                lgaField.setText(currentFarmer.farmerLga)
                //photo.setText(viewModel.photo)
                farmNameField.setText(currentFarmer.farmName)
                farmLocationField.setText(currentFarmer.farmLocation)
                coordinateField.setText(currentFarmer.farmCoordinates)
            }

            fullNameField.addTextChangedListener { viewModel.fullName = it.toString() }
            phoneField.addTextChangedListener { viewModel.phone = it.toString() }
            stateField.addTextChangedListener { viewModel.fState = it.toString() }
            lgaField.addTextChangedListener { viewModel.lga = it.toString() }
            //photo.addTextChangedListener { viewModel.photo = it.toString() }
            farmNameField.addTextChangedListener { viewModel.farmName = it.toString() }
            farmLocationField.addTextChangedListener { viewModel.farmLocation = it.toString() }
            coordinateField.addTextChangedListener { viewModel.coordinates = it.toString() }

            switchIndicator.setOnClickListener { toggleLayout(farmerDetailLayout, switchIndicator) }
            switchIndicator2.setOnClickListener { toggleLayout(farmDetailLayout, switchIndicator2) }

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
                        setFragmentResult("add_edit_request", bundleOf("add_edit_result" to event.result))
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
}