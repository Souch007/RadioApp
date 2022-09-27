package com.coderoids.radio.ui.radio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coderoids.radio.request.AppApis
import com.coderoids.radio.request.RemoteDataSource
import com.coderoids.radio.request.Resource
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.ui.radio.adapter.OnClickListnerRadio
import com.coderoids.radio.ui.radio.data.temp.RadioLists
import com.coderoids.radio.ui.radio.data.temp.RadioResponse

class RadioViewModel() : ViewModel() , OnClickListnerRadio {

    val remoteDataSource = RemoteDataSource()
    val appRepository = AppRepository(remoteDataSource.buildApi(AppApis::class.java))
    //_________________________________Radio______________________//
    val _radioListing = MutableLiveData<Resource<RadioResponse>>()
    val radioListing: LiveData<Resource<RadioResponse>> = _radioListing

    //_________________________________Radio Public______________________//
    val radioListArray = MutableLiveData<List<RadioLists>>()
    val radioListingArray: LiveData<List<RadioLists>> = radioListArray

    //_________________________________Radio Pop and Rock______________________//
    val _radioPopListArray = MutableLiveData<List<RadioLists>>()
    val radioPopListingArray: LiveData<List<RadioLists>> = _radioPopListArray

    //_________________________________Radio News and Culture______________________//
    val _radioNewsListArray = MutableLiveData<List<RadioLists>>()
    val radioNewsListingArray: LiveData<List<RadioLists>> = _radioNewsListArray

    //_________________________________Radio News and Culture______________________//
    val _radioClassicallistingArry = MutableLiveData<List<RadioLists>>()
    val radioClassicallistingArry: LiveData<List<RadioLists>> = _radioClassicallistingArry



    //_________________Player Listner_____________//
    val _radioClickEvent = MutableLiveData<RadioLists>()
    val radioClickEvent: LiveData<RadioLists> = _radioClickEvent

    override fun onRadioClicked(data: RadioLists) {
        _radioClickEvent.value = data
    }
}