package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.repositories.TransferHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import com.romandevyatov.bestfinance.utils.localization.Storage
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.coVerify
import io.mockk.coEvery
import io.mockk.just
import io.mockk.runs
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class UpdateTransferHistoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: UpdateTransferHistoryViewModel
    private lateinit var storage: Storage
    private lateinit var transferHistoryRepository: TransferHistoryRepository
    private lateinit var walletRepository: WalletRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        transferHistoryRepository = mockk()
        walletRepository = mockk()

        viewModel = UpdateTransferHistoryViewModel(storage, transferHistoryRepository, walletRepository)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test updateTransferHistoryAndWallets`() = runTest {
        // Mock data
        val transferHistory = TransferHistory(1, 100.0, 1, 2, null, "", null, LocalDateTime.now())
        val walletFrom = Wallet(1, "wallet_mock_1", 200.0, 0.0, 0.0, "", null, "USD")
        val walletTo = Wallet(2, "wallet_mock_2", 100.0, 0.0, 0.0, "", null, "USD")

        // Mock repository behavior
        coEvery { transferHistoryRepository.updateTransferHistory(any()) } just runs
        coEvery { walletRepository.getWalletById(walletFrom.id!!) } returns walletFrom
        coEvery { walletRepository.getWalletById(walletTo.id!!) } returns walletTo
        coEvery { walletRepository.updateWallet(any()) } just runs

        // Call the function under test
        viewModel.updateTransferHistoryAndWallets(transferHistory)

        // Verify the expected behavior using assertions or verifications
        coVerify { transferHistoryRepository.updateTransferHistory(transferHistory) }
        coVerify { walletRepository.getWalletById(walletFrom.id!!) }
        coVerify { walletRepository.getWalletById(walletTo.id!!) }
        coVerify { walletRepository.updateWallet(any()) } // Perform necessary verifications

        assertEquals("", "result")
    }
}
