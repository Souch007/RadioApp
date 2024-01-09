//package com.netcast.radio.download.adapter
//
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.downloader.OnDownloadListener
//import com.netcast.radio.R
//import com.downloader.OnProgressListener;
//import com.downloader.PRDownloader
//import com.downloader.Progress
//
//val mutableMapOfIDToProgressListenerCallback: MutableMap<Int, MyDownloadProgressListener> =
//    mutableMapOf()
//
//class DownloadEpisodeAdapter2(private val dataSet: List<ListItem>) :
//    RecyclerView.Adapter<DownloadEpisodeAdapter2.ViewHolder>() {
//
//    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        lateinit var listItem: ListItem
//
//        val percentDownloaded = itemView.findViewById<TextView?>(R.id.textView)
//        fun setUpdateViewLambda() {
//            mutableMapOfIDToProgressListenerCallback[listItem.id]?.setLabmda {
//                percentDownloaded?.text = toPercent(it).toString()
//            }
//        }
//
//        fun removeUpdateViewLambda() {
//            mutableMapOfIDToProgressListenerCallback[listItem.id]?.setLabmda {}
//        }
//
//        init {
//            view.setOnClickListener { //to demonstrate downloads
//                Thread { //use coroutines instead
//                    downloadItem(
//                        listItem,
//                        adapterPosition,
//                        this@DownloadEpisodeAdapter2,
//                        "https://downloads.gradle-dn.com/distributions/gradle-6.9.1-bin.zip", //insert your download here
//                        view.context.getExternalFilesDir(null)!!.absolutePath,
//                        "a${listItem.id}.zip"
//                    )
//                }.start()
//            }
//        }
//    }
//
//    override fun onViewAttachedToWindow(holder: ViewHolder) {
//        holder.setUpdateViewLambda()
//        super.onViewAttachedToWindow(holder)
//    }
//
//    override fun onViewDetachedFromWindow(holder: ViewHolder) {
//        holder.removeUpdateViewLambda() //remove callback so that recycled viewholder doesn't undeservingly get updates
//        super.onViewDetachedFromWindow(holder)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(
//            LayoutInflater
//                .from(parent.context)
//                .inflate(R.layout.download_row, parent, false)
//        )
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val listItem = dataSet[position]
//
//        //perform your binding
//
//        holder.listItem = listItem
//        holder.percentDownloaded?.text = listItem.percentDownloaded.toString()
//        holder.setUpdateViewLambda()
//    }
//
//    override fun getItemCount(): Int {
//        return dataSet.size
//    }
//}
//
//data class ListItem(
//    val id: Int, //a value that uniquely represents this list item
//    var percentDownloaded: Byte //can only ever be between 0-100, so no need for Int
//)
//
//class MyDownloadProgressListener : OnProgressListener {
//    lateinit var listItem: ListItem
//    private var updateView: (progress: Progress) -> Unit = {}
//    fun setLabmda(lambda: (progress: Progress) -> Unit) {
//        updateView = lambda
//    }
//
//    override fun onProgress(progress: Progress?) {
//        Log.d("Adapter", "Progress object: $progress")
//        if (progress != null) {
//            val toPercent = toPercent(progress)
//            Log.d("Adapter", "Percent received: $toPercent")
//            listItem.percentDownloaded = toPercent
//            updateView(progress)
//        }
//    }
//}
//
//fun downloadItem(
//    listItem: ListItem,
//    adapterPosition: Int,
//    adapter: DownloadEpisodeAdapter2,
//    url: String,
//    dirPath: String,
//    filename: String
//) {
//    val listener = MyDownloadProgressListener()
//    mutableMapOfIDToProgressListenerCallback[listItem.id] = listener
//    listener.listItem = listItem
//    Handler(Looper.getMainLooper()).post { adapter.notifyItemChanged(adapterPosition)/*bind listener to ViewHolder*/ }
//
//    PRDownloader
//        .download(url, dirPath, filename)
//        .build()
//        .setOnProgressListener(listener)
//        .setOnCancelListener {
//            mutableMapOfIDToProgressListenerCallback.remove(listItem.id)
//        }
//        .start(object : OnDownloadListener {
//            override fun onDownloadComplete() {
//                mutableMapOfIDToProgressListenerCallback.remove(listItem.id)
//            }
//
//            override fun onError(error: com.downloader.Error?) {
//                mutableMapOfIDToProgressListenerCallback.remove(listItem.id)
//            }
//
//        }
//        )
//}
//
//fun toPercent(progress: Progress) =
//    (100 * (progress.currentBytes / progress.totalBytes.toDouble())).toInt().toByte()