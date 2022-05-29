package com.interview.pagination.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interview.pagination.model.Resource
import com.interview.pagination.utils.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val detailRepository: DetailRepository
) : ViewModel() {
    private val _res = MutableLiveData<Resource<WeatherResponse>>()

    val res: LiveData<Resource<WeatherResponse>>
        get() = _res

    fun getWeatherData(country: String) = viewModelScope.launch {
        _res.postValue(Resource.loading(null))
        detailRepository.getWeatherApi(country).let {
            if (it.isSuccessful) {
                _res.postValue(Resource.success(it.body()))
            } else {
                _res.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }
}