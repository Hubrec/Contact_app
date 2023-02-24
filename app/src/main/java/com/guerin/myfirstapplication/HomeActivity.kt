package com.guerin.myfirstapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class HomeActivity : AppCompatActivity() {

    val RESULT_BACK = 100
    val RESULT_DELETE = 300
    val RESULT_BACK_FAV_PAGE = 500

    var adapter: ContactAdapter? = null

    var listItems = arrayListOf<Contact>()

    val ActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val contact = result.data?.getSerializableExtra("contact") as Contact

            listItems.add(contact)
            adapter?.notifyDataSetChanged()

            Snackbar.make(
                findViewById(android.R.id.content),
                "New contact ${contact.getFullName} added",
                Snackbar.LENGTH_LONG
            ).show()
        }
        else if (result.resultCode == RESULT_BACK) {
            Log.i("HomeActivity", "RESULT_BACK")
            val contact = result.data?.getSerializableExtra("contact") as Contact

            listItems.add(contact)
            listItems.sortBy { it.contactName }
            adapter?.notifyDataSetChanged()
        }
        else if (result.resultCode == RESULT_DELETE) {
            Log.i("HomeActivity", "RESULT_DELETE")
            val contact = result.data?.getSerializableExtra("contact") as Contact

            listItems.remove(contact)
            listItems.sortBy { it.contactName }
            adapter?.notifyDataSetChanged()
        }
        else if (result.resultCode == RESULT_BACK_FAV_PAGE) {
            Log.i("HomeActivity", "RESULT_BACK_FAV_PAGE")
            listItems.clear()
            val newlistItems = result.data?.getSerializableExtra("contacts") as ArrayList<Contact>
            listItems.addAll(newlistItems)
            listItems.sortBy { it.contactName }

            adapter?.notifyDataSetChanged()
        }

        val file = File(filesDir, "contacts.txt")

        FileOutputStream(file).use {
            val outputStream = ObjectOutputStream(it)
            outputStream.writeObject(listItems)
        }
        Log.i("HomeActivity", "File contacts.txt updated")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.i("HomeActivity", "onCreate() called")

        try {
            val file = File(filesDir, "contacts.txt")
            if (file.exists()) {
                Log.i("HomeActivity", "File contacts.txt exists $file")

                FileInputStream(file).use {
                    val inputStream = ObjectInputStream(it)
                    val contacts = inputStream.readObject() as ArrayList<Contact>
                    contacts.forEach() {
                        Log.i("HomeActivity", it.toString())
                    }
                    listItems.addAll(contacts)
                }
            } else {
                Log.i("HomeActivity", "File contacts.txt does not exist")
                openFileOutput("contacts.txt", MODE_PRIVATE).use {
                    it.write("".toByteArray())
                }
            }
        } catch (e: Exception) {
            Log.i("HomeActivity", "Error catch : $e")
        }

        adapter = ContactAdapter(
            this,
            listItems
        )
        listItems.sortBy { it.contactName }
        listViewContacts.adapter = adapter

        buttonContact.setOnClickListener {
            Log.i("HomeActivity", "buttonContact clicked")

            val intent = Intent(this, AddContactActivity::class.java)
            ActivityResultLauncher.launch(intent)
        }

        val filters = arrayOf<String>("Name", "Surname", "Date of birth", "Phone number", "Email", "Gender")
        spinnerContact.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filters)

        spinnerContact.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(applicationContext, "Selected sort : ${filters[position]}", Toast.LENGTH_SHORT).show()

                when (position) {
                    0 -> listItems.sortBy { it.contactName }
                    1 -> listItems.sortBy { it.contactSurname }
                    2 -> listItems.sortBy { it.contactDateOfBirth }
                    3 -> listItems.sortBy { it.contactNumber }
                    4 -> listItems.sortBy { it.contactEmail }
                    5 -> listItems.sortBy { it.contactGender }
                }

                adapter?.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.i("HomeActivity", "Nothing selected on spinner")
            }
        }

        listViewContacts.setOnItemClickListener { parent, view, position, id ->

            val intent = Intent(this, ContactDetailsActivity::class.java)
            intent.putExtra("contact", listItems[position])

            listItems.removeAt(position)
            ActivityResultLauncher.launch(intent)
        }

        editTextSearch.doOnTextChanged() { text, start, before, count ->
            if (text.toString() != "") {
                val filteredList = listItems.filter {
                    it.contactName.contains(text.toString(), true) ||
                    it.contactSurname.contains(text.toString(), true) ||
                    it.contactDateOfBirth.contains(text.toString(), true) ||
                    it.contactNumber.contains(text.toString(), true) ||
                    it.contactEmail.contains(text.toString(), true) ||
                    it.contactGender.toString().contains(text.toString(), true)
                } as ArrayList<Contact>

                adapter = ContactAdapter(
                    this,
                    filteredList
                )
                listViewContacts.adapter = adapter
            } else {
                adapter = ContactAdapter(
                    this,
                    listItems
                )
                listViewContacts.adapter = adapter
            }

            adapter?.notifyDataSetChanged()
        }

        buttonFavPage.setOnClickListener {
            val intent = Intent(this, FavoriActivity::class.java)
            intent.putExtra("contacts", listItems)
            ActivityResultLauncher.launch(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i("HomeActivity", "onPause() called")
    }

    override fun onResume() {
        super.onResume()
        Log.i("HomeActivity", "onResume() called")
    }
}

