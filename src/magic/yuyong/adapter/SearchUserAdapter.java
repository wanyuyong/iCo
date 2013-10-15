//package magic.yuyong.adapter;
//
//import java.util.List;
//
//import magic.yuyong.R;
//import magic.yuyong.interf.GetImg;
//import magic.yuyong.model.SearchUser;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//public class SearchUserAdapter extends BaseAdapter{
//	private List<SearchUser> users;
//	private Context mContext;
//	private LayoutInflater inflater;
//	private GetImg getImg;
//	
//	public GetImg getGetImg() {
//		return getImg;
//	}
//
//	public void setGetImg(GetImg getImg) {
//		this.getImg = getImg;
//	}
//
//	public SearchUserAdapter(Context content) {
//		super();
//		mContext = content;
//		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	}
//	
//	public void changeData(List<SearchUser> users){
//		this.users = users;
//		notifyDataSetChanged();
//	}
//
//	@Override
//	public int getCount() {
//		return users ==  null ? 0 : users.size();
//	}
//
//	@Override
//	public Object getItem(int arg0) {
//		return users ==  null ? null : users.get(arg0);
//	}
//
//	@Override
//	public long getItemId(int arg0) {
//		return users ==  null ? 0 : users.get(arg0).getId();
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		View view = null;
//		final SearchUser user = users.get(position);
//		if(convertView == null){
//			view = inflater.inflate(R.layout.search_user_item, null);
//		}else{
//			view = convertView;
//		}
//		TextView user_name = (TextView)view.findViewById(R.id.user_name);
//		user_name.setText(user.getScreen_name());
//		TextView followers = (TextView) view.findViewById(R.id.followers);
//		followers.setText("Followers : "+user.getFollowers_count());
//		return view;
//	}
//
//}
