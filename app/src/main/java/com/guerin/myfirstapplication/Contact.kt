package com.guerin.myfirstapplication

import android.net.Uri
import android.util.Log
import java.util.*

data class Contact(
    var contactName: String,
    var contactSurname: String,
    var contactDateOfBirth: String,
    var contactNumber: String,
    var contactEmail: String,
    var contactGender: GenderType,
    var favorite: Boolean,
    var profilePictureUri: Uri? = null
): java.io.Serializable {

    val getFullName: String get() = "$contactSurname $contactName"

    val isMan: Boolean get() = contactGender == GenderType.MAN

    val isWoman: Boolean get() = contactGender == GenderType.WOMAN

    val simplifiedGender: String get() = if (contactGender == GenderType.MAN) "M." else if (contactGender == GenderType.WOMAN) "Mme" else " unknown"

    val isBirthday : Boolean get() {
        val date = Calendar.getInstance()
        val today = date.get(Calendar.DAY_OF_MONTH).toString()
        val month = if ( date.get(Calendar.MONTH) <= 9 ) ("0" + date.get(Calendar.MONTH)).toString() else date.get(Calendar.MONTH).toString()
        val birth = contactDateOfBirth.split("/")
        Log.i("Contact", "Today is $today/$month and the birth is ${birth[0]}/${birth[1]}")
        return today == birth[0] && month == (birth[1].toInt() - 1).toString()
    }

    val hasProfilePicture : Boolean get() {
        return profilePictureUri != null
    }

    override fun toString(): String {
        return "Contact : $simplifiedGender $contactSurname $contactName \n" +
                "birth : $contactDateOfBirth \n" +
                "number : $contactNumber \n" +
                "email : $contactEmail \n" +
                "favorite : $favorite \n" +
                "profile picture : $profilePictureUri \n"
    }
}

enum class GenderType {
    MAN,
    WOMAN,
    OTHER
}