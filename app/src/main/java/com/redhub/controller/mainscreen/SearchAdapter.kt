package com.redhub.controller.mainscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.redhub.R
import com.redhub.model.ArticleModel
import com.redhub.model.StarModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_search_list.view.*
import kotlinx.android.synthetic.main.layout_star_list.view.*

class SearchAdapter( private val list_search: ArrayList<ArticleModel>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>(){
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val title = itemView.movie_title
        val rate = itemView.movie_rate
        val imguri : ImageView=itemView.ArticleImageView



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_search_list,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = list_search[position].title
        holder.rate.text = list_search[position].rates.toString()
        Picasso.get()
            .load(list_search[position].posterUri)
            .into(holder.imguri)

    }

    override fun getItemCount(): Int {
        return list_search.size
    }

}