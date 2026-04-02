package com.altankoc.beuverse.core.utils

import android.content.Context
import com.altankoc.beuverse.R

fun String.toDepartmentName(context: Context): String {
    return when (this) {
        "MUHENDISLIK_FAKULTESI" -> context.getString(R.string.dept_muhendislik)
        "TIP_FAKULTESI" -> context.getString(R.string.dept_tip)
        "IKTISADI_IDARI_BILIMLER" -> context.getString(R.string.dept_iktisadi)
        "FEN_FAKULTESI" -> context.getString(R.string.dept_fen)
        "DIS_HEKIMLIGI_FAKULTESI" -> context.getString(R.string.dept_dis_hekimligi)
        "ILAHIYAT_FAKULTESI" -> context.getString(R.string.dept_ilahiyat)
        "SAGLIK_BILIMLERI_FAKULTESI" -> context.getString(R.string.dept_saglik)
        "ILETISIM_FAKULTESI" -> context.getString(R.string.dept_iletisim)
        "SPOR_BILIMLERI_FAKULTESI" -> context.getString(R.string.dept_spor)
        "DIGER" -> context.getString(R.string.dept_diger)
        else -> this
    }
}