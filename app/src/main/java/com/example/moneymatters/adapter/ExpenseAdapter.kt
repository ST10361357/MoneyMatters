package com.example.moneymatters.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymatters.R
import com.example.moneymatters.data.Expense
import com.example.moneymatters.databinding.ActivityViewExpensesBinding
import com.example.moneymatters.databinding.ItemExpenseBinding

class ExpenseAdapter(private var expenseList: List<Expense>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
       val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.binding.expenseName.text = expense.expenseName
        holder.binding.expenseAmount.text = "R${expense.amount}"
        holder.binding.expenseDate.text = expense.date
        holder.binding.expenseStartTime.text = expense.startTime
        holder.binding.expenseEndTime.text = expense.endTime

        // Set image if available
        if (!expense.photo.isNullOrEmpty()) {
            val imageUri = Uri.parse(expense.photo)
            holder.binding.expenseImage.setImageURI(imageUri)
            holder.binding.expenseImage.visibility = View.VISIBLE
        } else {
            holder.binding.expenseImage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = expenseList.size

    fun updateExpenses(newList: List<Expense>) {
        expenseList = newList
        notifyDataSetChanged()
    }
}
