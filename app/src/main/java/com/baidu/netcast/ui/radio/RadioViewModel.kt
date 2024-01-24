package com.baidu.netcast.ui.radio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baidu.netcast.request.AppApis
import com.baidu.netcast.request.RemoteDataSource
import com.baidu.netcast.request.Resource
import com.baidu.netcast.request.repository.AppRepository
import com.baidu.netcast.ui.radio.genres.Genres
import com.baidu.netcast.ui.radio.adapter.OnClickGeneresListener
import com.baidu.netcast.ui.radio.adapter.OnClickListenerCountires
import com.baidu.netcast.ui.radio.adapter.OnClickListenerLanguages
import com.baidu.netcast.ui.radio.adapter.OnClickListnerRadio
import com.baidu.netcast.ui.radio.countries.Countries
import com.baidu.netcast.ui.radio.data.temp.RadioLists
import com.baidu.netcast.ui.radio.data.temp.RadioResponse
import com.baidu.netcast.ui.radio.lanuages.Data
import com.baidu.netcast.ui.radio.lanuages.Lanuages

class RadioViewModel() : ViewModel() , OnClickListnerRadio , OnClickListenerLanguages,OnClickListenerCountires , OnClickGeneresListener{

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

    //_____________________ Languages ______________________________//

    val _languageListingMutable = MutableLiveData<Resource<Lanuages>>()
    val languagesListingLive : LiveData<Resource<Lanuages>> = _languageListingMutable

    val _langListArray = MutableLiveData<List<Data>>()
    val langListArray : LiveData<List<Data>> = _langListArray

    //______________Countries____________________//

    val _countriesListingMutable = MutableLiveData<Resource<Countries>>()
    val countriesListingLive : LiveData<Resource<Countries>> = _countriesListingMutable

    val _countriesListArray = MutableLiveData<List<com.baidu.netcast.ui.radio.countries.Data>>()
    val countriesListArray : LiveData<List<com.baidu.netcast.ui.radio.countries.Data>> = _countriesListArray


    //______________Geners____________________//

    val _genresListinMutable = MutableLiveData<Resource<Genres>>()
    val _genresListinLive: LiveData<Resource<Genres>> = _genresListinMutable

    val _genresListArray = MutableLiveData<List<com.baidu.netcast.ui.radio.genres.Data>>()
    val genresListArray : LiveData<List<com.baidu.netcast.ui.radio.genres.Data>> = _genresListArray
    //_________________Player Listener_____________//




    override fun onRadioClicked(data: RadioLists,type: String) {
    }

    override fun onLanguageClicked(data: Data) {
    }

    override fun OnCountrlySelected(data: com.baidu.netcast.ui.radio.countries.Data) {
    }

    override fun OnClickGenres(data: com.baidu.netcast.ui.radio.genres.Data) {
    }
}