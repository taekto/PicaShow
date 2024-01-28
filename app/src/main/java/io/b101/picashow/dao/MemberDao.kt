package io.b101.picashow.dao

import androidx.room.Dao
import androidx.room.Query
import io.b101.picashow.entity.Member
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao : BaseDao<Member>{
    @Query("SELECT * FROM member WHERE memberSeq = :memberSeq")
    fun selectById(memberSeq : Long) : Array<Member>

    @Query("SELECT * FROM member")
    fun selectAll() : Array<Member>

    @Query("SELECT * FROM member")
    fun getAll() : Flow<List<Member>>

    @Query("SELECT * FROM member WHERE memberSeq = :memberSeq")
    fun getMember(memberSeq: Long): Member
}