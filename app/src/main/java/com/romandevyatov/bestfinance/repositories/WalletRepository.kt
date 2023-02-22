package com.romandevyatov.bestfinance.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.WalletDao
import com.romandevyatov.bestfinance.db.entities.Wallet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val walletDao: WalletDao
) {

    fun getAllWallets(): LiveData<List<Wallet>> = walletDao.getAll()

    fun getAllWalletsByIsArchived(isArchived: Int): LiveData<List<Wallet>> = walletDao.getAllByIsArchived(0)

    suspend fun insertWallet(wallet: Wallet) {
        walletDao.insert(wallet)
    }

    suspend fun deleteWallet(wallet: Wallet) {
        walletDao.delete(wallet)
    }

    suspend fun updateWallet(wallet: Wallet) {
        walletDao.update(wallet)
    }

}
