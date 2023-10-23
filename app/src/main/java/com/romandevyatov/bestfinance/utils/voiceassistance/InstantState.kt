package com.romandevyatov.bestfinance.utils.voiceassistance

import androidx.annotation.StringRes
import com.romandevyatov.bestfinance.R

enum class InputState(@StringRes val settingTextResId: Int) {
    GROUP(R.string.group_setting),
    SUB_GROUP(R.string.sub_group_setting),
    WALLET(R.string.wallet_setting),
    WALLET_FROM(R.string.wallet_from_setting),
    WALLET_TO(R.string.wallet_to_setting),
    AMOUNT(R.string.amount_setting),
    COMMENT(R.string.comment_setting),
    WALLET_NAME(R.string.wallet_name_setting),
    DESCRIPTION(R.string.description_setting),
    SET_BALANCE(R.string.set_balance_setting),
    CONFIRM(R.string.confirm_setting)
}
