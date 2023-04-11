package com.example.mp3playerondbpro
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mp3playerondbpro.databinding.ItemRecyclerBinding
import java.text.SimpleDateFormat

class MusicRecyclerAdapter (val context : Context, val musicList: MutableList<MusicData>) : RecyclerView.Adapter<MusicRecyclerAdapter.CustomViewHolder>() {

    val ALBUM_IMAGE_SIZE = 90

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int = musicList.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val binding = holder.binding
        // Image(Bitmap), artist, title, duration 가져오기
        val bitmap = musicList.get(position).getAlbumBitmap(context,ALBUM_IMAGE_SIZE)
        if(bitmap != null) {
            binding.ivAlbumArt.setImageBitmap(bitmap)
        } else {
            binding.ivAlbumArt.setImageResource(R.drawable.music_video_24)
        }
        binding.tvArtist.text = musicList.get(position).artist
        binding.tvTitle.text = musicList.get(position).title
        binding.tvDuration.text = SimpleDateFormat("mm:ss").format(musicList.get(position).duration)
        when(musicList.get(position).likes){
            0 -> binding.ivItemLike.setImageResource(R.drawable.favorite_empty_24)
            1 -> binding.ivItemLike.setImageResource(R.drawable.favorite_like_24)
        }
        // 아이템 항목 클릭시 PlayActivity로 MusicData 전달
        binding.root.setOnClickListener {
            val intent = Intent(context, PlayActivity::class.java)
            val parcelableList : ArrayList<Parcelable>? = musicList as ArrayList<Parcelable>
            intent.putExtra("parcelableList", parcelableList)
            intent.putExtra("position", position)
            context.startActivity(intent)
        }
        binding.ivItemLike.setOnClickListener {
            when(musicList.get(position).likes){
                0 -> {
                    musicList.get(position).likes = 1
                    binding.ivItemLike.setImageResource(R.drawable.favorite_like_24)
                }
                1 -> {
                    musicList.get(position).likes = 0
                    binding.ivItemLike.setImageResource(R.drawable.favorite_empty_24)
                }
            }
            val db = DBOpenHelper(context,MainActivity.DB_NAME,MainActivity.VERSION)
            var errorFlag = db.updateLike(musicList.get(position))
            if(errorFlag){
                Toast.makeText(context,"updateLike 에러 발생", Toast.LENGTH_SHORT).show()
            } else{
                this.notifyDataSetChanged()
            }
        }
    }

    inner class CustomViewHolder(val binding : ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root)
}