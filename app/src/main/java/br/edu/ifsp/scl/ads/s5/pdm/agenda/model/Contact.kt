package br.edu.ifsp.scl.ads.s5.pdm.agenda.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact (
    val name: String = "",
    val userId: String = "",
    var telephone: String = "",
    var email: String = ""
): Parcelable