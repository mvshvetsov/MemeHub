package ru.shvetsov.memehub.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.shvetsov.memehub.data.models.VideoWithUserInfoModel
import ru.shvetsov.memehub.databinding.VideoPlayerItemBinding
class VideoPagerAdapter(
    private val videos: MutableList<VideoWithUserInfoModel>,
) : RecyclerView.Adapter<VideoPagerAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(
        private val binding: VideoPlayerItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        var player: ExoPlayer? = null

        init {
            binding.playerView.setOnClickListener {
                player?.let {
                    it.playWhenReady = !it.isPlaying
                }
            }
        }

        fun bind(video: VideoWithUserInfoModel) {
            binding.descriptionTextView.text = video.description
            binding.tagTextView.text = video.tag

            Glide.with(binding.root.context)
                .load(video.profilePicture)
                .circleCrop()
                .into(binding.profileImage)

            binding.usernameTextView.text = video.username

            player = ExoPlayer.Builder(binding.root.context).build().apply {
                val mediaItem = MediaItem.fromUri(video.videoUrl)
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }

            binding.playerView.player = player

            player?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        player?.seekTo(0)
                        player?.playWhenReady = true
                    }
                }
            })
        }

        fun preloadNextVideo(nextVideo: VideoWithUserInfoModel) {
            val nextPlayer = ExoPlayer.Builder(binding.root.context).build().apply {
                val mediaItem = MediaItem.fromUri(nextVideo.videoUrl)
                setMediaItem(mediaItem)
                prepare()
            }
            nextPlayer.release()
        }

        fun releasePlayer() {
            player?.release()
            player = null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding =
            VideoPlayerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        holder.releasePlayer()
        holder.bind(video)

        if (position < videos.size - 1) {
            val nextVideo = videos[position + 1]
            holder.preloadNextVideo(nextVideo)
        }
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        holder.releasePlayer()
    }

    fun addVideos(newVideos: List<VideoWithUserInfoModel>) {
        val startPosition = videos.size
        videos.addAll(newVideos)
        notifyItemRangeInserted(startPosition, newVideos.size)
    }
}
