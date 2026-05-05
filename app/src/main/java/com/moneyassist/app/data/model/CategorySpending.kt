package com.moneyassist.app.data.model

/**
 * A data model representing the total amount spent in a specific category.
 * Used for reporting and visualization.
 */
data class CategorySpending(
    val categoryId: Int,
    val categoryName: String,
    val totalAmount: Double
)
