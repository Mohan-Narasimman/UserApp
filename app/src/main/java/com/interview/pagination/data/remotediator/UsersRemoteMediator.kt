package com.interview.pagination.data.remotediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.interview.pagination.api.UsersApi
import com.interview.pagination.data.db.AppDataBase
import com.interview.pagination.data.entity.RemoteKeys
import com.interview.pagination.model.Results
import com.interview.pagination.utils.Constants.Companion.STARTING_PAGE_INDEX
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class UsersRemoteMediator(
    private val service: UsersApi,
    private val db: AppDataBase,
    private val endPage: Boolean
) : RemoteMediator<Int, Results>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Results>
    ): MediatorResult {
        val key = when (loadType) {
            LoadType.REFRESH -> {
                if (db.usersDao.count() > 0) return MediatorResult.Success(false)
                null
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                getKey()
            }
        }

        try {
            if (key != null) {
                if (key.isEndReached) return MediatorResult.Success(endOfPaginationReached = true)
            }

            val page: Int = key?.nextKey ?: STARTING_PAGE_INDEX
            val apiResponse = service.getUsers(state.config.pageSize, page)

            val usersList = apiResponse.results

            db.withTransaction {
                val nextKey = page + 1

                db.remoteKeysDao.insertKey(
                    RemoteKeys(
                        0,
                        nextKey = nextKey,
                        isEndReached = false
                    )
                )
                db.usersDao.insertMultipleUsers(usersList)
            }
            return MediatorResult.Success(endOfPaginationReached = endPage)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getKey(): RemoteKeys? {
        return db.remoteKeysDao.getKeys().firstOrNull()
    }

}