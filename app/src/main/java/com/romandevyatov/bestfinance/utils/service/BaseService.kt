package com.romandevyatov.bestfinance.utils.service

import android.app.Service
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import androidx.annotation.CallSuper
import com.romandevyatov.bestfinance.BudgetApplication
import com.romandevyatov.bestfinance.utils.localization.LocaleUtil
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage

abstract class BaseService : Service() {
    protected val myApp : BudgetApplication by lazy {
        (application as BudgetApplication)
    }

    protected val storage : Storage by lazy {
        myApp.storage
    }

    @CallSuper   //forcing to call the base method to be localized
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LocaleUtil.applyLocalizedContext(baseContext, Storage(this).getPreferredLocale())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun getResources(): Resources {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            super.getResources()
        } else {
            //before Android PIE we should override resources also
            LocaleUtil.getLocalizedResources(super.getResources(), Storage(this).getPreferredLocale())
        }
    }
}
