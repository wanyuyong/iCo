package magic.yuyong.adapter;

import java.util.List;

import magic.yuyong.R;
import magic.yuyong.activity.ProfileActivity;
import magic.yuyong.activity.ShowPics;
import magic.yuyong.model.Twitter;
import magic.yuyong.util.Debug;
import magic.yuyong.util.StringUtil;
import magic.yuyong.view.AsyncImageView;
import magic.yuyong.view.TwitterContent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TwitterListAdapter extends BaseAdapter {
	private List<Twitter> twitters;
	private Context mContext;
	private LayoutInflater inflater;

	public static final int INVALID_POSTION = -1;
	private int selectedPostion = INVALID_POSTION;

	public TwitterListAdapter(Context content) {
		super();
		mContext = content;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setItemOnSelected(int postion) {
		selectedPostion = postion;
		notifyDataSetChanged();
	}

	public void setData(List<Twitter> twitters) {
		this.twitters = twitters;
		notifyDataSetChanged();
	}

	public List<Twitter> getData() {
		return this.twitters;
	}

	@Override
	public boolean isEmpty() {
		return twitters == null || twitters.size() == 0;
	}

	@Override
	public int getCount() {
		return twitters == null ? 0 : twitters.size();
	}

	@Override
	public Object getItem(int arg0) {
		return twitters == null ? null : twitters.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	public static class ViewHolder {
		public View container;
		public AsyncImageView user_avatar;
		public TextView user_name;
		public TextView time;
		public TwitterContent content;
		public AsyncImageView pic;
		public View divider;
		public TextView origin_user_name;
		public TwitterContent origin_content;
		public AsyncImageView origin_pic;
		public TextView from;
		public TextView repost_num;
		public TextView comment_num;
		public Twitter twitter;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Twitter twitter = twitters.get(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.twitter_list_item, null);
			holder = new ViewHolder();
			holder.container = convertView.findViewById(R.id.container);
			holder.user_avatar = (AsyncImageView) convertView
					.findViewById(R.id.user_avatar);
			holder.user_avatar.setDefaultImageResource(R.drawable.avatar);
			holder.user_name = (TextView) convertView
					.findViewById(R.id.user_name);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.content = (TwitterContent) convertView
					.findViewById(R.id.content);
			holder.pic = (AsyncImageView) convertView.findViewById(R.id.pic);
			holder.divider = convertView.findViewById(R.id.divider);
			holder.origin_user_name = (TextView) convertView
					.findViewById(R.id.origin_user_name);
			holder.origin_content = (TwitterContent) convertView
					.findViewById(R.id.origin_content);
			holder.origin_pic = (AsyncImageView) convertView
					.findViewById(R.id.origin_pic);
			holder.from = (TextView) convertView.findViewById(R.id.from);
			holder.repost_num = (TextView) convertView
					.findViewById(R.id.repost_num);
			holder.comment_num = (TextView) convertView
					.findViewById(R.id.comment_num);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.twitter = twitter;
		if (selectedPostion == position) {
			holder.container
					.setBackgroundResource(R.color.twitter_item_bg_pressed);
		} else {
			holder.container.setBackgroundResource(R.drawable.twitter_item_bg);
		}

		String avatar_url = twitter.getUser().getProfile_image_url();
		holder.user_avatar.setUrl(avatar_url);
		holder.user_avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ProfileActivity.class);
				intent.putExtra("uid", twitter.getUser().getId());
				mContext.startActivity(intent);
			}
		});
		holder.user_name.setText(twitter.getUser().getScreen_name());
		holder.time.setText(twitter.getCreated_at());
		holder.content.setData(twitter.getText());
		holder.pic.setVisibility(View.GONE);
		String pic_url = twitter.getThumbnail_pic();
		if (!StringUtil.isEmpty(pic_url)) {
			holder.pic.setVisibility(View.VISIBLE);
			holder.pic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					showPic(twitter.getBmiddle_pic(),
							twitter.getOriginal_pic(), twitter.getPic_urls());
				}
			});
			holder.pic.setUrl(pic_url);
		}
		holder.origin_content.setVisibility(View.GONE);
		holder.divider.setVisibility(View.GONE);
		holder.origin_user_name.setVisibility(View.GONE);
		holder.origin_pic.setVisibility(View.GONE);

		if (twitter.getOrigin() != null) {
			holder.origin_content.setVisibility(View.VISIBLE);
			holder.divider.setVisibility(View.VISIBLE);
			holder.origin_user_name.setVisibility(View.VISIBLE);
			String origin_content = twitter.getOrigin().getText();
			holder.origin_content.setData(origin_content);
			if (!twitter.getOrigin().isDeleted()) {
				holder.origin_user_name.setText(twitter.getOrigin().getUser()
						.getScreen_name());
			} else {
				holder.origin_user_name.setText("Unknow");
			}
			String origin_pic_url = twitter.getOrigin().getThumbnail_pic();
			if (!StringUtil.isEmpty(origin_pic_url)) {
				holder.origin_pic.setVisibility(View.VISIBLE);
				holder.origin_pic.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						showPic(twitter.getOrigin().getBmiddle_pic(), twitter
								.getOrigin().getOriginal_pic(), twitter
								.getOrigin().getPic_urls());
					}
				});
				holder.origin_pic.setUrl(origin_pic_url);
			}
		}

		holder.from.setText(mContext.getResources().getString(
				R.string.text_source)
				+ twitter.getSource());
		holder.comment_num.setText(twitter.getComments_count() + " "
				+ mContext.getResources().getString(R.string.label_comment));
		holder.repost_num.setText(twitter.getReposts_count() + " "
				+ mContext.getResources().getString(R.string.label_repost));
		return convertView;
	}

	private void showPic(String url, String originalUrl, String[] pics) {
		Intent showPic = new Intent(mContext, ShowPics.class);
		showPic.putExtra("url", url);
		showPic.putExtra("originalUrl", originalUrl);
		showPic.putExtra("pics", pics);
		mContext.startActivity(showPic);
	}

}
