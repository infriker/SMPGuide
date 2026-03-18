package com.example.smp_help.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smp_help.R
import com.example.smp_help.WebViewActivity
import com.example.smp_help.adapter.MenuItemAdapter
import com.example.smp_help.data.DataSource
import com.example.smp_help.databinding.FragmentListBinding

class SubcategoryFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MenuItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sectionIndex = arguments?.getInt("sectionIndex") ?: 0
        val items = DataSource.templateSections[sectionIndex].items

        adapter = MenuItemAdapter(items) { item ->
            val intent = Intent(requireContext(), WebViewActivity::class.java).apply {
                putExtra(WebViewActivity.EXTRA_URL, item.url)
                putExtra(WebViewActivity.EXTRA_TITLE, item.title)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.queryHint = "Поиск по заголовкам"
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?) = true
                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter(newText ?: "")
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: android.view.MenuItem) = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
