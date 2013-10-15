package magic.yuyong.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import magic.yuyong.R;
import magic.yuyong.activity.ProfileActivity;
import magic.yuyong.model.Comment;
import magic.yuyong.model.Repost;
import magic.yuyong.view.AsyncImageView;
import magic.yuyong.view.TwitterContent;

import java.util.ArrayList;
import java.util.List;

public class CommentOrRepostAdapter extends BaseAdapter {
	private List<Object> items;
	private Context mContext;
	private LayoutInflater inflater;

    public static final int TYPE_INVALID = -1;
    public static final int TYPE_COMMENTS = 0;
    public static final int TYPE_REPOSTS = 1;
    private int type = TYPE_INVALID;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Object> getItems() {
		return items;
	}

	public CommentOrRepostAdapter(Context content) {
		super();
		mContext = content;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(List<Object> data) {
       items = data;
       notifyDataSetChanged();
    }

	@Override
	public int getCount() {
		return items == null ? 0 : items.size();
	}

	@Override
	public Object getItem(int arg0) {
		return items.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	private class ViewHolder {
		AsyncImageView user_avatar;
		TextView user_name;
		TextView time;
		TwitterContent text;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object object = items.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.comment_item, null);
			holder = new ViewHolder();
			holder.user_avatar = (AsyncImageView) convertView
					.findViewById(R.id.user_avatar);
			holder.user_avatar.setDefaultImageResource(R.drawable.avatar);
			holder.user_name = (TextView) convertView
					.findViewById(R.id.user_name);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.text = (TwitterContent) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

        if(object instanceof Comment){
            final Comment comment = (Comment)object;
            holder.user_avatar.setUrl(comment.getUser().getProfile_image_url());
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
        }else if(object instanceof Repost){
            final Repost repost = (Repost)object;
            holder.user_avatar.setUrl(repost.getUser().getProfile_image_url());
            holder.user_avatar.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.putExtra("uid", repost.getUser().getId());
                    mContext.startActivity(intent);
                }
            });
            holder.user_name.setText(repost.getUser().getScreen_name());
            holder.time.setText(repost.getCreate_at());
            holder.text.setData(repost.getText());
        }

		return convertView;
	}

}
