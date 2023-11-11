package com.romandevyatov.bestfinance.utils

import com.romandevyatov.bestfinance.data.entities.Currency

object Constants {
    const val UNDO_DELAY: Long = 4000L
    const val SPINNER_TO = "spinner_to"
    const val SPINNER_FROM = "spinner_from"
    const val ADD_WALLET_FRAGMENT: String = "add_wallet_fragment"
    const val ADD_TRANSFER_HISTORY_FRAGMENT = "add_transfer_history_fragment"
    const val WALLETS_FRAGMENT: String = "WalletsFragment"
    const val ADD_INCOME_HISTORY_FRAGMENT = "add_income_history_fragment"
    const val ADD_EXPENSE_HISTORY_FRAGMENT = "add_expense_history_fragment"
    const val MENU_WALLET_FRAGMENT = "wallet_fragment"
    const val WALLETS_SETTINGS_FRAGMENT = "wallets_settings_fragment"
    const val DATABASE_NAME = "bestfinance_database"
    const val DEFAULT_DELAY_AFTER_SPOKEN_TEXT: Long = 0
    const val SHOW_DROP_DOWN_DELAY_MS: Long = 30
    const val CLICK_DELAY_MS = 1000L
    const val UNCALLABLE_WORD = "&&Rпизда%<хуй"

    val supportedLocales = listOf("en", "ru")

    private val currencyData = """
        EUR    Euro
        USD	   US Dollar
        JPY    Japanese Yen
        BGN    Bulgarian Lev
        CZK    Czech Republic Koruna
        DKK    Danish Krone
        GBP    British Pound Sterling
        HUF    Hungarian Forint
        PLN    Polish Zloty
        RON    Romanian Leu
        SEK    Swedish Krona
        CHF    Swiss Franc
        ISK    Icelandic Króna
        NOK    Norwegian Krone
        HRK    Croatian Kuna
        RUB    Russian Ruble
        TRY    Turkish Lira
        AUD    Australian Dollar
        BRL    Brazilian Real
        CAD    Canadian Dollar
        CNY    Chinese Yuan
        HKD    Hong Kong Dollar
        IDR    Indonesian Rupiah
        ILS    Israeli New Sheqel
        INR    Indian Rupee
        KRW    South Korean Won
        MXN    Mexican Peso
        MYR    Malaysian Ringgit
        NZD    New Zealand Dollar
        PHP    Philippine Peso
        SGD    Singapore Dollar
        THB    Thai Baht
        ZAR    South African Rand
    """.trimIndent()

    val DEFAULT_CURRENCIES: List<Currency> = currencyData.lines().map { line ->
        val (code, name) = line.trim().split(Regex("\\s+"), 2)
        Currency(code, name)
    }
}
