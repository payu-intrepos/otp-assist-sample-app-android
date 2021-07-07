package com.payu.otp_assist_sample

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class SaveCardAdapter(var storedCards: ArrayList<StoredCard?>, var cardSelected: CardSelected) :
    RecyclerView.Adapter<SaveCardAdapter.SaveCardViewHolder>() {
    var mSelectedPosition: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveCardViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_save_card_item, null, false)
        return SaveCardViewHolder(view)
    }

    fun updateData(storedCards: ArrayList<StoredCard?>) {
        this.storedCards = storedCards
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return storedCards?.size ?: 0
    }

    override fun onBindViewHolder(holder: SaveCardViewHolder, position: Int) {
        holder.maskedCardNumber?.text = storedCards[holder.adapterPosition]?.maskedCardNumber
        holder.radioSelector?.isChecked = position == mSelectedPosition
        holder.etCVV?.visibility = if (position == mSelectedPosition) View.VISIBLE else View.GONE
        holder.etCVV?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                cardSelected.onCVVEntered(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }


    inner class SaveCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var maskedCardNumber: TextView? = null
        var etCVV: EditText? = null
        var radioSelector: RadioButton? = null

        init {
            maskedCardNumber = itemView.findViewById(R.id.cardnumber)
            etCVV = itemView.findViewById(R.id.cvv)
            radioSelector = itemView.findViewById(R.id.radioSelector)
            radioSelector?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    var copyOfLastCheckedPosition = mSelectedPosition
                    mSelectedPosition = adapterPosition
                    cardSelected.onCardSelected(storedCards.get(mSelectedPosition))
                    notifyItemChanged(mSelectedPosition)
                    notifyItemChanged(copyOfLastCheckedPosition)
                }
            })
        }
    }
}