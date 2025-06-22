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
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.ItemTouchHelper

class HistoryFragment : Fragment() {

    // In both LogFragment.kt and HistoryFragment.kt
    private val viewModel: MainViewModel by activityViewModels {
        val database = (requireActivity().application as ButtonBoyApplication).database
        MainViewModelFactory(database.eventDao(), database.buttonDao()) // Pass both DAOs
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

        // Create the swipe-to-delete callback
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, // not used for drag & drop
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // enable swipe left and right
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // not used
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val eventToDelete = historyAdapter.getEventAt(position)
                viewModel.deleteEvent(eventToDelete)

                // Show Snackbar with Undo action
                Snackbar.make(view, "Event deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("UNDO") {
                        viewModel.insertEvent(eventToDelete)
                    }
                    show()
                }
            }
        }

        // Attach the callback to the RecyclerView
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)

        // Observe the LiveData from the ViewModel
        viewModel.allEvents.observe(viewLifecycleOwner) { events -> // Cleaned up redundant SAM
            events?.let {
                historyAdapter.setData(it)
            }
        }

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
                        // In exportToCsv() in HistoryFragment.kt
                        writer.append("id,eventName,timestamp\n") // Add new column header
                        eventsToExport.forEach { event ->
                            writer.append("${event.id},\"${event.eventName}\",\"${event.timestamp}\"\n") // Add new data field
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
