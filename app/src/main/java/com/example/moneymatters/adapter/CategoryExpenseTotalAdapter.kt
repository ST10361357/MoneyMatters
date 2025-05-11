package com.example.moneymatters.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymatters.data.CategoryExpenseTotal
import com.example.moneymatters.databinding.CategoryItemBinding

class CategoryExpenseTotalAdapter(private val categoryTotals: MutableList<CategoryExpenseTotal>) :
    RecyclerView.Adapter<CategoryExpenseTotalAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(private val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryTotal: CategoryExpenseTotal) {
            binding.categoryNameTextView.text = categoryTotal.categoryName
            binding.totalAmountTextView.text = "Total: R${categoryTotal.totalAmount}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryTotals[position])
    }

    override fun getItemCount(): Int = categoryTotals.size

    fun updateData(newCategoryTotals: List<CategoryExpenseTotal>) {
        Log.d("AdapterDebug", "Updating RecyclerView with ${newCategoryTotals.size} items")

        categoryTotals.clear()
        categoryTotals.addAll(newCategoryTotals)
        notifyDataSetChanged()
    }

}

