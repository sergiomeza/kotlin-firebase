package com.sergiomeza.firebasetest.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sergiomeza.firebasetest.R
import com.sergiomeza.firebasetest.model.Movies
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater
import kotlin.properties.Delegates

/**
* Created by Sergio Meza el 5/21/16.
*/
class MainAdapter() : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    //SE INICIALIZA LA LISTA VACIA
    var items: MutableList<Movies> by
        Delegates.observable(mutableListOf(), { prop, old, new -> notifyDataSetChanged() })

    //LISTENER PARA EL LLAMADO DESDE LA ACTIVIDAD
    var onItemClickListener: ((Int) -> Unit)? = null

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        val v = parent.context.layoutInflater.inflate(R.layout.movie_item, parent, false)
        return ViewHolder(v, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(items[position])
    }

    fun getItemAt(mPos: Int) = items[mPos]

    //ELIMINAR
    fun removeItemByObject(mMovie: Movies){
        val mPosition = items.indexOfFirst { it.name == mMovie.name }
        items.removeAt(mPosition)
        notifyItemRemoved(mPosition)
    }

    //AGREGAR
    fun add(mMovie: Movies){
        items.add(mMovie)
        notifyDataSetChanged()
    }

    //ACTUALIZAR
    fun update(mMovie: Movies){
        val mPosition = items.indexOfFirst { it.name == mMovie.name }
        items[mPosition] = mMovie
        notifyItemChanged(mPosition)
    }

    class ViewHolder(itemView: View, var onItemClickListener: ((Int) -> Unit)?) :
            RecyclerView.ViewHolder(itemView) {
        var mImage: ImageView = itemView.find(R.id.mImage)
        var mTxtName: TextView =  itemView.find(R.id.mTxtName)
        var mTxtDirector : TextView = itemView.find(R.id.mTxtDirector)
        val mImageDel : ImageView = itemView.find(R.id.mImageDelete)

        fun bindHolder(mMovie: Movies){
            mTxtName.text = mMovie.name
            mTxtDirector.text = mMovie.director
            Picasso.with(itemView.context).load(mMovie.url).centerCrop().fit().into(mImage)

            mImageDel.setOnClickListener {
                onItemClickListener?.invoke(adapterPosition)
            }
        }
    }
}