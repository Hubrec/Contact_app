package com.guerin.myfirstapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_favori.*
import kotlinx.android.synthetic.main.activity_home.*

class FavoriActivity : AppCompatActivity() {

    val RESULT_BACK = 100
    val RESULT_DELETE = 300
    val RESULT_BACK_FAV_PAGE = 500

    lateinit var adapter: ContactAdapter
    var listItems = arrayListOf<Contact>()
    var listItemsFavorite = arrayListOf<Contact>()

    val ActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_BACK) {
            Log.i("FavoriActivity", "RESULT_BACK")
            val contact = result.data?.getSerializableExtra("contact") as Contact

            if (contact.favorite) {
                listItemsFavorite.add(contact)
            }
            listItems.add(contact)

            listItemsFavorite.sortBy { it.contactName }
            adapter.notifyDataSetChanged()
        }
        else if (result.resultCode == RESULT_DELETE) {
            Log.i("FavoriActivity", "RESULT_DELETE")
            val contact = result.data?.getSerializableExtra("contact") as Contact

            listItems.remove(contact)
            listItemsFavorite.remove(contact)
            listItemsFavorite.sortBy { it.contactName }
            adapter.notifyDataSetChanged()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favori)

        val intent = getIntent()
        listItems = intent.getSerializableExtra("contacts") as ArrayList<Contact>

        for (contact in listItems) {
            if (contact.favorite) {
                listItemsFavorite.add(contact)
            }
        }

        adapter = ContactAdapter(
            this,
            listItemsFavorite
        )
        listViewFavoriContacts.adapter = adapter
        adapter.notifyDataSetChanged()

        buttonBack.setOnClickListener {
            val intent = Intent()
            intent.putExtra("contacts", listItems)
            setResult(RESULT_BACK_FAV_PAGE, intent)
            finish()
        }

        listViewFavoriContacts.setOnItemClickListener { parent, view, position, id ->

            val intent = Intent(this, ContactDetailsActivity::class.java)
            intent.putExtra("contact", listItemsFavorite[position])

            listItems.remove(listItemsFavorite[position])
            listItemsFavorite.removeAt(position)
            ActivityResultLauncher.launch(intent)
        }
    }
}