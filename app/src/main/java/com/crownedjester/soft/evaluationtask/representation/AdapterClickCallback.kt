package com.crownedjester.soft.evaluationtask.representation

interface AdapterClickCallback {

    fun onItemLongClicked(isVisible: Boolean)

    fun onDeleteButtonPressed(onAction: () -> Unit)

}