package com.guerin.myfirstapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_contact_details.*
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream

class ContactDetailsActivity : AppCompatActivity() {

    val RESULT_BACK = 100
    val RESULT_DELETE = 300

    val ActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            contact = result.data?.getSerializableExtra("contact") as Contact

            textFullName.text = contact?.getFullName
            textDateOfBirth.text = contact?.contactDateOfBirth
            textPhone.text = contact?.contactNumber
            textEmail.text = contact?.contactEmail
            textGender.text = contact?.contactGender.toString()

            if (contact?.favorite == true) {
                buttonAddToFav.background = getDrawable(R.drawable.favorite_filled)
            } else {
                buttonAddToFav.background = getDrawable(R.drawable.favorite_empty)
            }
        }
    }

    var contact: Contact? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)

        Log.i("ContactDetailsActivity", "onCreate() called")

        val intent = getIntent()
        contact = intent.getSerializableExtra("contact") as Contact

        textFullName.text = contact?.getFullName
        textDateOfBirth.text = contact?.contactDateOfBirth
        textPhone.text = contact?.contactNumber
        textEmail.text = contact?.contactEmail
        textGender.text = contact?.contactGender.toString()

        if (contact?.favorite == true) {
            buttonAddToFav.background = getDrawable(R.drawable.favorite_filled)
        }

        buttonBack.setOnClickListener {
            val intent = Intent()
            intent.putExtra("contact", contact)
            setResult(RESULT_BACK, intent)
            finish()
        }

        buttonEdit.setOnClickListener {

            val intent = Intent(this, AddContactActivity::class.java)
            intent.putExtra("contact", contact)
            ActivityResultLauncher.launch(intent)
        }

        buttonDelete.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure you want to delete this contact ?")
            builder.setPositiveButton("Yes") { dialog, which ->
                val intent = Intent()
                intent.putExtra("contact", contact)
                setResult(RESULT_DELETE, intent)
                finish()

                Toast.makeText(applicationContext, "Contact deleted", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        buttonAddToFav.setOnClickListener {
            if (contact?.favorite == true) {
                buttonAddToFav.background = getDrawable(R.drawable.favorite_empty)
                contact?.favorite = false
            } else {
                buttonAddToFav.background = getDrawable(R.drawable.favorite_filled)
                contact?.favorite = true
            }
        }

    }

    override fun onPause() {
        super.onPause()
        Log.i("ContactDetailsActivity", "onPause() called")
    }

    override fun onResume() {
        super.onResume()
        Log.i("ContactDetailsActivity", "onResume() called")
    }
}