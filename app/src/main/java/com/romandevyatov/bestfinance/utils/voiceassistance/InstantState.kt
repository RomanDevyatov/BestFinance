package com.romandevyatov.bestfinance.utils.voiceassistance

enum class InputState(val setText: String) {
    GROUP("Set group."),
    SUB_GROUP("Set subgroup."),
    WALLET("Set wallet."),
    WALLET_FROM("Set wallet from."),
    WALLET_TO("Set wallet to."),
    AMOUNT("Set amount."),
    COMMENT("Set comment."),
    SET_BALANCE("Set wallet balance."),
    CONFIRM("Confirm transaction (Yes/No)")
}