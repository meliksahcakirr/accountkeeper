package com.meliksahcakir.accountkeeper.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.meliksahcakir.accountkeeper.data.Account
import com.meliksahcakir.accountkeeper.data.IAccountDataSource
import com.meliksahcakir.accountkeeper.data.UserInfo
import com.meliksahcakir.accountkeeper.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class AccountLocalDataSource internal constructor(
    private val accountDao: AccountDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IAccountDataSource {

    private var uid = ""

    override fun setUserId(uid: String) {
        this.uid = uid
    }

    override fun observeAccounts(): LiveData<Result<List<Account>>> {
        return accountDao.observeAccounts(uid).map {
            Result.Success(it)
        }
    }

    override suspend fun getAccounts(): Result<List<Account>> {
        return withContext(ioDispatcher) {
            try {
                Result.Success(accountDao.getAccounts(uid))
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override fun observeAccount(accountId: String): LiveData<Result<Account>> {
        return accountDao.observeAccountById(accountId).map {
            Result.Success(it)
        }
    }

    override suspend fun getAccount(accountId: String): Result<Account> {
        return withContext(ioDispatcher) {
            try {
                Result.Success(accountDao.getAccountById(accountId))
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun saveAccount(account: Account) {
        withContext(ioDispatcher) {
            accountDao.insertAccount(account)
        }
    }

    override suspend fun saveAccountUnlessAvailable(account: Account) {
        withContext(ioDispatcher) {
            try {
                accountDao.insertAccountIgnoreOnConflict(account)
            } catch (e: Exception) {
                Timber.d(e.printStackTrace().toString())
            }
        }
    }

    override suspend fun updateAccount(account: Account) {
        withContext(ioDispatcher) {
            accountDao.updateAccount(account)
        }
    }

    override suspend fun deleteAccount(account: Account) {
        withContext(ioDispatcher) {
            accountDao.deleteAccount(account)
        }
    }

    override suspend fun deleteAccounts(): Int {
        return withContext(ioDispatcher) {
            accountDao.deleteAccounts(uid)
        }
    }

    override suspend fun deletePersonalAccounts(): Int {
        return withContext(ioDispatcher) {
            accountDao.deletePersonalAccounts(uid)
        }
    }

    override suspend fun deleteSavedAccounts(): Int {
        return withContext(ioDispatcher) {
            accountDao.deleteSavedAccounts(uid)
        }
    }

    override suspend fun updateRemoteUserInfo(userInfo: UserInfo): Result<Unit> {
        return Result.Error(Exception("RemoteDataSource should be used"))
    }

    override suspend fun getRemoteUserInfo(userId: String ): Result<UserInfo> {
        return Result.Error(Exception("RemoteDataSource should be used"))
    }

    override suspend fun getRemoteUserList(userName: String): Result<List<UserInfo>> {
        return Result.Error(Exception("RemoteDataSource should be used"))
    }

    override suspend fun getRemoteAccountList(
        userId: String,
        accountId: String?
    ): Result<List<Account>> {
        return Result.Error(Exception("RemoteDataSource should be used"))
    }
}