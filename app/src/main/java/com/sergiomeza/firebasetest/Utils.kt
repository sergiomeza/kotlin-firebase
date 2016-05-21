package com.sergiomeza.firebasetest

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog

/**
* Created by Sergio Meza el 5/21/16.
*/
class Utils(val mContext: Context) {

    val mUrl = "https://apptestserg.firebaseio.com/" // CAMBIAR POR SU APP

    fun showProgress() : MaterialDialog {
        return MaterialDialog.Builder(mContext)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();
    }

    fun confirmDialog(mContent: Int, mTitle: Int): MaterialDialog {
        return MaterialDialog.Builder(mContext)
                .title(mTitle)
                .content(mContent)
                .positiveText(R.string.positive)
                .show();
    }

    fun showCustom(layout: Int, mTitle: Int): MaterialDialog {
        return MaterialDialog.Builder(mContext)
                .title(mTitle)
                .customView(layout, true)
                .positiveText(R.string.positive)
                .show();
    }
}