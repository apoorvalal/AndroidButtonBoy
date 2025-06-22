package com.example.buttonboy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LogFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels {
        val database = (requireActivity().application as ButtonBoyApplication).database
        MainViewModelFactory(database.eventDao(), database.buttonDao())
    }

    private lateinit var flexboxLayout: FlexboxLayout
    private lateinit var emptyStateText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log, container, false)
        flexboxLayout = view.findViewById(R.id.flexboxLayout)
        emptyStateText = view.findViewById(R.id.emptyStateText)

        val fab: FloatingActionButton = view.findViewById(R.id.addButton)
        fab.setOnClickListener {
            showAddButtonDialog()
        }

        // Observe the list of custom buttons
        viewModel.allButtons.observe(viewLifecycleOwner, Observer { buttons ->
            updateButtonUI(buttons)
        })

        return view
    }

    private fun updateButtonUI(buttons: List<CustomButton>) {
        // Show empty state text if there are no buttons
        emptyStateText.visibility = if (buttons.isEmpty()) View.VISIBLE else View.GONE

        flexboxLayout.removeAllViews() // Clear existing buttons
        buttons.forEach { buttonData ->
            // Create a new MaterialButton programmatically for each button in the database
            val button = MaterialButton(requireContext()).apply {
                text = buttonData.name
                textSize = 40f

                setOnClickListener {
                    viewModel.logEvent(buttonData.name)
                }
                // Optional: Add some styling
                layoutParams = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(8, 8, 8, 8) }
            }
            flexboxLayout.addView(button)
        }
    }

    private fun showAddButtonDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add New Button")

        // Set up the input
        val input = EditText(requireContext())
        input.hint = "e.g., Meditated"
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Add") { dialog, _ ->
            val buttonName = input.text.toString().trim()
            if (buttonName.isNotEmpty()) {
                viewModel.addButton(buttonName)
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }
}
