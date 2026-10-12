package com.example.data.model

data class SyncInfo(
    val isOnline: Boolean = true,
    val isManualOfflineMode: Boolean = false,
    val isSyncing: Boolean = false,
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val pendingUploadsCount: Int = 0,
    val syncedItemsCount: Int = 0,
    val syncLogMessage: String = "Connecté au Cloud BIZSTOCK"
) {
    val isEffectivelyOnline: Boolean
        get() = isOnline && !isManualOfflineMode
}
