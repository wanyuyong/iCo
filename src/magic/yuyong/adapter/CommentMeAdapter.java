package magic.yuyong.adapter;

import java.util.List;

import magic.yuyong.R;
import magic.yuyong.activity.ProfileActivity;
import magic.yuyong.model.Comment;
import magic.yuyong.transformation.CircleTransformation;
import magic.yuyong.view.TwitterContent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class CommentMeAdapter extends BaseAdapter {
	private List<Comment> comments;
	private Context mContext;
	private LayoutInflater inflater;

    public static final int INVALID_POSTION = -1;
    private int selectedPostion = INVALID_POSTION;

	public List<Comment> getComments() {
		return comments;
	}

    public void setItemOnSelected(int postion){
        selectedPostion = postion;
        notifyDataSetChanged();
    }

	public CommentMeAdapter(Context content) {
		super();
		mContext = content;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addData(List<Comment> comments) {
		if (this.comments == null) {
			this.comments = comments;
		} else {
			this.comments.addAll(comments);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return comments == null ? 0 : comments.size();
	}

	@Override
	public Object getItem(int arg0) {
		return comments == null ? null : comments.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	private class ViewHolder {
		ImageView user_avatar;
		TextView user_name;
		TextView time;
		TwitterContent text;
		TwitterContent tag_text;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Comment comment = comments.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.comment_me_item, null);
			holder = new ViewHolder();
			holder.user_avatar = (ImageView) convertView
					.findViewById(R.id.user_avatar);
			holder.user_name = (TextView) convertView
					.findViewById(R.id.user_name);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.text = (TwitterContent) convertView.findViewById(R.id.text);
			holder.tag_text = (TwitterContent) convertView
					.findViewById(R.id.tag_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

        if(selectedPostion == position){
            convertView.setBackgroundResource(R.color.twitter_item_bg_pressed);
        }else{
            convertView.setBackgroundResource(R.drawable.twitter_item_bg);
        }

        Picasso.with(mContext).load(comment.getUser().getProfile_image_url()).placeholder(R.drawable.avatar).transform(new CircleTransformation()).into(holder.user_avatar);
		holder.user_avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ProfileActivity.class);
				intent.putExtra("uid", comment.getUser().getId());
				mContext.startActivity(intent);
			}
		});
		holder.user_name.setText(comment.getUser().getScreen_name());
		holder.time.setText(comment.getCreate_at());
		holder.text.setData(comment.getText());
		String tag_content = null;
		if (comment.getReply_comment() != null) {
			tag_content = comment.getReply_comment().getText();
		} else if (comment.getTwitter() != null) {
			tag_content = comment.getTwitter().getText();
		}
		holder.tag_text.setData(tag_content);
		return convertView;
	}

}
