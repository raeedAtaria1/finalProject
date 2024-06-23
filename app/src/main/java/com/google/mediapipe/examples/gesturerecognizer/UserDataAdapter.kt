package com.google.mediapipe.examples.gesturerecognizer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

class UserDataAdapter(private val context: Context, private val userList: List<UserData>, private val currentUserEmail: String) : BaseAdapter() {

    override fun getCount(): Int {
        return userList.size
    }

    override fun getItem(position: Int): Any {
        return userList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_score, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val userData = userList[position]
        viewHolder.textViewFullName.text = "${userData.firstName} ${userData.lastName}"
        viewHolder.textViewPoints.text = userData.points.toString()

        if (userData.email == currentUserEmail) {
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

        return view
    }

    private class ViewHolder(view: View) {
        val textViewFullName: TextView = view.findViewById(R.id.textViewFullName)
        val textViewPoints: TextView = view.findViewById(R.id.textViewPoints)
    }
}
