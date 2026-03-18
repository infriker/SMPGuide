package com.example.smp_help.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smp_help.R
import com.example.smp_help.adapter.MenuItemAdapter
import com.example.smp_help.data.DataSource
import com.example.smp_help.data.MenuItem
import com.example.smp_help.databinding.FragmentListBinding

class TemplatesFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sections = DataSource.templateSections
        val sectionItems = sections.mapIndexed { index, section ->
            MenuItem(section.emoji, section.title, index.toString())
        }

        val adapter = MenuItemAdapter(sectionItems) { item ->
            val index = item.url.toInt()
            val section = sections[index]
            findNavController().navigate(
                R.id.action_templatesFragment_to_subCategoryFragment,
                bundleOf(
                    "sectionIndex" to index,
                    "sectionTitle" to section.title
                )
            )
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
