package com.example.music

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.NonDisposableHandle.parent

class MusicAdapter(private val context: Context, private val musicArr : ArrayList<MusicModal>) : RecyclerView.Adapter<MusicAdapter.viewHolder>() {

    class viewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.music_title)
        val album = itemView.findViewById<TextView>(R.id.music_album)
        val duration = itemView.findViewById<TextView>(R.id.music_duration)
        val imageIcon = itemView.findViewById<ImageView>(R.id.imageIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return musicArr.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentItems = musicArr[position]

        Glide.with(context)
            .load(currentItems.icon)
            .into(holder.imageIcon)
        holder.title.text = currentItems.title
        holder.album.text = currentItems.album
        holder.duration.text = formatDuration(currentItems.duration)

        // passing the intent value

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MusicItem::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicAdapter")
            ContextCompat.startActivity(context,intent, null)

        }

    }
}
