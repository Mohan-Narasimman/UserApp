package com.interview.pagination.data.repository

import androidx.paging.*
import com.interview.pagination.api.UsersApi
import com.interview.pagination.data.db.AppDataBase
import com.interview.pagination.data.remotediator.UsersRemoteMediator
import com.interview.pagination.model.Results
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepository @Inject constructor(
    private val usersApi: UsersApi,
    private val db: AppDataBase
) {

    private val pagingSourceFactory = { db.usersDao.getUsers() }

    @OptIn(ExperimentalPagingApi::class)
    fun getUsers(endPage: Boolean): Flow<PagingData<Results>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = UsersRemoteMediator(
                usersApi,
                db,
                endPage
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 25
    }
}