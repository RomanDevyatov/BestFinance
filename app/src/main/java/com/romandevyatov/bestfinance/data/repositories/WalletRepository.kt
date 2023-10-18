package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.WalletDao
import com.romandevyatov.bestfinance.data.entities.Wallet
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val walletDao: WalletDao
) {

    fun getAllWalletLiveData(): LiveData<List<Wallet>> = walletDao.getAllLiveData()

    fun getAllWalletsNotArchivedLiveData(): LiveData<List<Wallet>> = walletDao.getAllNotArchivedLiveData()

    suspend fun insertWallet(wallet: Wallet) {
        walletDao.insert(wallet)
    }

    suspend fun updateWallet(wallet: Wallet) {
        walletDao.update(wallet)
    }

    fun getWalletByNameNotArchivedLiveData(walletName: String): LiveData<Wallet> {
        return walletDao.getByNameNotArchivedLiveData(walletName)
    }

    fun getWalletByNameNotArchived(walletName: String): Wallet {
        return walletDao.getByNameNotArchived(walletName)
    }

    fun getWalletById(id: Long?): Wallet? {
        return walletDao.getById(id)
    }

    fun getWalletByNameLiveData(walletName: String): LiveData<Wallet>? {
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
}
