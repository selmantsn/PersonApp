package com.example.personapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.personapp.data.DataSource
import com.example.personapp.data.FetchCompletionHandler
import com.example.personapp.data.FetchError
import com.example.personapp.data.FetchResponse

class MainViewModel : ViewModel() {

    val fetchResponse = MutableLiveData<FetchResponse>()
    val fetchError = MutableLiveData<FetchError>()
    val loading = MutableLiveData<Boolean>()

    var mNext: String? = null
    private var dataSource: DataSource = DataSource()

    val isLastPage = MutableLiveData<Boolean>()

    fun getPersons(next: String? = null) {
        loading.value = true
        dataSource.fetch(next, object : FetchCompletionHandler {
            override fun invoke(p1: FetchResponse?, p2: FetchError?) {
                p1?.let {
                    fetchResponse.value = it
                    loading.value = false
                }

                p2?.let {
                    fetchError.value = it
                    loading.value = false
                }
            }
        })

    }

    fun loadMore(linearLayoutManager: LinearLayoutManager, dy: Int, currentPersonCount: Int) {
        val visibleItemCount: Int = linearLayoutManager.childCount
        val totalItemCount: Int = linearLayoutManager.itemCount
        val firstVisibleItemPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
        val lastPage = currentPersonCount == dataSource.getPeopleCount()

        if (lastPage) isLastPage.value = lastPage

        if (!lastPage && loading.value == false && (totalItemCount < 20 || dy > 0)) { //totalItemCount < 20 because min require count for pagination
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 4) { // when get to 4. item from the end, fetch new request
                getPersons(mNext)
            }
        }
    }

}