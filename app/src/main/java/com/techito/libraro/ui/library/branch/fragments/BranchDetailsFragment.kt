package com.techito.libraro.ui.library.branch.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.techito.libraro.R
import com.techito.libraro.databinding.FragmentBranchDetailsBinding
import com.techito.libraro.ui.library.branch.adapter.LibraryImageAdapter

class BranchDetailsFragment : Fragment() {

    private var _binding: FragmentBranchDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageAdapter: LibraryImageAdapter
    private val selectedImages = mutableListOf<Uri>()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            if (selectedImages.size < 4) {
                selectedImages.add(it)
                imageAdapter.setImages(selectedImages)
            } else {
                Toast.makeText(requireContext(), "You can only add up to 4 images", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBranchDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupImageRecyclerView()

        binding.btnSubmit.setOnClickListener {
            // Navigate to Master or Profile as per previous pattern
            findNavController().navigate(R.id.branchMasterFragment)
        }
    }

    private fun setupSpinners() {
        val states = arrayOf("Rajasthan", "Maharashtra", "Gujarat", "Delhi", "Punjab", "Uttar Pradesh", "Madhya Pradesh")
        val stateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, states)
        binding.actvState.setAdapter(stateAdapter)

        val cities = arrayOf("Kota", "Jaipur", "Mumbai", "Pune", "Ahmedabad", "Surat", "New Delhi", "Amritsar", "Lucknow", "Indore")
        val cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cities)
        binding.actvCity.setAdapter(cityAdapter)
    }

    private fun setupImageRecyclerView() {
        imageAdapter = LibraryImageAdapter(
            onAddClick = {
                if (selectedImages.size < 4) {
                    pickImageLauncher.launch("image/*")
                } else {
                    Toast.makeText(requireContext(), "You can only add up to 4 images", Toast.LENGTH_SHORT).show()
                }
            },
            onRemoveClick = { index ->
                selectedImages.removeAt(index)
                imageAdapter.setImages(selectedImages)
            }
        )

        binding.rvImages.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }
        imageAdapter.setImages(selectedImages)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
