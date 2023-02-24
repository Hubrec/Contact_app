package com.guerin.myfirstapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_contact.*
import java.io.File
import java.util.*

class AddContactActivity : AppCompatActivity() {

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                imageViewProfile.setImageURI(uri)
            }
        }
    }

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { imageViewProfile.setImageURI(uri) }
    }

    private var latestTmpUri: Uri? = null

    private var buttonFavChecked = false

    private var isOnEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        if (getIntent().hasExtra("contact")) {
            isOnEdit = true

            val intent = getIntent()
            var contact = intent.getSerializableExtra("contact") as Contact

            editTextName.setText(contact.contactName)
            editTextSurname.setText(contact.contactSurname)
            editTextEmail.setText(contact.contactEmail)
            editTextPhone.setText(contact.contactNumber)
            editTextDate.setText(contact.contactDateOfBirth)
            date_Picker.updateDate(contact.contactDateOfBirth.substring(6).toInt(), contact.contactDateOfBirth.substring(3, 5).toInt(), contact.contactDateOfBirth.substring(0, 2).toInt())
            if (contact.favorite) {
                buttonAddToFav.background = getDrawable(R.drawable.favorite_filled)
                buttonFavChecked = true
            }

            if (contact.isMan) {
                radioButtonMen.isChecked = true
            } else if (contact.isWoman) {
                radioButtonWomen.isChecked = true
            } else {
                radioButtonOther.isChecked = true
            }
        }

        buttonLoadPicture.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Load picture")
            builder.setMessage("Take a picture or lead a picture from your gallery ?")
            builder.setPositiveButton("Take a picture") { dialog, which ->
                takeImage()
            }
            builder.setNegativeButton("Load a picture") { dialog, which ->
                selectImageFromGallery()
            }
            builder.show()
        }

        buttonCancel.setOnClickListener() {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Cancel")
            builder.setMessage("Are you sure you want to cancel ?")
            builder.setPositiveButton("Yes") { dialog, which ->
                Toast.makeText(applicationContext, "Return to main menu", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
            builder.setNegativeButton("No") { dialog, which ->
                Toast.makeText(applicationContext, "Stay on contact page", Toast.LENGTH_SHORT)
                    .show()
            }
            builder.show()
        }

        buttonSubmit.setOnClickListener {
            if (!radioButtonMen.isChecked && !radioButtonWomen.isChecked && !radioButtonOther.isChecked) {
                Snackbar.make(
                    findViewById(R.id.buttonSubmit),
                    "Gender not selected",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            else if (editTextName.text.toString().isEmpty()) {
                Snackbar.make(
                    findViewById(R.id.buttonSubmit),
                    "Name not valid",
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (editTextSurname.text.toString().isEmpty()) {
                Snackbar.make(
                    findViewById(R.id.buttonSubmit),
                    "Surname not valid",
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (editTextDate.text.toString().isEmpty()) {
                Snackbar.make(
                    findViewById(R.id.buttonSubmit),
                    "Date not valid",
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (!(Patterns.PHONE).matcher(editTextPhone.text.toString()).matches()) {
                Snackbar.make(
                    findViewById(R.id.buttonSubmit),
                    "Phone number not valid",
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (!(Patterns.EMAIL_ADDRESS).matcher(editTextEmail.text.toString()).matches()) {
                Snackbar.make(
                    findViewById(R.id.buttonSubmit),
                    "Email not valid",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {

                val newContact = Contact(
                    editTextName.text.toString(),
                    editTextSurname.text.toString(),
                    editTextDate.text.toString(),
                    editTextPhone.text.toString(),
                    editTextEmail.text.toString(),
                    if (radioButtonMen.isChecked) GenderType.MAN else if (radioButtonWomen.isChecked) GenderType.WOMAN else GenderType.OTHER,
                    if (buttonFavChecked) true else false,
                    null
                )

                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Validation")
                builder.setMessage(
                    "Are you sure you want to validate ?\n" +
                    "The current contact is : \n\n" +
                    "${newContact.toString()}"
                )
                builder.setPositiveButton("Yes") { dialog, which ->
                    Toast.makeText(applicationContext, "Contact added", Toast.LENGTH_SHORT)
                        .show()

                    if (isOnEdit) {
                        val intent = Intent(this, ContactDetailsActivity::class.java)
                        intent.putExtra("contact", newContact)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("contact", newContact)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
                builder.setNegativeButton("No") { dialog, which ->
                    Toast.makeText(applicationContext, "Back to contact page", Toast.LENGTH_SHORT)
                        .show()
                }
                builder.show()
            }
        }

        radioButtonMen.setOnClickListener() {
            imageView.setImageResource(R.drawable.profile_men)
        }
        radioButtonWomen.setOnClickListener() {
            imageView.setImageResource(R.drawable.profile_women)
        }
        radioButtonOther.setOnClickListener() {
            imageView.setImageResource(R.drawable.profile_other)
        }

        val datePicker = findViewById<DatePicker>(R.id.date_Picker)
        val today = Calendar.getInstance()

        if (today.get(Calendar.MONTH) < 10 && today.get(Calendar.DAY_OF_MONTH) < 10) {
            editTextDate.setText("0${today.get(Calendar.DAY_OF_MONTH)}/0${today.get(Calendar.MONTH)}/${today.get(Calendar.YEAR)}")
        } else if (today.get(Calendar.MONTH) < 10) {
            editTextDate.setText("${today.get(Calendar.DAY_OF_MONTH)}/0${today.get(Calendar.MONTH)}/${today.get(Calendar.YEAR)}")
        } else if (today.get(Calendar.DAY_OF_MONTH) < 10) {
            editTextDate.setText("0${today.get(Calendar.DAY_OF_MONTH)}/${today.get(Calendar.MONTH)}/${today.get(Calendar.YEAR)}")
        } else {
            editTextDate.setText("${today.get(Calendar.DAY_OF_MONTH)}/${today.get(Calendar.MONTH)}/${today.get(Calendar.YEAR)}")
        }

        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH) )
        { view, year, month, day ->
            val month = month + 1
            if (month < 10 && day < 10) {
                editTextDate.setText("0$day/0$month/$year")
            } else if (month < 10) {
                editTextDate.setText("$day/0$month/$year")
            } else if (day < 10) {
                editTextDate.setText("0$day/$month/$year")
            } else {
                editTextDate.setText("$day/$month/$year")
            }
        }

        buttonAddToFav.setOnClickListener {
            if (buttonFavChecked) {
                buttonFavChecked = false
                buttonAddToFav.background = getDrawable(R.drawable.favorite_empty)
            } else {
                buttonFavChecked = true
                buttonAddToFav.background = getDrawable(R.drawable.favorite_filled)
            }
        }
    }

    private fun takeImage() {
        getTmpFileUri().let { uri ->
            latestTmpUri = uri
            takeImageResult.launch(uri)
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

    override fun onResume() {
        super.onResume()
        Log.i("AddContactActivity", "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.i("AddContactActivity", "onPause() called")
    }
}