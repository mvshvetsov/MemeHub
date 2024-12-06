package ru.shvetsov.memehub.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.shvetsov.memehub.data.models.VideoWithUserInfoModel
import ru.shvetsov.memehub.databinding.ProfileVideoItemBinding

class ProfileVideoAdapter(
    private var videoList: MutableList<VideoWithUserInfoModel>,
    private val onVideoClick: (Int) -> Unit
) : RecyclerView.Adapter<ProfileVideoAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ProfileVideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(video: VideoWithUserInfoModel) {

            Glide.with(binding.root)
                .load(video.thumbnailUrl)
                .into(binding.videoPreview)

            binding.videoPreview.setOnClickListener {
                onVideoClick(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ProfileVideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    fun updateData(newVideoList: List<VideoWithUserInfoModel>) {
        videoList.clear()
        videoList.addAll(newVideoList)
        notifyDataSetChanged()
    }
}