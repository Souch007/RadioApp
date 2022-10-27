package com.coderoids.radio.ui.ui.settings.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.coderoids.radio.R
import com.coderoids.radio.ui.ui.settings.SettingsData
import org.json.JSONArray

class AdapterSettings(private val mSettingsList: MutableLiveData<ArrayList<SettingsData>>?) : RecyclerView.Adapter<AdapterSettings.MyViewHodler>(){
    private var listener: ((Int,View) -> Unit)? = null
    inner class MyViewHodler(view: View) : RecyclerView.ViewHolder(view){
        var heading = view.findViewById<TextView>(R.id.tv_heading)
        var subHeading = view.findViewById<TextView>(R.id.tv_sub_heading)
        var description = view.findViewById<TextView>(R.id.tv_description_wifi)
        var switch = view.findViewById<SwitchCompat>(R.id.swich_state_wifi)
    }

    fun itemClickListener(l: (Int,View) -> Unit) {
        listener = l
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHodler {
        return MyViewHodler(LayoutInflater.from(parent.context).inflate(R.layout.item_settings, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHodler, position: Int) {
        val settingsData = mSettingsList!!.value!!.get(position)
        if(!settingsData.mainHeading.matches("".toRegex())) {
            holder.heading.visibility = View.VISIBLE
            holder.heading.text = settingsData.mainHeading
        } else
            holder.heading.visibility = View.GONE

        if(!settingsData.title.matches("".toRegex())) {
            holder.subHeading.visibility = View.VISIBLE
            holder.subHeading.text = settingsData.title
        } else
            holder.subHeading.visibility = View.GONE

        if(!settingsData.description.matches("".toRegex())) {
            holder.description.visibility = View.VISIBLE
            holder.description.text = settingsData.description
        } else
            holder.description.visibility = View.GONE

        if(settingsData.isSwitchAval){
            holder.switch.visibility = View.VISIBLE
            holder.switch.isChecked = settingsData.switchState
        } else {
            holder.switch.visibility = View.GONE
        }
        holder.subHeading.setOnClickListener {view->
            listener?.let {
                it(position,view)
            }
        }
    }

    override fun getItemCount(): Int {
        return mSettingsList!!.value!!.size;
    }

    fun changemodetext(mode:String) {
        this.mSettingsList?.value?.get(0)?.description=mode
        notifyDataSetChanged()

    }
}