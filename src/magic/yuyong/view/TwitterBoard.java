package magic.yuyong.view;

import java.util.ArrayList;
import java.util.List;

import magic.yuyong.R;
import magic.yuyong.activity.TwitterShowActivity;
import magic.yuyong.model.Twitter;
import magic.yuyong.transformation.CircleTransformation;
import magic.yuyong.util.ColorUtil;
import magic.yuyong.util.DisplayUtil;
import magic.yuyong.util.StringUtil;
import magic.yuyong.view.TwitterBoardScrollView.TwitterBoardScrollListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * @author wanyuyong
 * 
 */
public class TwitterBoard extends ViewGroup implements
		TwitterBoardScrollListener {
	
	private boolean aequilate = true;

	private static final int ROWS = 2;

	private static final int NARROW_TEXT_MAX_LEN = 25;

	private static final int TYPE_ONLY_TEXT = 0;
	private static final int TYPE_AVATAR_TEXT = 1;

	private final float scale_wide_w = .95f;
	private final float scale_top = .1f;
	private final float scale_bottom = .088f;

	private int tile_h;
	private int wide_tile_w;
	private int narrow_tile_w;

	private int gap_tile = 6;
	private int gap_top;
	private int gap_bottom;
	private int flip_board_width;

	private boolean needCalculate = true;

	private List<Tile> tiles = new ArrayList<TwitterBoard.Tile>();
	private List<View> reuseView = new ArrayList<View>();

	private OnFlipListener mOnFlipListener;
	private TwitterBoardScrollView parent;
	private BoundaryListener mBoundaryListener;

	private int[] right;
	private int[] top;

	public int getTwittersCount() {
		return tiles.size();
	}

	public BoundaryListener getBoundaryListener() {
		return mBoundaryListener;
	}

	public void setBoundaryListener(BoundaryListener mBoundaryListener) {
		this.mBoundaryListener = mBoundaryListener;
	}

	class Tile {
		public View tileView;
		public Twitter twitter;
		public Rect rect;
		public boolean isAttached;
		public int color;
		public int type = -1;
	}

	public static interface BoundaryListener {
		void toTheBeginning();
		void toTheEnd();
	}
	
	public static interface OnFlipListener {
		void onFlip(int flip_board_width, int flip);
	}

	private OnClickListener onTileClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ViewHolder holder = (ViewHolder) (v.getTag());
			Twitter twitter = holder.tile.twitter;
			Intent intent = new Intent(getContext(), TwitterShowActivity.class);
			intent.putExtra("twitter", twitter);
			getContext().startActivity(intent);
		}
	};

	public void setOnFlipListener(OnFlipListener mOnFlipListener) {
		this.mOnFlipListener = mOnFlipListener;
	}

	public TwitterBoard(Context context) {
		super(context);
		init();
	}

	public TwitterBoard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TwitterBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		gap_tile = (int) DisplayUtil.dpToPx(getResources(), gap_tile);
		if (gap_tile % 2 != 0) {
			gap_tile++;
		}
	}

	public void setScrollView(TwitterBoardScrollView parent) {
		this.parent = parent;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (needCalculate) {
			needCalculate = false;

			DisplayMetrics dm = new DisplayMetrics();
			dm = getResources().getDisplayMetrics();
			int screenWidth = dm.widthPixels;

			wide_tile_w = (int) (screenWidth * scale_wide_w);
			narrow_tile_w = (wide_tile_w - gap_tile) / 2;
			gap_top = (int) (h * scale_top);
			gap_bottom = (int) (h * scale_bottom);
			tile_h = (h - gap_top - gap_bottom - (ROWS - 1) * gap_tile) / ROWS;
			reset();
		}
	}

	private void reset() {
		right = new int[ROWS];
		for (int i = 0; i < ROWS; i++) {
			right[i] = 0;
		}
		top = new int[ROWS];
		for (int i = 0; i < ROWS; i++) {
			top[i] = gap_top + (gap_tile + tile_h) * i;
		}
		flip_board_width = 0;
	}

	public void addData(List<Twitter> newTwitters) {

		for (int index = 0; index < newTwitters.size(); index++) {
			Twitter twitter = newTwitters.get(index);
			Tile tile = new Tile();
			tile.twitter = twitter;
			tile.color = ColorUtil.getRandomColor();

			int l = right[0], t, r, b;
			int indicator = 0;
			for (int i = 0; i < ROWS; i++) {
				if (l > right[i]) {
					l = right[i];
					indicator = i;
				}
			}

			l += gap_tile;
			t = top[indicator];
			b = t + tile_h;
			if (aequilate || tile.twitter.getText().length() > NARROW_TEXT_MAX_LEN) {
				r = l + wide_tile_w;
			} else {
				r = l + narrow_tile_w;
			}
			right[indicator] = r;

			tile.rect = new Rect(l, t, r, b);
			tile.type = tile.rect.width() == wide_tile_w ? TYPE_AVATAR_TEXT
					: TYPE_ONLY_TEXT;
			tiles.add(tile);
			if (Rect.intersects(getFlipViewRect(parent), tile.rect)) {
				attachTile(tile, true);
			}
		}

		for (int i = 0; i < ROWS; i++) {
			if (flip_board_width < right[i]) {
				flip_board_width = right[i];
			}
		}
		flip_board_width += gap_tile;
		requestLayout();

	}

	private void attachTile(final Tile tile, boolean toRight) {
		tile.isAttached = true;
		boolean[] reuse = new boolean[1];
		final View view = getTileView(tile, reuse);
		view.setBackgroundDrawable(getBackgroundDrawable(tile.color));
		tile.tileView = view;
		if (reuse[0]) {
			attachViewToParent(view, toRight ? -1 : 0, generateDefaultLayoutParams());
		} else {
			addViewInLayout(view, toRight ? -1 : 0, generateDefaultLayoutParams(), true);
		}
		final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(
				tile.rect.width(), MeasureSpec.EXACTLY);
		final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(
				tile.rect.height(), MeasureSpec.EXACTLY);

		view.measure(widthMeasureSpec, heightMeasureSpec);
		view.layout(tile.rect.left, tile.rect.top, tile.rect.right,
				tile.rect.bottom);
	}

	private void unAttachTile(Tile tile) {
		View view = tile.tileView;
		detachViewFromParent(view);
		tile.isAttached = false;
		tile.tileView = null;
		reuseView.add(view);
	}

	private Rect getFlipViewRect(View parent) {
		return new Rect(parent.getScrollX(), 0, parent.getScrollX()
				+ parent.getWidth(), parent.getHeight());
	}

	public void refresh() {
		parent.scrollTo(0, 0);
		removeAllViews();
		tiles.clear();
		reset();
	}

	class ViewHolder {
		Tile tile;
		ImageView avatar;
		TextView name;
		TextView msg;
		ImageView type;
		View has_pic;
		TextView comments_count;
	}

	private View getTileView(Tile tile, boolean[] reuse) {
		View view = reuseView.size() == 0 ? null : reuseView.remove(0);
		ViewHolder holder = null;
		if (view == null) {
			reuse[0] = false;
			view = inflate(getContext(), R.layout.tile, null);
			holder = new ViewHolder();
			holder.avatar = (ImageView) view.findViewById(R.id.avatar);
			holder.name = (TextView) view.findViewById(R.id.user_name);
			holder.msg = (TextView) view.findViewById(R.id.msg);
			holder.type = (ImageView) view.findViewById(R.id.type);
			holder.has_pic = view.findViewById(R.id.has_pic);
			holder.comments_count = (TextView) view
					.findViewById(R.id.comments_count);
			view.setTag(holder);
		} else {
			reuse[0] = true;
			holder = (ViewHolder) view.getTag();
		}
		holder.tile = tile;

		Twitter twitter = tile.twitter;
		if (!twitter.isDeleted()) {
			holder.name.setText(twitter.getUser().getScreen_name());
			holder.msg.setText(twitter.getText());
			holder.type
					.setImageResource(twitter.getOrigin() == null ? R.drawable.social_send_now
							: R.drawable.social_forward);
			boolean hasPic = (!StringUtil.isEmpty(twitter.getBmiddle_pic()) || (twitter
					.getOrigin() != null && !StringUtil.isEmpty(twitter
					.getOrigin().getBmiddle_pic())));
			holder.has_pic
					.setVisibility(hasPic ? View.VISIBLE : View.INVISIBLE);
			holder.comments_count.setText(String.valueOf(twitter
					.getComments_count()));
		}

		if (tile.type == TYPE_AVATAR_TEXT) {
			holder.avatar.setVisibility(View.VISIBLE);
			if (!twitter.isDeleted()) {
				Picasso.with(getContext()).load(twitter.getUser().getProfile_image_url()).placeholder(R.drawable.avatar).transform(new CircleTransformation()).into(holder.avatar);
			}
		} else if (tile.type == TYPE_ONLY_TEXT) {
			holder.avatar.setVisibility(View.GONE);
		}

		view.setOnClickListener(onTileClickListener);
		return view;
	}

	private Drawable getBackgroundDrawable(final int color) {
		StateListDrawable drawable = new StateListDrawable();
		drawable.addState(new int[] { android.R.attr.state_pressed },
				new Drawable() {

					@Override
					public void setColorFilter(ColorFilter cf) {
					}

					@Override
					public void setAlpha(int alpha) {
					}

					@Override
					public int getOpacity() {
						return 0;
					}

					@Override
					public void draw(Canvas canvas) {
						canvas.drawColor((color & 0X00FFFFFF) | 0XA0000000);
					}
				});

		drawable.addState(new int[] { -android.R.attr.state_pressed },
				new Drawable() {

					@Override
					public void setColorFilter(ColorFilter cf) {
					}

					@Override
					public void setAlpha(int alpha) {
					}

					@Override
					public int getOpacity() {
						return 0;
					}

					@Override
					public void draw(Canvas canvas) {
						canvas.drawColor(color);
					}
				});
		return drawable;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(MeasureSpec.makeMeasureSpec(flip_board_width,
				MeasureSpec.EXACTLY), heightMeasureSpec);
	}

	@Override
	public void onTwitterBoardScrollChanged(int l, int t, int oldl, int oldt,
			TwitterBoardScrollView parent) {
		if (mOnFlipListener != null && flip_board_width != 0) {
			mOnFlipListener.onFlip(flip_board_width, l);
		}

		if (l + parent.getWidth() == flip_board_width
				&& mBoundaryListener != null) {
			mBoundaryListener.toTheEnd();
		} else if (l == 0 && mBoundaryListener != null) {
			mBoundaryListener.toTheBeginning();
		}

		Rect viewRect = getFlipViewRect(parent);
		boolean toRight = l > oldl;
		for (Tile tile : tiles) {
			if (!tile.isAttached && Rect.intersects(viewRect, tile.rect)) {
				attachTile(tile, toRight);
			} else if (tile.isAttached && !Rect.intersects(viewRect, tile.rect)) {
				unAttachTile(tile);
			}
		}

	}

}
