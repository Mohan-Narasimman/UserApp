package com.interview.pagination.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.interview.pagination.model.Results

@Dao
interface UsersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultipleUsers(list: List<Results>)

    @Query("SELECT * FROM users ")
    fun getUsers(): PagingSource<Int, Results>

    @Query("DELETE FROM users")
    suspend fun clearRepos()

    @Query("SELECT COUNT() from users")
    suspend fun count(): Int

}