package sk.stuba.mobv_team_7.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_post.view.*
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.home.PostDto
import sk.stuba.mobv_team_7.http.DATE_FORMAT_POST
import java.text.SimpleDateFormat

class PostsAdapter(
    var posts: List<PostDto>,
    val listener: (PostDto) -> Unit
) : RecyclerView.Adapter<PostsAdapter.ItemPostViewHolder>() {

    private val dateFormat = SimpleDateFormat(DATE_FORMAT_POST)

    inner class ItemPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                listener(posts[adapterPosition])
            }
        }
    }

    // called when the recyclerview needs a new view holder
    // for example: if user scrolls and another item rise recycled and it needs to create
    // new item which is visible
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostViewHolder {
        // we never want tp attach inflated layout to root (parent)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ItemPostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    // binds data to our items (posts); data to view
    override fun onBindViewHolder(holder: ItemPostViewHolder, position: Int) {
        holder.itemView.apply {
            title.text = posts[position].username
            description.text = dateFormat.format(posts[position].created)
        }
    }

}