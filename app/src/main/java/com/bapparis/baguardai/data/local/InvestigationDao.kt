package com.bapparis.baguardai.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestigationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestigation(
        investigation: InvestigationEntity
    )

    @Query(
        "SELECT * FROM investigation_history ORDER BY createdAt DESC"
    )
    fun getAllInvestigations(): Flow<List<InvestigationEntity>>

    @Query(
        "SELECT * FROM investigation_history WHERE id = :id LIMIT 1"
    )
    suspend fun getInvestigationById(
        id: Long
    ): InvestigationEntity?

    @Query(
        "DELETE FROM investigation_history WHERE id = :id"
    )
    suspend fun deleteInvestigation(id: Long)

    @Query("DELETE FROM investigation_history")
    suspend fun deleteAllInvestigations()
}