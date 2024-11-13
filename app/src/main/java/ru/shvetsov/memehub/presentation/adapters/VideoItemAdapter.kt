package ru.shvetsov.memehub.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.shvetsov.memehub.data.models.VideoItem
import ru.shvetsov.memehub.databinding.VideoItemBinding

class VideoItemAdapter(
    private val videoList: List<VideoItem>
) : RecyclerView.Adapter<VideoItemAdapter.ViewHolder>() {

    class ViewHolder(private val binding: VideoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}