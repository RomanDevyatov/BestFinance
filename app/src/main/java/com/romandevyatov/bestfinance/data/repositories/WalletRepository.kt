package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.WalletDao
import com.romandevyatov.bestfinance.data.entities.Wallet
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WalletRepository @Inject constructor(
    private val walletDao: WalletDao
) {

    fun getAllWalletLiveData(): LiveData<List<Wallet>> = walletDao.getAllLiveData()

    fun getAllWalletsArchivedLiveData(): LiveData<List<Wallet>>? = walletDao.getAllArchivedLiveData()

    fun getAllWalletsNotArchivedLiveData(): LiveData<List<Wallet>> = walletDao.getAllNotArchivedLiveData()

    suspend fun insertWallet(wallet: Wallet) {
        walletDao.insert(wallet)
    }

    suspend fun deleteWallet(wallet: Wallet) {
        walletDao.delete(wallet)
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

    fun getWalletById(id: Long?): Wallet {
        return walletDao.getById(id)
    }

    fun getWalletByNameLiveData(walletName: String): LiveData<Wallet>? {
        return walletDao.getByNameLiveData(walletName)
    }

    fun getWalletArchivedByNameLiveData(walletName: String): LiveData<Wallet>? {
        return walletDao.getByNameArchivedLiveData(walletName)
    }

    suspend fun deleteWalletById(id: Long?) {
        walletDao.deleteById(id)
    }

    fun unarchiveWalletById(id: Long?) {
        walletDao.unarchiveById(id)
    }



}
