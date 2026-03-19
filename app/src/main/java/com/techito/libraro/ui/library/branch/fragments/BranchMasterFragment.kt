package com.techito.libraro.ui.library.branch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.techito.libraro.R
import com.techito.libraro.databinding.FragmentBranchMasterBinding
import com.techito.libraro.ui.library.branch.adapter.LibraryFeatureAdapter

class BranchMasterFragment : Fragment() {

    private var _binding: FragmentBranchMasterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBranchMasterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupOperatingHrsSpinner()
        setupBillingDateSpinner()
        setupFeaturesRecyclerView()
        
        binding.btnSubmit.setOnClickListener {
            // As per request, navigating back to Profile, 
            // though logically it might go to Details (Step 3)
            findNavController().navigate(R.id.branchDetailsFragment)
        }
    }

    private fun setupOperatingHrsSpinner() {
        val hrs = (10..24).map { "$it Hrs" }.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, hrs)
        binding.actvOperatingHrs.setAdapter(adapter)
    }

    private fun setupBillingDateSpinner() {
        val dates = (1..30).map { it.toString() }.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, dates)
        binding.actvBillingDate.setAdapter(adapter)
    }

    private fun setupFeaturesRecyclerView() {
        val features = listOf(
            LibraryFeatureAdapter.Feature("Wifi Access", R.drawable.ic_plus),
            LibraryFeatureAdapter.Feature("AC", R.drawable.ic_plus),
            LibraryFeatureAdapter.Feature("Purified Water", R.drawable.ic_plus),
            LibraryFeatureAdapter.Feature("CCTV", R.drawable.ic_plus),
            LibraryFeatureAdapter.Feature("Newspaper", R.drawable.ic_plus)
        )

        binding.rvFeatures.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = LibraryFeatureAdapter(features)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
