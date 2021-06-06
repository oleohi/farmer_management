package com.oleohialli.farminventory.views.farmer

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.oleohialli.farminventory.R
import com.oleohialli.farminventory.data.FarmerInfo
import com.oleohialli.farminventory.databinding.FragmentDashboardBinding
import com.oleohialli.farminventory.utils.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard), FarmerAdapter.OnItemClickListener {

    private val viewModel: FarmerViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDashboardBinding.bind(view)

        val farmerAdapter = FarmerAdapter(this)
        binding.apply {
            recyclerView.apply {
                adapter = farmerAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val farmer = farmerAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onFarmerSwiped(farmer)
                }

            }).attachToRecyclerView(recyclerView)
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.farmers.observe(viewLifecycleOwner) {
            farmerAdapter.submitList(it)
        }

        binding.addFab.setOnClickListener {
            viewModel.onAddNewFarmerClick()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.farmerEvent.collect { event ->
                when (event) {
                    is FarmerViewModel.FarmerEvent.ShowUndoDeleteFarmerMessage -> {
                        Snackbar.make(requireView(), "Farmer Deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleted(event.farmer)
                            }.show()

                    }

                    is FarmerViewModel.FarmerEvent.NavigateToAddFarmerScreen -> {
                        val action = DashboardFragmentDirections.actionDashboardFragment2ToDetailFragment2()
                        findNavController().navigate(action)
                    }

                    is FarmerViewModel.FarmerEvent.NavigateToEditFarmerScreen -> {
                        val action = DashboardFragmentDirections.actionDashboardFragment2ToDetailFragment2(event.farmer)
                        findNavController().navigate(action)
                    }
                    is FarmerViewModel.FarmerEvent.ShowFarmerSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_farmer_fragment, menu)

        val searchFarmer = menu.findItem(R.id.action_search)
        val searchView = searchFarmer.actionView as SearchView
        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
    }

    override fun onItemClick(farmer: FarmerInfo) {
        viewModel.onFarmerSelected(farmer)
    }

}