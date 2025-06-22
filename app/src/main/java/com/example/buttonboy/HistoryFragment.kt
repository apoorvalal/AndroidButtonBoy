package com.example.buttonboy

import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.OutputStreamWriter

class HistoryFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((requireActivity().application as ButtonBoyApplication).database.eventDao())
    }
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.historyRecyclerView)
        val exportButton: Button = view.findViewById(R.id.exportButton)

        // Setup RecyclerView
        historyAdapter = HistoryAdapter()
        recyclerView.adapter = historyAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe the LiveData from the ViewModel
        viewModel.allEvents.observe(viewLifecycleOwner, Observer { events ->
            events?.let { historyAdapter.setData(it) }
        })

        // Setup Export button
        exportButton.setOnClickListener {
            exportToCsv()
        }

        return view
    }

    private fun exportToCsv() {
        val eventsToExport = viewModel.allEvents.value
        if (eventsToExport.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No data to export.", Toast.LENGTH_SHORT).show()
            return
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "buttonboy_export_${System.currentTimeMillis()}.csv")
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/ButtonBoy")
        }

        val resolver = requireContext().contentResolver
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        if (uri != null) {
            try {
                resolver.openOutputStream(uri).use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.append("id,timestamp\n")
                        eventsToExport.forEach { event ->
                            writer.append("${event.id},\"${event.timestamp}\"\n")
                        }
                        writer.flush()
                    }
                }
                Toast.makeText(requireContext(), "Exported to Downloads/ButtonBoy", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}