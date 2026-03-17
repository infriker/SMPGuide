package com.example.smp_help.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smp_help.WebViewActivity
import com.example.smp_help.adapter.MenuItemAdapter
import com.example.smp_help.data.DataSource
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

        val adapter = MenuItemAdapter(DataSource.cardTemplates) { item ->
            val intent = Intent(requireContext(), WebViewActivity::class.java).apply {
                putExtra(WebViewActivity.EXTRA_URL, item.url)
                putExtra(WebViewActivity.EXTRA_TITLE, item.title)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}