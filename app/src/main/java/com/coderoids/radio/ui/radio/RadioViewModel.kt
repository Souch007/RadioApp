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
    val _radioListing = MutableLiveData<Resource<RadioData>>()
    val radioListArray = MutableLiveData<List<Data>>()

    val radioListingArray: LiveData<List<Data>> = radioListArray
    val radioListing: LiveData<Resource<RadioData>> = _radioListing

    fun getRadioListing(){
        viewModelScope.launch {
            _radioListing.value = appRepository.getRadioListing()
        }
    }

    override fun onRadioClicked(data: Data) {

    }
}