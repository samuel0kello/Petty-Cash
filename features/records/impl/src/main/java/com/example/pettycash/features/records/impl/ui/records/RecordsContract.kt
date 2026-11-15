package com.example.pettycash.features.records.impl.ui.records

import com.example.pettycash.features.records.impl.data.ActivityItem
import com.example.pettycash.features.records.impl.data.ActivityKind

enum class ActivityFilter { All, MoneyIn, MoneyOut, Requests, Spends, Refunds }

data class ActivityState(
    val loading: Boolean = true,
    val items: List<ActivityItem> = emptyList(),
    val query: String = "",
    val filter: ActivityFilter = ActivityFilter.All,
) {
    val filtered: List<ActivityItem>
        get() = items.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.title.contains(query, true) ||
                item.subtitle.contains(query, true) ||
                item.paidTo.contains(query, true)
            val matchesFilter = when (filter) {
                ActivityFilter.All -> true
                ActivityFilter.MoneyIn -> item.incoming
                ActivityFilter.MoneyOut -> !item.incoming
                ActivityFilter.Requests -> item.kind == ActivityKind.Request
                ActivityFilter.Spends -> item.kind == ActivityKind.Spend
                ActivityFilter.Refunds -> item.kind == ActivityKind.Refund
            }
            matchesQuery && matchesFilter
        }
}

sealed interface ActivityAction {
    data class QueryChanged(val value: String) : ActivityAction
    data class FilterSelected(val filter: ActivityFilter) : ActivityAction
    data class OpenItem(val item: ActivityItem) : ActivityAction
}

sealed interface ActivityEvent {
    data class OpenDetail(val kind: ActivityKind, val id: String) : ActivityEvent
}
