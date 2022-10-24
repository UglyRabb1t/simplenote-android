package com.automattic.simplenote.billing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.automattic.simplenote.BottomSheetDialogBase
import com.automattic.simplenote.R
import com.automattic.simplenote.databinding.BottomSheetSubscriptionsBinding
import com.automattic.simplenote.viewmodels.IapViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

class SubscriptionBottomSheetDialog : BottomSheetDialogBase() {

    private lateinit var viewModel: IapViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_subscriptions, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(BottomSheetSubscriptionsBinding.bind(view)) {
            viewModel = ViewModelProvider(
                activity as FragmentActivity
            ).get(IapViewModel::class.java)

            val adapter = SubscriptionsAdapter()
            viewModel.planOffers.observe(viewLifecycleOwner) { offers ->
                adapter.submitList(offers){
                    view.findViewById<View>(R.id.plans_loading_progress).visibility = View.GONE
                }
            }

            viewModel.onPurchaseRequest.observe(viewLifecycleOwner) { productDetails ->
                viewModel.buy(
                    productDetails.offerToke,
                    productDetails.productDetails,
                    requireActivity()
                )
            }

            contentRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
            contentRecyclerView.adapter = adapter

            viewModel.onBottomSheetDisplayed()

            dialog?.setOnShowListener { dialogInterface ->
                val sheetDialog = dialogInterface as? BottomSheetDialog

                val bottomSheet = sheetDialog?.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet
                ) as? FrameLayout

                bottomSheet?.let {
                    val behavior = BottomSheetBehavior.from(it)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    companion object {
        val TAG = SubscriptionBottomSheetDialog::class.java.simpleName
    }
}