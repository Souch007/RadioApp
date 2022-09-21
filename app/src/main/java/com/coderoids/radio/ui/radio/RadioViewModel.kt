package com.coderoids.radio.ui.radio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coderoids.radio.request.AppApis
import com.coderoids.radio.request.RemoteDataSource
import com.coderoids.radio.request.Resource
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.ui.radio.adapter.OnClickListnerRadio
import com.coderoids.radio.ui.radio.data.Data
import com.coderoids.radio.ui.radio.data.RadioData
import kotlinx.coroutines.launch

class RadioViewModel() : ViewModel() , OnClickListnerRadio {

    val remoteDataSource = RemoteDataSource()
    val appRepository = AppRepository(remoteDataSource.buildApi(AppApis::class.java))
    //_________________________________Radio______________________//
    val _radioListing = MutableLiveData<Resource<RadioData>>()
    val radioListing: LiveData<Resource<RadioData>> = _radioListing
    val radioListArray = MutableLiveData<List<Data>>()
    val radioListingArray: LiveData<List<Data>> = radioListArray

    //_________________________________Radio Pop and Rock______________________//
    val _radioPopListing = MutableLiveData<Resource<RadioData>>()
    val radioPopListing: LiveData<Resource<RadioData>> = _radioPopListing
    val _radioPopListArray = MutableLiveData<List<Data>>()
    val radioPopListingArray: LiveData<List<Data>> = _radioPopListArray

    //_________________________________Radio News and Culture______________________//
    val _radioNewsListing = MutableLiveData<Resource<RadioData>>()
    val radioNewsListing: LiveData<Resource<RadioData>> = _radioNewsListing
    val _radioNewsListArray = MutableLiveData<List<Data>>()
    val radioNewsListingArray: LiveData<List<Data>> = _radioNewsListArray

    //_________________Player Listner_____________//
    val _radioClickEvent = MutableLiveData<Data>()
    val radioClickEvent: LiveData<Data> = _radioClickEvent

    override fun onRadioClicked(data: Data) {
        _radioClickEvent.value = data
    }
}