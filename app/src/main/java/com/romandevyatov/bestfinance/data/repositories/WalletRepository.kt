package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.WalletDao
import com.romandevyatov.bestfinance.data.entities.WalletEntity
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository
@Inject
constructor(private val walletDao: WalletDao) {

    fun getAllWalletLiveData(): LiveData<List<WalletEntity>> = walletDao.getAllLiveData()

    fun getAllWalletsNotArchivedLiveData(): LiveData<List<WalletEntity>> = walletDao.getAllNotArchivedLiveData()

    suspend fun insertWallet(walletEntity: WalletEntity) {
        walletDao.insert(walletEntity)
    }

    suspend fun updateWallet(walletEntity: WalletEntity) {
        walletDao.update(walletEntity)
    }

    fun getWalletByNameNotArchivedLiveData(walletName: String): LiveData<WalletEntity?> {
        return walletDao.getByNameNotArchivedLiveData(walletName)
    }

    fun getWalletByNameNotArchived(walletName: String): WalletEntity? {
        return walletDao.getByNameNotArchived(walletName)
    }

    suspend fun getWalletByIdAsync(id: Long?): WalletEntity? {
        return walletDao.getById(id)
    }

    fun getWalletByNameLiveData(walletName: String): LiveData<WalletEntity?> {
        return walletDao.getByNameLiveData(walletName)
    }

    suspend fun deleteWalletById(id: Long?) {
        walletDao.deleteById(id)
    }

    fun unarchiveWalletById(id: Long?) {
        walletDao.updateArchivedDateById(id, null)
    }

    fun archiveWalletById(id: Long?, date: LocalDateTime) {
        walletDao.updateArchivedDateById(id, date)
    }

    fun getWalletByIdLiveData(id: Long): LiveData<WalletEntity?> {
        return walletDao.getByIdLiveData(id)
    }
}
