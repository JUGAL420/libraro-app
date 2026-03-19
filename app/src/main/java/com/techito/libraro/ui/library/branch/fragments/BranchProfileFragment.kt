package com.techito.libraro.ui.library.branch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.techito.libraro.R
import com.techito.libraro.databinding.FragmentBranchProfileBinding

class BranchProfileFragment : Fragment() {

    private var _binding: FragmentBranchProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBranchProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupLibraryTypeSpinner()
        
        binding.btnSubmit.setOnClickListener {
            findNavController().navigate(R.id.branchMasterFragment)
        }
    }

    private fun setupLibraryTypeSpinner() {
        val types = arrayOf("Private", "Public")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, types)
        binding.actvLibraryType.setAdapter(adapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
