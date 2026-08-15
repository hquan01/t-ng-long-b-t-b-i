package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_action_steps")
data class CustomActionStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val stepOrder: Int,
    val actionName: String,
    val description: String = "",
    val delaySeconds: Int = 2,
    val screenXPercent: Float = 0.5f,
    val screenYPercent: Float = 0.5f,
    val isEnabled: Boolean = true
)
