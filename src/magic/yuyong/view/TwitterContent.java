package magic.yuyong.view;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import magic.yuyong.R;
import magic.yuyong.activity.NewPostActivity;
import magic.yuyong.activity.ProfileActivity;
import magic.yuyong.util.DisplayUtil;
import magic.yuyong.util.FaceUtil;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class TwitterContent extends View implements OnGestureListener {

	private String text;

	private List<Block> blocks = new ArrayList<Block>();
	private List<PaintBlock> paintBlocks = new ArrayList<TwitterContent.PaintBlock>();
	private GestureDetector mGesture;
	private PaintBlock catchPaintBlock;
	private boolean action;

	private float gap;
	private float textSize;
	private int textColor;
	private float line_height;
	private float ascent;
	private float descent;
	private final static int DEFAUT_GAP = 2;
	private final static int DEFAUT_TEXT_SIZE = 13;
	private final static int DEFAUT_TEXT_COLOR = 0XFF333333;

	public boolean isAction() {
		return action;
	}

	public void setAction(boolean action) {
		this.action = action;
	}

	public TwitterContent(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public TwitterContent(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		int defaut_text_size = DisplayUtil.sp2px(DEFAUT_TEXT_SIZE,
				getResources().getDisplayMetrics().scaledDensity);
		int defaut_text_gap = DisplayUtil.sp2px(DEFAUT_GAP, getResources()
				.getDisplayMetrics().scaledDensity);
		mGesture = new GestureDetector(this);
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.TwitterContent);
			textSize = a.getDimension(R.styleable.TwitterContent_textSize,
					defaut_text_size);
			gap = a.getDimension(R.styleable.TwitterContent_textGap,
					defaut_text_gap);
			textColor = a.getColor(R.styleable.TwitterContent_textColor,
					DEFAUT_TEXT_COLOR);
		} else {
			textSize = defaut_text_size;
			gap = defaut_text_gap;
			textColor = DEFAUT_TEXT_COLOR;
		}
		Paint paint = new Paint();
		paint.setTextSize(textSize);
		FontMetrics fm = paint.getFontMetrics();
		line_height = fm.descent - fm.ascent;
		ascent = fm.ascent;
		descent = fm.descent;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if (width != 0) {
			measureBlocks(width);
			if(paintBlocks.size() != 0){
				PaintBlock paintBolock = paintBlocks.get(paintBlocks.size() - 1);
				height = (int) (paintBolock.y + Math
						.ceil(paintBolock.belongBlock.paint.getFontMetrics().descent));
			}else{
				height = 0;
			}
		}
		setMeasuredDimension(width, height);
	}

	private void measureBlocks(int width) {
		float x = 0, y = line_height;
		paintBlocks.clear();
		for (Block block : blocks) {
			PaintBlock paintBlock = null;
			if (block.type == Block.TYPE_FACE) {
				if (x + line_height > width) {
					y += line_height + gap;
					x = 0;
				}
				paintBlock = new PaintBlock(x, y, block);
				paintBlock.start = block.start;
				paintBlock.end = block.end + 1;
				paintBlocks.add(paintBlock);
				x += line_height;
				continue;
			}
			for (int i = block.start; i <= block.end; i++) {
				if (paintBlock == null) {
					paintBlock = new PaintBlock(x, y, block);
					paintBlock.start = i;
					paintBlock.end = i;
					paintBlocks.add(paintBlock);
				}
				char c = text.charAt(i);
				x += block.paint.measureText(String.valueOf(c));
				if (x > width) {
					y += line_height + gap;
					x = 0;
					paintBlock = null;
					i--;
				} else {
					paintBlock.end++;
				}
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (PaintBlock paintBlock : paintBlocks) {
			if (paintBlock.belongBlock.type == Block.TYPE_FACE) {
				Drawable drawable = getResources().getDrawable(FaceUtil.getFaceDrawableID(
						text.substring(paintBlock.start+1, paintBlock.end-1)));
				int left = (int) paintBlock.x;
				int top = (int) (paintBlock.y + ascent);
				int bottom = (int)(paintBlock.y + descent);
				drawable.setBounds(left, top, (int) (left + line_height), bottom);
				drawable.draw(canvas);
			} else {
				canvas.drawText(text, paintBlock.start, paintBlock.end,
						paintBlock.x, paintBlock.y,
						paintBlock.belongBlock.paint);
			}
		}
	}

	public void setData(String text) {
		this.text = text;
		blocks.clear();
		makeBlocks();
		requestLayout();
	}

	class PaintBlock {
		Block belongBlock;
		float x, y;
		int start, end;

		public PaintBlock(float x, float y, Block belongBlock) {
			super();
			this.x = x;
			this.y = y;
			this.belongBlock = belongBlock;
		}
	}

	class Block {
		int type;
		int start, end;
		Paint paint;

		public static final int TYPE_TEXT = 0;
		public static final int TYPE_URL = 1;
		public static final int TYPE_FACE = 2;
		public static final int TYPE_AT = 3;
		public static final int TYPE_TOPIC = 4;

		public Block(int type) {
			super();
			this.type = type;
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextSize(textSize);
			switch (type) {
			case TYPE_TEXT:
				paint.setColor(textColor);
				break;
			case TYPE_URL:
				paint.setColor(0XFF99CCFF);
				break;
			case TYPE_FACE:
				paint.setColor(0XFFCC3399);
				break;
			case TYPE_AT:
				paint.setColor(0XFFFF99CC);
				break;
			case TYPE_TOPIC:
				paint.setColor(0XFF669900);
				break;
			}
		}
	}

	private int[] getBlockState(int start) {
		char c = text.charAt(start);
		int type = Block.TYPE_TEXT;
		int end = start;
		if (c == 'h') {
			Pattern pattern = Pattern.compile("http://[0-9A-Za-z]");
			int len = "http://".length();
			if (text.length() >= start + len + 1
					&& pattern.matcher(text.substring(start, start + len + 1))
							.matches()) {
				type = Block.TYPE_URL;
				for (int i = start + len; i < text.length(); i++) {
					char inner = text.charAt(i);
					Pattern p = Pattern.compile("[0-9A-Za-z/.//]");
					if (i == text.length() - 1) {
						end = i;
					} else if (!p.matcher(String.valueOf(inner)).matches()) {
						end = i - 1;
						break;
					}
				}
			}
		} else if (c == '[') {
			StringBuffer sb = new StringBuffer();
			for (int i = start + 1; i < text.length(); i++) {
				char inner = text.charAt(i);
				if (inner == ']') {
					break;
				} else {
					sb.append(inner);
				}
			}
			for (String str : FaceUtil.faceStrs) {
				if (str.equals(sb.toString())) {
					type = Block.TYPE_FACE;
					end = start + str.length() + 1;
					break;
				}
			}
		} else if (c == '@') {
			Pattern p = Pattern.compile("[_A-Za-z0-9-\u4e00-\u9fa5]");
			if (text.length() > start + 1
					&& p.matcher(String.valueOf(text.charAt(start + 1)))
							.matches()) {
				type = Block.TYPE_AT;
				for (int i = start + 1; i < text.length(); i++) {
					char inner = text.charAt(i);
					if (!p.matcher(String.valueOf(inner)).matches()
							|| inner == ' ') {
						end = i - 1;
						break;
					} else if (i == text.length() - 1) {
						end = i;
					}
				}
			}
		} else if (c == '#') {
			for (int i = start + 1; i < text.length(); i++) {
				char inner = text.charAt(i);
				if (inner == '#') {
					type = Block.TYPE_TOPIC;
					end = i;
					break;
				}
			}
		}

		if (type == Block.TYPE_TEXT) {
			for (int i = start + 1; i < text.length(); i++) {
				char inner = text.charAt(i);
				if (inner == 'h' || inner == '[' || inner == '#'
						|| inner == '@') {
					end = i - 1;
					break;
				} else if (i == text.length() - 1) {
					end = i;
				}
			}
		}
		return new int[] { type, end };
	}

	private void makeBlocks() {
		int i = 0;
		while (i < text.length()) {
			int[] state = getBlockState(i);
			Block block = new Block(state[0]);
			block.start = i;
			block.end = state[1];
			blocks.add(block);
			i = block.end + 1;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean flag = false;
		if (mGesture != null && action) {
			flag = mGesture.onTouchEvent(event);
		} else {
			flag = super.onTouchEvent(event);
		}
		if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {
			catchPaintBlock = null;
		}
		return flag;
	}

	@Override
	public boolean onDown(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		for (PaintBlock paintBlock : paintBlocks) {
			if (paintBlock.belongBlock.type == Block.TYPE_TEXT
					|| paintBlock.belongBlock.type == Block.TYPE_FACE) {
				continue;
			}
			RectF rect = new RectF();
			rect.left = paintBlock.x;
			float len = paintBlock.belongBlock.paint.measureText(text
					.substring(paintBlock.start, paintBlock.end));
			rect.right = rect.left + len;
			FontMetrics fm = paintBlock.belongBlock.paint.getFontMetrics();
			rect.top = paintBlock.y + fm.ascent;
			rect.bottom = rect.top + line_height;
			if (rect.contains(x, y)) {
				catchPaintBlock = paintBlock;
			}
		}
		if (catchPaintBlock != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		if (null == catchPaintBlock) {
			return false;
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		if (null == catchPaintBlock) {
			return false;
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		if (catchPaintBlock != null) {
			Block block = catchPaintBlock.belongBlock;
			String text = this.text.substring(block.start, block.end + 1);
			Intent intent = null;
			if (block.type == Block.TYPE_AT) {
				intent = new Intent(getContext(), ProfileActivity.class);
				intent.putExtra("screen_name", text.substring(1));
			} else if (block.type == Block.TYPE_TOPIC) {
				intent = new Intent(getContext(), NewPostActivity.class);
				intent.putExtra("#", text);
			} else if (block.type == Block.TYPE_URL) {
				Uri url = Uri.parse(text);
				intent = new Intent(Intent.ACTION_VIEW, url);
			}
			getContext().startActivity(intent);
			return true;
		} else {
			return false;
		}
	}

}
