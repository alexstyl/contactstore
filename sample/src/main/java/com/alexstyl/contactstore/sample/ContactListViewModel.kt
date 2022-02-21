package com.alexstyl.contactstore.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexstyl.contactstore.ContactStore
import com.alexstyl.contactstore.FetchJob
import com.alexstyl.contactstore.sample.ContactListState.Loaded
import com.alexstyl.contactstore.sample.ContactListState.PermissionRequired
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val permissions: ContactPermission,
    private val contactStore: ContactStore
) : ViewModel() {
    val state = MutableStateFlow<ContactListState>(ContactListState.Loading)

    private val reloadContacts = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private var fetchJob: FetchJob? = null

    init {
        viewModelScope.launch {
            reloadContacts.collect {
                fetchJob?.cancel()
                fetchJob = contactStore.fetchContacts()
                    .collect {
                        state.value = Loaded(it)
                    }
            }
        }
        viewModelScope.launch {
            if (permissions.canReadContacts) {
                reloadContacts.emit(Unit)
            } else {
                state.emit(PermissionRequired)
            }
        }
    }

    fun refreshList() {
        viewModelScope.launch {
            reloadContacts.emit(Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
    }
}
