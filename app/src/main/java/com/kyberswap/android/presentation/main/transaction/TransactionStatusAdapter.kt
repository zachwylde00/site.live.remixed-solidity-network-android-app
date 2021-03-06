package com.kyberswap.android.presentation.main.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.kyberswap.android.AppExecutors
import com.kyberswap.android.R
import com.kyberswap.android.databinding.ItemHeaderBinding
import com.kyberswap.android.databinding.ItemTransactionBinding
import com.kyberswap.android.domain.model.Transaction
import com.kyberswap.android.presentation.base.DataBoundListAdapter
import com.kyberswap.android.presentation.base.DataBoundViewHolder
import java.util.*

class TransactionStatusAdapter(
    appExecutors: AppExecutors,
    private val walletAddress: String?,
    private val onTransactionClick: ((Transaction) -> Unit)?

) : DataBoundListAdapter<TransactionItem, ViewDataBinding>(
    appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<TransactionItem>() {
        override fun areItemsTheSame(oldItem: TransactionItem, newItem: TransactionItem): Boolean {
            return when {
                oldItem is TransactionItem.Header && newItem is TransactionItem.Header && oldItem.date == newItem.date -> true
                oldItem is TransactionItem.ItemOdd && newItem is TransactionItem.ItemOdd && oldItem.transaction.sameKey(
                    newItem.transaction
                ) -> true
                oldItem is TransactionItem.ItemEven && newItem is TransactionItem.ItemEven && oldItem.transaction.sameKey(
                    newItem.transaction
                ) -> true
//                oldItem is TransactionItem.ItemOdd && newItem is TransactionItem.ItemEven && oldItem.transaction.sameKey(
//                    newItem.transaction
//                ) -> true
//                oldItem is TransactionItem.ItemEven && newItem is TransactionItem.ItemOdd && oldItem.transaction.sameKey(
//                    newItem.transaction
//                ) -> true

                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: TransactionItem, newItem: TransactionItem
        ): Boolean {
            return when {
                oldItem is TransactionItem.Header && newItem is TransactionItem.Header && oldItem.date == newItem.date -> true
                oldItem is TransactionItem.ItemOdd && newItem is TransactionItem.ItemOdd && oldItem.transaction.sameDisplay(
                    newItem.transaction
                ) -> true
                oldItem is TransactionItem.ItemEven && newItem is TransactionItem.ItemEven && oldItem.transaction.sameDisplay(
                    newItem.transaction
                ) -> true
//                oldItem is TransactionItem.ItemOdd && newItem is TransactionItem.ItemEven && oldItem.transaction.sameDisplay(
//                    newItem.transaction
//                ) -> true
//                oldItem is TransactionItem.ItemEven && newItem is TransactionItem.ItemOdd && oldItem.transaction.sameDisplay(
//                    newItem.transaction
//                ) -> true
                else -> false
            }
        }
    }
) {

    override fun bind(binding: ViewDataBinding, item: TransactionItem) {
        when (item) {
            is TransactionItem.Header -> {
                binding as ItemHeaderBinding
                binding.tvDate.text = item.date
            }

            is TransactionItem.ItemEven -> {
                binding as ItemTransactionBinding
                binding(binding, item.transaction)
            }

            is TransactionItem.ItemOdd -> {
                binding as ItemTransactionBinding
                binding(binding, item.transaction)
            }
        }
    }

    private fun binding(binding: ItemTransactionBinding, transaction: Transaction) {
        binding.tvTransactionDetail.text = transaction.displayTransaction
        binding.tvRate.text = transaction.displayRate
        val context = binding.tvRate.context


        val transactionType = when (transaction.transactionType) {
            Transaction.TransactionType.SEND -> {
                context.getString(R.string.filter_send)
            }

            Transaction.TransactionType.RECEIVED -> {
                context.getString(R.string.filter_receive)
            }

            Transaction.TransactionType.SWAP -> {
                context.getString(R.string.filter_swap)
            }
        }
        binding.tvTransactionType.text = transactionType.toUpperCase(Locale.getDefault())
        binding.tvFail.visibility = if (transaction.isTransactionFail) View.VISIBLE else View.GONE
        binding.root.setOnClickListener {
            onTransactionClick?.invoke(transaction)
        }
    }

    override fun getItemViewType(position: Int): Int {

        return when (getData()[position]) {
            is TransactionItem.Header -> TYPE_HEADER
            else -> TYPE_ITEM
        }
    }


    override fun onBindViewHolder(
        holder: DataBoundViewHolder<ViewDataBinding>,
        position: Int
    ) {
        super.onBindViewHolder(holder, position)

        val color = when (getData()[position]) {
            is TransactionItem.Header -> R.color.transaction_header_color
            is TransactionItem.ItemEven -> R.color.transaction_item_even_color
            is TransactionItem.ItemOdd -> R.color.transaction_item_odd_color
        }

        val root = holder.binding.root
        val background = ContextCompat.getColor(
            root.context,
            color
        )
        root.setBackgroundColor(background)
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        val layout =
            if (viewType == TYPE_HEADER) R.layout.item_header else R.layout.item_transaction
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
    }


    companion object {
        private const val TYPE_HEADER = 1
        private const val TYPE_ITEM = 2
    }
}