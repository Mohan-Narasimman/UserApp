package com.interview.pagination.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.interview.pagination.data.repository.UsersRepository
import com.interview.pagination.model.Resource
import com.interview.pagination.model.Results
import com.interview.pagination.utils.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: UsersRepository,
    private val listRepository: ListRepository
) : ViewModel() {

    private var currentResult: Flow<PagingData<Results>>? = null

    fun searchPlayers(name: String): Flow<PagingData<Results>> {
        val newResult: Flow<PagingData<Results>> = if(name.isNotEmpty()) {
            repository.getUsers(true).cachedIn(viewModelScope)
        } else {
            repository.getUsers(false).cachedIn(viewModelScope)
        }
        currentResult = newResult.map {
            it.filter { item -> (item.name.first + item.name.last).contains(name,ignoreCase = true) }
        }
        return currentResult as Flow<PagingData<Results>>
    }

    val res = MutableLiveData<Resource<WeatherResponse>>()

    fun getWeatherDataLatLong(latitude: String, longitude: String) = viewModelScope.launch {
        res.postValue(Resource.loading(null))
        listRepository.getWeatherApiLatLong(latitude, longitude).let {
            if (it.isSuccessful) {
                res.postValue(Resource.success(it.body()))
            } else {
                res.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }
}