package io.b101.picashow.repository

import androidx.annotation.WorkerThread
import io.b101.picashow.dao.MemberDao
import io.b101.picashow.entity.Member
import kotlinx.coroutines.flow.Flow

class MemberRepository(private val memberDao: MemberDao) {
    val allMembers: Flow<List<Member>> = memberDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(member: Member) {
        memberDao.insert(member)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(member: Member) {
        memberDao.delete(member)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(member: Member) {
        memberDao.update(member)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun getMember(memberSeq: Long): Member {
        return memberDao.getMember(memberSeq)
    }
}
