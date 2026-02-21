package com.paraskcd.kcdsearch.ui.modules.home

import com.paraskcd.kcdsearch.services.SearchService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchService: SearchService
): ViewModel() {
}