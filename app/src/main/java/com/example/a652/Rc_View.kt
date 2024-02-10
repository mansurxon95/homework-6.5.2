package com.example.a652

import android.annotation.SuppressLint
import android.graphics.RenderEffect.createBlurEffect
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.a652.databinding.ItemRcBinding

class Rc_View(var onClik: OnClik): ListAdapter<User, Rc_View.VH>(MyDiffUtill()) {

    inner class VH(var itemview : ItemRcBinding): RecyclerView.ViewHolder(itemview.root){

        fun onBind(user: User,position: Int){

            itemview.itemName.text = user.lastFirst_name
            itemview.itemTelNum.text = user.tel_number
            itemview.itemImage.setImageURI(user.image)



            itemview.itemBtn.setOnClickListener {
                onClik.edit(user,position,itemview.itemBtn.rootView)
            }
            itemview.viewBtn.setOnClickListener {
                onClik.view(user, position)
            }



        }

    }

    class MyDiffUtill: DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return  oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.equals(newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRcBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.onBind(item,position)
    }

    interface OnClik{
        fun edit(contact: User,position: Int,view: View){

        }

        fun view(contact: User,position: Int){

        }
    }


}