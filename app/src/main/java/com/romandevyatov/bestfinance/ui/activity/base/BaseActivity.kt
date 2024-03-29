package com.romandevyatov.bestfinance.ui.activity.base

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.romandevyatov.bestfinance.BudgetApplication
import com.romandevyatov.bestfinance.utils.localization.LocaleUtil
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage

open class BaseActivity: AppCompatActivity() {

    private lateinit var oldPrefLocaleCode : String
    protected val storage : Storage by lazy {
        (application as BudgetApplication).storage
    }

    private fun resetTitle() {
        try {
            val label = packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA).labelRes;
            if (label != 0) {
                setTitle(label);
            }
        } catch (_: PackageManager.NameNotFoundException) {}
    }

    override fun attachBaseContext(base: Context) {
        oldPrefLocaleCode = Storage(base).getPreferredLocale()
        applyOverrideConfiguration(LocaleUtil.getLocalizedConfiguration(oldPrefLocaleCode))
        super.attachBaseContext(base)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetTitle()
    }

    override fun onResume() {
        val currentLocaleCode = Storage(this).getPreferredLocale()
        if(oldPrefLocaleCode != currentLocaleCode){
            recreate() //locale is changed, restart the activty to update
            oldPrefLocaleCode = currentLocaleCode
        }
        super.onResume()
    }
}
