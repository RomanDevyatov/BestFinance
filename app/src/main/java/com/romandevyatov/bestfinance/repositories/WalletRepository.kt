package com.romandevyatov.bestfinance.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.WalletDao
import com.romandevyatov.bestfinance.db.entities.Wallet
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WalletRepository @Inject constructor(
    private val walletDao: WalletDao
) {

    fun getAllWallets(): LiveData<List<Wallet>> = walletDao.getAll()

    fun getAllNotArchivedWalletsLiveData(): LiveData<List<Wallet>> = walletDao.getAllWhereArchivedDateIsNull()

    suspend fun insertWallet(wallet: Wallet) {
        walletDao.insert(wallet)
    }

    suspend fun deleteWallet(wallet: Wallet) {
        walletDao.delete(wallet)
    }

    suspend fun updateWallet(wallet: Wallet) {
        walletDao.update(wallet)
    }

    fun getNotArchivedWalletByNameLiveData(walletName: String): LiveData<Wallet> {
        return walletDao.getNotArchivedWalletByName(walletName)
    }

}
