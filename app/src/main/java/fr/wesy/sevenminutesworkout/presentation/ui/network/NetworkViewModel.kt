package fr.wesy.sevenminutesworkout.presentation.ui.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.wesy.sevenminutesworkout.data.NetworkStatusObserver
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    networkStatusObserver: NetworkStatusObserver
) : ViewModel() {
    val isConnected: LiveData<Boolean> = networkStatusObserver.isConnected
}