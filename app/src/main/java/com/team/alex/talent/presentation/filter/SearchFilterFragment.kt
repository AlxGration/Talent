package com.team.alex.talent.presentation.filter

import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.app.Dialog
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.team.alex.talent.R
import dagger.hilt.android.AndroidEntryPoint

import android.util.DisplayMetrics
import android.widget.CompoundButton
import com.team.alex.talent.databinding.FragmentSearchFilterBinding

import android.content.DialogInterface
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult

@AndroidEntryPoint
class SearchFilterFragment : BottomSheetDialogFragment() {

    private val viewModel: SearchFilterViewModel by viewModels()
    private var _binding: FragmentSearchFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchFilterBinding.bind(
            inflater.inflate(R.layout.fragment_search_filter, container)
        )

        viewModel.errorMsg.observe(viewLifecycleOwner, { msg ->
            Toast.makeText(context, msg.toString(requireContext()), Toast.LENGTH_LONG).show()
        })

        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            binding.pBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.tagsList.observe(viewLifecycleOwner, { tagsList ->
            val ctx = requireContext()
            val scale = ctx.resources.displayMetrics.scaledDensity;
            tagsList.forEach { tag ->
                val chip: Chip = Chip(ctx).also {
                    it.text = tag.name
                    it.textSize = ctx.resources.getDimension(R.dimen.h4) / scale
                    it.setTextColor(ctx.getColorStateList(R.color.chip_stroke))
                    it.chipBackgroundColor = ctx.getColorStateList(R.color.chip_back)
                    it.chipStrokeWidth = 2f
                    it.isCheckable = true
                    it.checkedIcon = null
                    it.chipStrokeColor = ctx.getColorStateList(R.color.chip_stroke)
                    it.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    it.setChipStrokeColorResource(R.color.red)
                    it.minHeight = 30
                    it.setEnsureMinTouchTargetSize(false)
                }
                if (viewModel.getSelectedTagsList().contains(tag.name)) chip.isChecked=true
                chip.setOnCheckedChangeListener(chipListener)
                binding.chgTags.addView(chip)
            }
        })

        binding.btnDoSearch.setBackgroundResource(R.drawable.style_btn)

        binding.btnDoSearch.setOnClickListener {
            dismiss()
        }

        //try to get arguments from parent
        arguments?.get(TAGS_LIST).let { tags ->
            viewModel.selectTags(tags as List<String>)
        }

        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        val list = viewModel.getSelectedTagsList().toMutableList()
        setFragmentResult(
            TAGS_LIST,
            bundleOf(TAGS_LIST to list)
        )
        super.onDismiss(dialog)
    }

    private val chipListener = CompoundButton.OnCheckedChangeListener { btn, isChecked ->
        val tag = btn.text.toString()
        if (isChecked) viewModel.selectTag(tag)
        else viewModel.removeTag(tag)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        val behavior = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        bottomSheet.setBackgroundResource(R.drawable.style_filter_fragment)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAGS_LIST = "TAGS_LIST"
    }
}