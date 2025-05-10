package com.example.moneymatters.adapter

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymatters.R
import com.example.moneymatters.data.Goal
import com.example.moneymatters.databinding.ActivityGoalBinding
import com.example.moneymatters.databinding.ItemGoalBinding

/*
class GoalAdapter(private var goalList: List<Goal>) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(val binding: ActivityGoalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ActivityGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goalList[position]
        holder.binding.monthSelectionEditText.text = Editable.Factory.getInstance().newEditable(goal.month)
        holder.binding.monthSelectionEditText.setText(String.format("%.2f", goal.minimumGoal))
        holder.binding.monthSelectionEditText.setText(String.format("%.2f", goal.maximumGoal))

    }

    override fun getItemCount(): Int = goalList.size

    fun updateData(newGoals: List<Goal>) {
        goalList = newGoals
        notifyDataSetChanged()
    }
}
*/


class GoalAdapter(private var goalList: List<Goal>) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    inner class GoalViewHolder(val binding: ItemGoalBinding) : RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goalList[position]

        holder.binding.textMonth.text = "Month: ${goal.month}"
        holder.binding.textGoalRange.text = "Goal Range: ${goal.minimumGoal} - ${goal.maximumGoal}"
    }

    override fun getItemCount(): Int = goalList.size

    // access the list
    fun getGoals(): List<Goal> = goalList

    // update the data and refresh RecyclerView
    fun updateData(newGoals: List<Goal>) {
        goalList = newGoals
        notifyDataSetChanged()
    }
}

