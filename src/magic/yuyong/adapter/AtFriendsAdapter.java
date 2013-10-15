package magic.yuyong.adapter;

import java.util.List;

import magic.yuyong.R;
import magic.yuyong.model.AtUser;
import magic.yuyong.util.StringUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AtFriendsAdapter extends BaseAdapter{
	private List<AtUser> friends;
	private Context mContext;
	private LayoutInflater inflater;
	

	public AtFriendsAdapter(Context content) {
		super();
		mContext = content;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void addData(List<AtUser> friends){
		if(this.friends == null){
			this.friends = friends;
		}else{
			this.friends.addAll(friends);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return friends ==  null ? 0 : friends.size();
	}

	@Override
	public Object getItem(int arg0) {
		return friends ==  null ? null : friends.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	
   class ViewHolder{
		TextView user_name;
		TextView remark;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final AtUser friend = friends.get(position);
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.at_friend_item, null);
			holder = new ViewHolder();
			holder.user_name = (TextView)convertView.findViewById(R.id.user_name);
			holder.remark = (TextView)convertView.findViewById(R.id.remark);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.user_name.setText(friend.getNickname());
		String remark = friend.getRemark();
		holder.remark.setText(StringUtil.isEmpty(remark) ? "" : "("+remark+")");
		return convertView;
	}

}
