package cz.thorne.sdk.storage

import android.content.Context

interface StorageManager<TData> {
    fun saveToStorage(context: Context, data: TData)
    fun loadFromStorage(context: Context) : TData
    fun isEmpty(context: Context) : Boolean
}