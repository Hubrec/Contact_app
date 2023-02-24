package com.guerin.myfirstapplication

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ContactAdapter(
    private val context: Context,
    private val dataSource: ArrayList<Contact>
) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val rowView = inflater.inflate(R.layout.item_contact, parent, false)

        val contactName = rowView.findViewById(R.id.ctName) as TextView
        val contactSurname = rowView.findViewById(R.id.ctSurname) as TextView
        val contactImage = rowView.findViewById(R.id.ctImage) as ImageView

        val contact = getItem(position) as Contact

        contactName.text = contact.contactName
        contactSurname.text = contact.contactSurname

        if (contact.hasProfilePicture) {
            contactImage.setImageURI(contact.profilePictureUri)
        }

        return rowView
    }
}