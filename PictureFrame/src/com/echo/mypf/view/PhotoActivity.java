package com.echo.mypf.view;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DigitalClock;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.echo.mypf.R;
import com.echo.mypf.adapter.BrightnessAdapter;
import com.echo.mypf.calendar.CalendarCard;
import com.echo.mypf.calendar.CalendarCard.OnCellClickListener;
import com.echo.mypf.calendar.CalendarViewAdapter;
import com.echo.mypf.calendar.CustomDate;
import com.echo.mypf.custom.CustomActivity;
import com.echo.mypf.custom.Echo;
import com.echo.mypf.receiver.AlarmReceiver;
import com.echo.mypf.utils.AlarmUtils;
import com.echo.mypf.utils.BrightnessUitls;
import com.echo.mypf.utils.DateTimeUtils;
import com.echo.mypf.utils.FileUtils;
import com.echo.mypf.utils.ImageUtils;
import com.echo.mypf.utils.InfoUtils;
import com.echo.mypf.utils.LanguageUtils;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
@SuppressLint("HandlerLeak")
public class PhotoActivity extends CustomActivity implements ViewFactory,
		OnClickListener, OnCellClickListener {

	private String tag = "PhotoActivity";
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private int brightness;
	private int transition;
	private boolean split;
	private int interval;
	private boolean shuffle;
	private boolean photo_info;
	private boolean photo_fit;
	private Context context;
	private int SOC;
	private String pathType;
	private int position;
	private MyHandler mHandler = new MyHandler();
	private ViewHolder viewHolder = new ViewHolder();
	private ArrayList<String> imgPathList = new ArrayList<String>();
	private ImageSwitcher ivsFit, ivsGalleryMain, ivsGalleryTop, ivsGalleryMid,
			ivsGalleryBottom;
	private RelativeLayout rlSetings, rlDateTime, rlTextClock, rlPhotoFit, rlPhotoFill,
			rlGallery;
	private Animation animationIn;
	private Animation animationOut;
	private boolean playFlag = true;
	private Timer timer;
	private PopupWindow pop;
	private int setitemWidth = 380;
	private int setitemHeight = 65;
	private int popParams = ViewGroup.LayoutParams.WRAP_CONTENT;
	private boolean FLAG_LEFT = true;
	private boolean FLAG_RIGHT = true;
	private boolean SETTINGS_SHOWING = false;
	private boolean CALENDAR_SHOWING = false;
	private boolean TIME_SHOWING = false;
	private CalendarViewAdapter<CalendarCard> adapter;
	private int mCurrentIndex = 498;
	private SlideDirection mDirection = SlideDirection.NO_SLIDE;
	private CalendarCard[] mShowViews;
	private TextView monthText;
	private ViewPager mViewPager;
	private ScrollView scroll;
	private HorizontalScrollView hscroll;
	private LinearLayout mLayout;
	private ImageView ivFill;
	private int MONTH_SET;
	private int DAY_SET;
	private int YEAR_SET;
	private int HOUR_SET;
	private int MINUTE_SET;
	private String AM_SET = "AM";
	private TextView tvMonthSet;
	private TextView tvDaySet;
	private TextView tvYearSet;
	private TextView tvHourSet;
	private TextView tvMinuteSet;
	private TextView tvAmSet;
	private String SWITCH_AlARM = "On";
	private int HOUR_ALARM;
	private int MINUTE_ALARM;
	private int AM_ALARM = 0;
	private static String modes[] = new String[] { "Weekday", "Weekend",
			"Everyday" };
	private int mode = 0;
	private String MODE_ALARM = "Weekday";
	private TextView tvSwitchAlarm;
	private TextView tvHourAlarm;
	private TextView tvMinuteAlarm;
	private TextView tvAmAlarm;
	private TextView tvModeAlarm;

	enum SlideDirection {
		RIGHT, LEFT, NO_SLIDE
	}

	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initListener();
		initData();
		initImage();
		initImageAnim();
	}

	@Override
	protected void onStart() {
		super.onStart();
		showImage(position);
		if (!photo_fit) {
			Thread.currentThread().interrupt();
			scroll.post(scrollrunnableDown);
		}
		autoShow();

	}

	private void initView() {
		setContentView(R.layout.activity_photo);

		/* 界面内所有隐藏布局 */
		rlPhotoFit = (RelativeLayout) findViewById(R.id.rl_photo_fit);
		rlPhotoFill = (RelativeLayout) findViewById(R.id.rl_photo_fill);
		rlGallery = (RelativeLayout) findViewById(R.id.rl_gallery);
		rlSetings = (RelativeLayout) findViewById(R.id.rl_settings);
		rlDateTime = (RelativeLayout) findViewById(R.id.rl_date_time);
		rlTextClock = (RelativeLayout) findViewById(R.id.rl_textclock);

		/* 6个播放组件 */
		ivsFit = (ImageSwitcher) findViewById(R.id.photo_ivs_fit);
		// ivsFill = (ImageSwitcher) findViewById(R.id.photo_ivs_fill);
		ivFill = (ImageView) findViewById(R.id.photo_iv_fill);
		ivsGalleryMain = (ImageSwitcher) findViewById(R.id.gallery_ivs_main);
		ivsGalleryTop = (ImageSwitcher) findViewById(R.id.gallery_ivs_top);
		ivsGalleryMid = (ImageSwitcher) findViewById(R.id.gallery_ivs_mid);
		ivsGalleryBottom = (ImageSwitcher) findViewById(R.id.gallery_ivs_bottom);

		/* 设置面板内容组件 */
		viewHolder.tvsetDateTime = (TextView) findViewById(R.id.tvset_date_time);
		viewHolder.tvsetAutoPower = (TextView) findViewById(R.id.tvset_auto_power);
		viewHolder.tvsetLanguage = (TextView) findViewById(R.id.tvset_language);
		viewHolder.tvsetBrightness = (TextView) findViewById(R.id.tvset_brightness);
		viewHolder.tvsetSplit = (TextView) findViewById(R.id.tvset_split);
		viewHolder.tvsetInterval = (TextView) findViewById(R.id.tvset_interval);
		viewHolder.tvsetTransition = (TextView) findViewById(R.id.tvset_transition);
		viewHolder.tvsetShuffle = (TextView) findViewById(R.id.tvset_shuffle);
		viewHolder.tvsetPhotoInfo = (TextView) findViewById(R.id.tvset_photo_info);
		viewHolder.tvsetPhotoFit = (TextView) findViewById(R.id.tvset_photo_fit);
		viewHolder.tvsetVersion = (TextView) findViewById(R.id.tvset_version);
		viewHolder.tvsetReset = (TextView) findViewById(R.id.tvset_reset);

		/* 时间日期组件 */
		monthText = (TextView) findViewById(R.id.tvCurrentDate);
		mViewPager = (ViewPager) findViewById(R.id.vp_calendar);

		scroll = (ScrollView) findViewById(R.id.scroll);
		hscroll = (HorizontalScrollView) findViewById(R.id.hscroll);
		mLayout = (LinearLayout) findViewById(R.id.mlayout);
	}

	private void initListener() {

		/* 每个设置项 */
		viewHolder.tvsetDateTime.setOnClickListener(this);
		viewHolder.tvsetAutoPower.setOnClickListener(this);
		viewHolder.tvsetLanguage.setOnClickListener(this);
		viewHolder.tvsetBrightness.setOnClickListener(this);
		viewHolder.tvsetSplit.setOnClickListener(this);
		viewHolder.tvsetInterval.setOnClickListener(this);
		viewHolder.tvsetTransition.setOnClickListener(this);
		viewHolder.tvsetShuffle.setOnClickListener(this);
		viewHolder.tvsetPhotoInfo.setOnClickListener(this);
		viewHolder.tvsetPhotoFit.setOnClickListener(this);
		viewHolder.tvsetVersion.setOnClickListener(this);
		viewHolder.tvsetReset.setOnClickListener(this);
	}

	private void initData() {
		context = this;

		/* 首选项相关数据 */
		sharedPreferences = getSharedPreferences(Echo.SHARED_PREFERENCES_NAME,
				0);
		editor = sharedPreferences.edit();
		transition = sharedPreferences.getInt(Echo.TRANSITION_SP, 0);
		interval = sharedPreferences.getInt(Echo.INTERVAL_SP, 5000);
		shuffle = sharedPreferences.getBoolean(Echo.SHUFFLE_SP, false);
		split = sharedPreferences.getBoolean(Echo.SPLIT_SP, false);
		photo_fit = sharedPreferences.getBoolean(Echo.PHOTO_FIT_SP, true);

		/* 获取intent携带参数 */
		pathType = getIntent().getStringExtra(Echo.PATH_TYPE);
		position = getIntent().getIntExtra(Echo.POSITION, 0);
		SOC = getIntent().getIntExtra("SOC", 0);
//		if (SOC == 1) {
//			SETTINGS_SHOWING = true;
//		} else if (SOC == 2) {
//			CALENDAR_SHOWING = true;
//		} else if (SOC == 3) {
//			TIME_SHOWING = true;
//		}

		imgPathList = FileUtils.getImageFilePath(context, pathType);

		/* 设置PopupWindow初始化 */
		pop = new PopupWindow(context);
		pop.setWidth(popParams);
		pop.setHeight(popParams);
		pop.setFocusable(true);
		pop.setBackgroundDrawable(new ColorDrawable(0x66000000));

		/* 日历数据初始化 */
		initCalendarData();
	}

	private void initCalendarData() {
		CalendarCard[] views = new CalendarCard[3];
		for (int i = 0; i < 3; i++) {
			views[i] = new CalendarCard(this, this);
		}
		adapter = new CalendarViewAdapter<CalendarCard>(views);
		setViewPager();
	}

	private void initImage() {
		if (SOC == 1) {
			rlSetings.setVisibility(View.VISIBLE);
			viewHolder.tvsetDateTime.requestFocus();
			SETTINGS_SHOWING = true;
		} else if (SOC == 2) {
			rlDateTime.setVisibility(View.VISIBLE);
			mViewPager.requestFocus();
			CALENDAR_SHOWING = true;
		} else if (SOC == 3) {
			rlTextClock.setVisibility(View.VISIBLE);
			TIME_SHOWING = true;
		}
		SOC = 0;
		if (split) {
			rlGallery.setVisibility(View.VISIBLE);
			ivsGalleryMain.setFactory(this);
			ivsGalleryTop.setFactory(this);
			ivsGalleryMid.setFactory(this);
			ivsGalleryBottom.setFactory(this);
		} else {
			if (photo_fit) {
				rlPhotoFit.setVisibility(View.VISIBLE);
				ivsFit.setFactory(this);
			} else {
				rlPhotoFill.setVisibility(View.VISIBLE);
			}
		}
	}

	private void showImage(int position) {
		initImageAnim();
		if (imgPathList.size() == 0) {
			if (split) {
				ivsGalleryMain.setImageResource(R.drawable.background_blue);
				ivsGalleryTop.setImageResource(R.drawable.background_blue);
				ivsGalleryMid.setImageResource(R.drawable.background_blue);
				ivsGalleryBottom.setImageResource(R.drawable.background_blue);
			} else if (photo_fit) {
				ivsFit.setImageResource(R.drawable.background_blue);
			} else {
				ivFill.setImageResource(R.drawable.background_blue);
			}
		} else {
			String imgPath = imgPathList.get(position);
			if (imgPath != null) {
				Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
				if (split) {
					int mPosition = position;
					bitmap = ImageUtils.getGalleryMain(bitmap);
					Bitmap[] bitmaps = new Bitmap[3];
					for (int i = 0; i < 3; i++) {
						bitmaps[i] = ImageUtils.getGalleryRest(BitmapFactory
								.decodeFile(imgPathList.get(mPosition)));
						if (mPosition < imgPathList.size() - 1) {
							mPosition++;
						} else {
							mPosition = 0;
						}
					}
					ivsGalleryMain.setImageDrawable(new BitmapDrawable(
							getResources(), bitmap));
					ivsGalleryTop.setImageDrawable(new BitmapDrawable(
							getResources(), bitmaps[0]));
					ivsGalleryMid.setImageDrawable(new BitmapDrawable(
							getResources(), bitmaps[1]));
					ivsGalleryBottom.setImageDrawable(new BitmapDrawable(
							getResources(), bitmaps[2]));
				} else {
					if (photo_fit) {
						bitmap = ImageUtils.fitScreen(bitmap);
						Drawable drawableFit = new BitmapDrawable(
								getResources(), bitmap);
						ivsFit.setImageDrawable(drawableFit);
					} else {
						bitmap = ImageUtils.fillScreen(bitmap);
						Drawable drawableFill = new BitmapDrawable(
								getResources(), bitmap);
						ivFill.setImageDrawable(drawableFill);
					}
				}

			}
		}
	}

	// Calendar
	private void autoShow() {
		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (playFlag) {
					if (shuffle) {
						position = shuffleRandom();
					} else {
						if (position < imgPathList.size() - 1) {
							position++;
						} else {
							position = 0;
						}
					}
					mHandler.sendEmptyMessage(0);
				}
			}
		};
		timer.schedule(task, interval, interval);
	}

	// Calendar
	private void setViewPager() {
		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(498);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				measureDirection(position);
				updateCalendarView(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	// Calendar
	private void measureDirection(int pos) {
		if (pos > mCurrentIndex) {
			mDirection = SlideDirection.RIGHT;
		} else if (pos < mCurrentIndex) {
			mDirection = SlideDirection.LEFT;
		}
		mCurrentIndex = pos;
	}

	// Calendar
	private void updateCalendarView(int pos) {
		mShowViews = adapter.getAllItems();
		if (mDirection == SlideDirection.RIGHT) {
			mShowViews[pos % mShowViews.length].rightSlide();
		} else if (mDirection == SlideDirection.LEFT) {
			mShowViews[pos % mShowViews.length].leftSlide();
		}
		mDirection = SlideDirection.NO_SLIDE;
	}

	// init Animation
	private void initImageAnim() {
		int tmpTransition;
		if (transition < 0) {
			tmpTransition = randomTransition();
		} else {
			tmpTransition = transition;
		}
		switch (tmpTransition) {
		case 0:
			animationIn = AnimationUtils.loadAnimation(context,
					R.anim.anim_null);
			animationOut = AnimationUtils.loadAnimation(context,
					R.anim.anim_null);
			break;
		case 1:
			animationIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
			animationOut = AnimationUtils.loadAnimation(context,
					R.anim.fade_out);
			break;
		case 2:
			animationIn = AnimationUtils.loadAnimation(context,
					R.anim.slide_in_left2right);
			animationOut = AnimationUtils.loadAnimation(context,
					R.anim.slide_out_left2right);
			break;
		case 3:
			animationIn = AnimationUtils.loadAnimation(context,
					R.anim.slide_in_up2down);
			animationOut = AnimationUtils.loadAnimation(context,
					R.anim.slide_out_up2down);
			break;
//		case 4:
//			animationIn = AnimationUtils.loadAnimation(context, R.anim.bounce);
//			animationOut = AnimationUtils.loadAnimation(context,
//					R.anim.anim_null);
//			break;
//		case 5:
//			animationIn = AnimationUtils.loadAnimation(context,
//					R.anim.push_left_in);
//			animationOut = AnimationUtils.loadAnimation(context,
//					R.anim.push_left_out);
//			break;

		default:
			break;
		}
		if (split) {
			ivsGalleryMain.setInAnimation(animationIn);
			ivsGalleryMain.setOutAnimation(animationOut);
		} else {
			if (photo_fit) {
				ivsFit.setInAnimation(animationIn);
				ivsFit.setOutAnimation(animationOut);
			} else {
				ivFill.startAnimation(AnimationUtils.loadAnimation(context,
						R.anim.fade_in));
				ivFill.setAnimation(AnimationUtils.loadAnimation(context,
						R.anim.fade_in));
			}
		}
	}

	// Random
	private int randomTransition() {
		int i = (int) (Math.random() * 4);
		return i;
	}

	// ImageSwitch
	@Override
	public View makeView() {
		ImageView iv = new ImageView(this);
		iv.setScaleType(ImageView.ScaleType.CENTER);
		iv.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return iv;
	}

	// MyHandler
	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				showImage(position);
				break;
			case 1:
				tvMonthSet.setText("" + MONTH_SET);
				break;
			case 2:
				tvDaySet.setText("" + DAY_SET);
				break;
			case 3:
				tvYearSet.setText("" + YEAR_SET);
				break;
			case 4:
				tvHourSet.setText("" + HOUR_SET);
				break;
			case 5:
				tvMinuteSet.setText("" + MINUTE_SET);
				break;
			case 6:
				tvAmSet.setText(AM_SET);
				break;
			case 7:
				Log.e("Swith_alarm:", SWITCH_AlARM);
				tvSwitchAlarm.setText(SWITCH_AlARM);
				break;
			case 8:
				tvHourAlarm.setText("" + HOUR_ALARM);
				break;
			case 9:
				tvMinuteAlarm.setText("" + MINUTE_ALARM);
				break;
			case 10:
				if (AM_ALARM == 0) {
					tvAmAlarm.setText("AM");
				} else {
					tvAmAlarm.setText("PM");
				}
				break;
			case 11:
				tvModeAlarm.setText(MODE_ALARM);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	}

	// Settings Panel
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvset_date_time:
			showDateTimePopupWindow();
			break;
		case R.id.tvset_auto_power:
			showAutoPowerPopupWindow();
			break;
		case R.id.tvset_language:
			showLanguagePopupWindow();
			break;
		case R.id.tvset_brightness:
			showBrightnessPopupWindow();
			break;
		case R.id.tvset_split:
			showSplitPopupWindow();
			break;
		case R.id.tvset_interval:
			showIntervalPopupWindow();
			break;
		case R.id.tvset_transition:
			showTransitionPopupWindow();
			break;
		case R.id.tvset_shuffle:
			showShufflePopupWindow();
			break;
		case R.id.tvset_photo_info:
			showPhotoInfoPopupWindow();
		case R.id.tvset_photo_fit:
			showPhotoFitPopupWindow();
			break;
		case R.id.tvset_version:
			showVersionPopupWindow();
			break;
		case R.id.tvset_reset:
			showResetPopupWindow();
			break;

		default:
			break;
		}
	}

	private void showDateTimePopupWindow() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.window_date_time, null);
		tvMonthSet = (TextView) view.findViewById(R.id.tv_datetime_month);
		tvDaySet = (TextView) view.findViewById(R.id.tv_datetime_day);
		tvYearSet = (TextView) view.findViewById(R.id.tv_datetime_year);
		tvHourSet = (TextView) view.findViewById(R.id.tv_datetime_hour);
		tvMinuteSet = (TextView) view.findViewById(R.id.tv_datetime_minute);
		tvAmSet = (TextView) view.findViewById(R.id.tv_datetime_am);
		MONTH_SET = Integer.valueOf(getCurrentDate("month"));
		DAY_SET = Integer.valueOf(getCurrentDate("day"));
		YEAR_SET = Integer.valueOf(getCurrentDate("year"));
		HOUR_SET = Integer.valueOf(getCurrentDate("hour"));
		MINUTE_SET = Integer.valueOf(getCurrentDate("minute"));
		if (HOUR_SET > 12) {
			HOUR_SET = HOUR_SET - 12;
			AM_SET = "PM";
		}
		tvMonthSet.setText("" + MONTH_SET);
		tvDaySet.setText("" + DAY_SET);
		tvYearSet.setText("" + YEAR_SET);
		tvHourSet.setText("" + HOUR_SET);
		tvMinuteSet.setText("" + MINUTE_SET);
		tvAmSet.setText(AM_SET);

		OnKeyListener keyListener = new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (v.getId()) {
					case R.id.tv_datetime_month:
						if (keyCode == Echo.KEY_UP) {
							if (MONTH_SET < 12) {
								MONTH_SET++;
							} else {
								MONTH_SET = 1;
							}
						} else if (keyCode == Echo.KEY_DOWN) {
							if (MONTH_SET > 1) {
								MONTH_SET--;
							} else {
								MONTH_SET = 12;
							}
						}
						mHandler.sendEmptyMessage(1);
						break;
					case R.id.tv_datetime_day:
						if (keyCode == Echo.KEY_UP) {
							if (DAY_SET < 31) {
								DAY_SET++;
							} else {
								DAY_SET = 1;
							}
						} else if (keyCode == Echo.KEY_DOWN) {
							if (DAY_SET > 1) {
								DAY_SET--;
							} else {
								DAY_SET = 31;
							}
						}
						mHandler.sendEmptyMessage(2);
						break;
					case R.id.tv_datetime_year:
						if (keyCode == Echo.KEY_UP) {
							if (YEAR_SET < 2037) {
								YEAR_SET++;
							}
						} else if (keyCode == Echo.KEY_DOWN) {
							if (YEAR_SET > 1977) {
								YEAR_SET--;
							}
						}
						mHandler.sendEmptyMessage(3);
						break;
					case R.id.tv_datetime_hour:
						if (keyCode == Echo.KEY_UP) {
							if (HOUR_SET < 12) {
								HOUR_SET++;
							} else {
								HOUR_SET = 1;
							}
						} else if (keyCode == Echo.KEY_DOWN) {
							if (HOUR_SET > 1) {
								HOUR_SET--;
							} else {
								HOUR_SET = 12;
							}
						}
						mHandler.sendEmptyMessage(4);
						break;
					case R.id.tv_datetime_minute:
						if (keyCode == Echo.KEY_UP) {
							if (MINUTE_SET < 50) {
								MINUTE_SET++;
							} else {
								MINUTE_SET = 1;
							}
						} else if (keyCode == Echo.KEY_DOWN) {
							if (MINUTE_SET > 1) {
								MINUTE_SET--;
							} else {
								MINUTE_SET = 59;
							}
						}
						mHandler.sendEmptyMessage(5);
						break;
					case R.id.tv_datetime_am:
						if (keyCode == Echo.KEY_UP) {
							if (AM_SET.equals("AM")) {
								AM_SET = "PM";
							} else {
								AM_SET = "AM";
							}
						} else if (keyCode == Echo.KEY_DOWN) {
							if (AM_SET.equals("AM")) {
								AM_SET = "PM";
							} else {
								AM_SET = "AM";
							}
						}
						mHandler.sendEmptyMessage(6);
						break;

					default:
						break;
					}

				}
				return false;
			}
		};

		OnClickListener mListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					int mHour = 0;
					if (AM_SET.equals("PM")) {
						mHour = HOUR_SET + 12;
					} else {
						mHour = HOUR_SET;
					}
					DateTimeUtils.setDateTime(YEAR_SET, MONTH_SET, DAY_SET,
							mHour, MINUTE_SET);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				pop.dismiss();

				removeThreads();
				initView();
				initListener();
				initData();
				initImage();
				initImageAnim();
				showImage(position);
				if (!photo_fit) {
					scroll.post(scrollrunnableDown);
				}
				autoShow();

			}
		};
		tvMonthSet.setOnKeyListener(keyListener);
		tvDaySet.setOnKeyListener(keyListener);
		tvYearSet.setOnKeyListener(keyListener);
		tvHourSet.setOnKeyListener(keyListener);
		tvMinuteSet.setOnKeyListener(keyListener);
		tvAmSet.setOnKeyListener(keyListener);
		tvMonthSet.setOnClickListener(mListener);
		tvDaySet.setOnClickListener(mListener);
		tvYearSet.setOnClickListener(mListener);
		tvHourSet.setOnClickListener(mListener);
		tvMinuteSet.setOnClickListener(mListener);
		tvAmSet.setOnClickListener(mListener);

		pop.setContentView(view);
		pop.showAsDropDown(viewHolder.tvsetDateTime, setitemWidth + 1,
				-setitemHeight);
	}

	private void showAutoPowerPopupWindow() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.window_auto_power, null);
		tvSwitchAlarm = (TextView) view
				.findViewById(R.id.tvset_autopower_switch);
		tvHourAlarm = (TextView) view.findViewById(R.id.tvset_autopower_hour);
		tvMinuteAlarm = (TextView) view
				.findViewById(R.id.tvset_autopower_minute);
		tvAmAlarm = (TextView) view.findViewById(R.id.tvset_autopower_am);
		tvModeAlarm = (TextView) view.findViewById(R.id.tvset_autopower_mode);
		SWITCH_AlARM = "On";
		HOUR_ALARM = sharedPreferences.getInt(Echo.HOUR_SCREEN_ON_SP, 0);
		MINUTE_ALARM = sharedPreferences.getInt(Echo.MINUTE_SCREEN_ON_SP, 0);
		AM_ALARM = sharedPreferences.getInt(Echo.AM_SCREEN_ON_SP, 0);
		MODE_ALARM = sharedPreferences.getString(Echo.MODE_SCREEN_ON_SP,
				"Weekday");
		tvHourAlarm.setText("" + HOUR_ALARM);
		tvMinuteAlarm.setText("" + MINUTE_ALARM);
		if (AM_ALARM == 0) {
			tvAmAlarm.setText("AM");
		} else {
			tvAmAlarm.setText("PM");
		}
		tvModeAlarm.setText(MODE_ALARM);
		OnKeyListener keyListener = new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (v.getId()) {
					case R.id.tvset_autopower_switch:
						if (keyCode == Echo.KEY_UP || keyCode == Echo.KEY_DOWN) {
							if (SWITCH_AlARM.equals("On")) {
								SWITCH_AlARM = "Off";
							} else {
								SWITCH_AlARM = "On";
							}
							mHandler.sendEmptyMessage(7);
						}
						break;
					case R.id.tvset_autopower_hour:
						if (keyCode == Echo.KEY_UP) {
							if (HOUR_ALARM < 12) {
								HOUR_ALARM++;
							} else {
								HOUR_ALARM = 1;
							}
						} else if (keyCode == Echo.KEY_DOWN) {
							if (HOUR_ALARM > 1) {
								HOUR_ALARM--;
							} else {
								HOUR_ALARM = 12;
							}
						}
						mHandler.sendEmptyMessage(8);
						break;
					case R.id.tvset_autopower_minute:
						if (keyCode == Echo.KEY_UP) {
							if (MINUTE_ALARM < 59) {
								MINUTE_ALARM++;
							} else {
								MINUTE_ALARM = 1;
							}
						} else if (keyCode == Echo.KEY_DOWN) {
							if (MINUTE_ALARM > 0) {
								MINUTE_ALARM--;
							} else {
								MINUTE_ALARM = 59;
							}
						}
						mHandler.sendEmptyMessage(9);
						break;
					case R.id.tvset_autopower_am:
						if (keyCode == Echo.KEY_UP || keyCode == Echo.KEY_DOWN) {
							if (AM_ALARM == 0) {
								AM_ALARM = 1;
							} else {
								AM_ALARM = 0;
							}
						}
						mHandler.sendEmptyMessage(10);
						break;
					case R.id.tvset_autopower_mode:
						if (keyCode == Echo.KEY_UP) {
							if (mode < 2) {
								mode++;
							} else {
								mode = 0;
							}
							MODE_ALARM = modes[mode];
							mHandler.sendEmptyMessage(11);
						} else if (keyCode == Echo.KEY_DOWN) {
							if (mode > 0) {
								mode--;
							} else {
								mode = 2;
							}
							MODE_ALARM = modes[mode];
							mHandler.sendEmptyMessage(11);
						}
						break;
					default:
						break;
					}

				}
				return false;
			}
		};
		OnClickListener click = new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (SWITCH_AlARM.equals("On")) {
					setAlarmScreenOn();
				} else {
					setAlarmScreenOff();
				}
				pop.dismiss();

			}
		};
		tvSwitchAlarm.setOnKeyListener(keyListener);
		tvHourAlarm.setOnKeyListener(keyListener);
		tvMinuteAlarm.setOnKeyListener(keyListener);
		tvAmAlarm.setOnKeyListener(keyListener);
		tvModeAlarm.setOnKeyListener(keyListener);
		tvSwitchAlarm.setOnClickListener(click);
		tvHourAlarm.setOnClickListener(click);
		tvMinuteAlarm.setOnClickListener(click);
		tvAmAlarm.setOnClickListener(click);
		tvModeAlarm.setOnClickListener(click);
		pop.setContentView(view);
		pop.showAsDropDown(viewHolder.tvsetAutoPower, setitemWidth + 1,
				-setitemHeight);
	}

	protected void setAlarmScreenOn() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.set(Calendar.HOUR, HOUR_ALARM);
		c.set(Calendar.AM_PM, AM_ALARM);
		c.set(Calendar.MINUTE, MINUTE_ALARM);
		editor.putInt(Echo.HOUR_SCREEN_ON_SP, HOUR_ALARM);
		editor.putInt(Echo.MINUTE_SCREEN_ON_SP, MINUTE_ALARM);
		editor.putInt(Echo.AM_SCREEN_ON_SP, AM_ALARM);
		editor.putString(Echo.MODE_SCREEN_ON_SP, MODE_ALARM);
		editor.commit();
		AlarmUtils.setAlarm(context, c, 0);
	}

	protected void setAlarmScreenOff() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.set(Calendar.HOUR, HOUR_ALARM);
		c.set(Calendar.AM_PM, AM_ALARM);
		c.set(Calendar.MINUTE, MINUTE_ALARM);
		editor.putInt(Echo.HOUR_SCREEN_OFF_SP, HOUR_ALARM);
		editor.putInt(Echo.MINUTE_SCREEN_OFF_SP, MINUTE_ALARM);
		editor.putInt(Echo.AM_SCREEN_OFF_SP, AM_ALARM);
		editor.putString(Echo.MODE_SCREEN_OFF_SP, MODE_ALARM);
		editor.commit();
		AlarmUtils.setAlarm(context, c, 1);
	}

	private void showLanguagePopupWindow() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.window_language, null);
		TextView tvEng = (TextView) view.findViewById(R.id.tvset_en);
		TextView tvFra = (TextView) view.findViewById(R.id.tvset_fr);
		TextView tvSpa = (TextView) view.findViewById(R.id.tvset_es);
		OnClickListener mListener = new OnClickListener() {
			String lan;

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tvset_en:
					lan = "en";
					break;
				case R.id.tvset_fr:
					lan = "fr";
					break;
				case R.id.tvset_es:
					lan = "zh";
					break;

				default:
					break;
				}
				editor.putString(Echo.LANGUAGE_SP, lan);
				editor.commit();
				LanguageUtils.setLanguage(context, lan);
				pop.dismiss();
			}
		};
		tvEng.setOnClickListener(mListener);
		tvFra.setOnClickListener(mListener);
		tvSpa.setOnClickListener(mListener);
		pop.setContentView(view);
		pop.showAsDropDown(viewHolder.tvsetLanguage, setitemWidth + 1,
				-setitemHeight);
	}

	private void showBrightnessPopupWindow() {
		brightness = sharedPreferences.getInt(Echo.BRIGHTNESS_SP, 9);
		Log.e("", "brightness_get:" + brightness);
		View view = LayoutInflater.from(context).inflate(
				R.layout.window_brightness, null);
		GridView gv = (GridView) view.findViewById(R.id.gv_brightness);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.e("", "brightness_save:" + position);
				editor.putInt(Echo.BRIGHTNESS_SP, position);
				editor.commit();
				BrightnessUitls.setBrightness(context.getContentResolver(),
						(position + 1) * 10);
				pop.dismiss();
			}
		});
		gv.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				BrightnessUitls.brightnessPreview(PhotoActivity.this,
						(float) (position + 1) / 10);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		BrightnessAdapter brightAdapter = new BrightnessAdapter(context);
		gv.setAdapter(brightAdapter);
		gv.setSelection(brightness);
		pop.setContentView(view);
		pop.showAsDropDown(viewHolder.tvsetBrightness, setitemWidth + 1,
				-setitemHeight);
	}

	// Split
	private void showSplitPopupWindow() {
		View view = LayoutInflater.from(context).inflate(R.layout.window_split,
				null);
		TextView splitOn = (TextView) view.findViewById(R.id.tvset_split_on);
		TextView splitOff = (TextView) view.findViewById(R.id.tvset_split_off);
		OnClickListener click = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.tvset_split_on) {
					split = true;
					rlGallery.setVisibility(View.VISIBLE);
					rlPhotoFit.setVisibility(View.INVISIBLE);
					rlPhotoFill.setVisibility(View.INVISIBLE);
				} else {
					split = false;
					rlGallery.setVisibility(View.INVISIBLE);
					if (photo_fit) {
						rlPhotoFit.setVisibility(View.INVISIBLE);
					} else {
						rlPhotoFill.setVisibility(View.INVISIBLE);
					}
				}
				editor.putBoolean(Echo.SPLIT_SP, split);
				editor.commit();
				pop.dismiss();
				removeThreads();
				initView();
				initListener();
//				initData();
				initImage();
				initImageAnim();
				showImage(position);
				if (!photo_fit) {
					scroll.post(scrollrunnableDown);
				}
				autoShow();
			}
		};
		splitOn.setOnClickListener(click);
		splitOff.setOnClickListener(click);
		pop.setContentView(view);
		pop.showAsDropDown(viewHolder.tvsetSplit, setitemWidth + 1,
				-setitemHeight);

	}

	// 换图间隔时间
	private void showIntervalPopupWindow() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.window_interval, null);
		TextView tv5s = (TextView) view.findViewById(R.id.tvset_interval_5s);
		TextView tv10s = (TextView) view.findViewById(R.id.tvset_interval_10s);
		TextView tv30s = (TextView) view.findViewById(R.id.tvset_interval_30s);
		TextView tv1m = (TextView) view.findViewById(R.id.tvset_interval_1m);
		TextView tv1h = (TextView) view.findViewById(R.id.tvset_interval_1h);
		TextView tv1d = (TextView) view.findViewById(R.id.tvset_interval_1d);
		OnClickListener mListener = new OnClickListener() {

			String toastContent;

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tvset_interval_5s:
					interval = 5000;
					toastContent = "5 seconds";
					break;
				case R.id.tvset_interval_10s:
					interval = 10000;
					toastContent = "10 seconds";
					break;
				case R.id.tvset_interval_30s:
					interval = 30000;
					toastContent = "30 seconds";
					break;
				case R.id.tvset_interval_1m:
					interval = 60000;
					toastContent = "1 minute";
					break;
				case R.id.tvset_interval_1h:
					interval = 3600000;
					toastContent = "1 hour";
					break;
				case R.id.tvset_interval_1d:
					interval = 86400000;
					toastContent = "1 day";
					break;

				default:
					toastContent = "set failed";
					break;
				}
				editor.putInt(Echo.INTERVAL_SP, interval);
				editor.commit();
				timer.cancel();
				autoShow();
				pop.dismiss();
				Toast.makeText(context, toastContent, Toast.LENGTH_SHORT)
						.show();
			}
		};
		tv5s.setOnClickListener(mListener);
		tv10s.setOnClickListener(mListener);
		tv30s.setOnClickListener(mListener);
		tv1m.setOnClickListener(mListener);
		tv1h.setOnClickListener(mListener);
		tv1d.setOnClickListener(mListener);
		pop.setContentView(view);
		pop.showAsDropDown(viewHolder.tvsetInterval, setitemWidth + 1,
				-setitemHeight);
	}

	// 图片过渡效果
	private void showTransitionPopupWindow() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.window_transition, null);
		TextView tvNormal = (TextView) view
				.findViewById(R.id.tvset_transition_normal);
		TextView tvRandom = (TextView) view
				.findViewById(R.id.tvset_transition_random);
		TextView tvFade = (TextView) view
				.findViewById(R.id.tvset_transition_fade);
		TextView tvLeftRight = (TextView) view
				.findViewById(R.id.tvset_transition_left_right);
		TextView tvTopBottom = (TextView) view
				.findViewById(R.id.tvset_transition_top_bottom);
//		TextView tvOpenDoor = (TextView) view
//				.findViewById(R.id.tvset_transition_open_door);
//		TextView tvCrossComb = (TextView) view
//				.findViewById(R.id.tvset_transition_cross_comb);
		OnClickListener mListener = new OnClickListener() {

			String toastContent;

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tvset_transition_normal:
					transition = 0;
					toastContent = "normal";
					break;
				case R.id.tvset_transition_fade:
					transition = 1;
					toastContent = "fade";
					break;
				case R.id.tvset_transition_left_right:
					transition = 2;
					toastContent = "left to right";
					break;
				case R.id.tvset_transition_top_bottom:
					transition = 3;
					toastContent = "top to bottom";
					break;
//				case R.id.tvset_transition_open_door:
//					transition = 4;
//					toastContent = "open door";
//					break;
//				case R.id.tvset_transition_cross_comb:
//					transition = 5;
//					toastContent = "cross comb";
//					break;
				case R.id.tvset_transition_random:
					transition = -1;
					toastContent = "random";
					break;

				default:
					toastContent = "set failed";
					break;
				}
				editor.putInt(Echo.TRANSITION_SP, transition);
				editor.commit();
				initImageAnim();
				pop.dismiss();
				Toast.makeText(context, toastContent, Toast.LENGTH_SHORT)
						.show();
			}
		};
		tvNormal.setOnClickListener(mListener);
		tvRandom.setOnClickListener(mListener);
		tvFade.setOnClickListener(mListener);
		tvLeftRight.setOnClickListener(mListener);
		tvTopBottom.setOnClickListener(mListener);
//		tvOpenDoor.setOnClickListener(mListener);
//		tvCrossComb.setOnClickListener(mListener);
		pop.setContentView(view);
		pop.showAsDropDown(viewHolder.tvsetTransition, setitemWidth + 1,
				-setitemHeight);
	}

	// 洗牌模式
	private void showShufflePopupWindow() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.window_shuffle, null);
		TextView tvOn = (TextView) view.findViewById(R.id.tvset_shuffle_on);
		TextView tvOff = (TextView) view.findViewById(R.id.tvset_shuffle_off);
		OnClickListener mListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.tvset_shuffle_on) {
					shuffle = true;
					Toast.makeText(context, "shuffle mode on",
							Toast.LENGTH_SHORT).show();
				} else {
					shuffle = false;
					Toast.makeText(context, "shuffle mode off",
							Toast.LENGTH_SHORT).show();
				}
				editor.putBoolean(Echo.SHUFFLE_SP, shuffle);
				editor.commit();
				pop.dismiss();
			}
		};
		tvOn.setOnClickListener(mListener);
		tvOff.setOnClickListener(mListener);
		pop.setContentView(view);
		pop.showAsDropDown(viewHolder.tvsetShuffle, setitemWidth + 1,
				-setitemHeight);
	}

	private void showPhotoInfoPopupWindow() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.window_photo_info, null);
		TextView tvOn = (TextView) view.findViewById(R.id.tvset_photo_info_on);
		TextView tvOff = (TextView) view
				.findViewById(R.id.tvset_photo_info_off);
		OnClickListener mListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.tvset_photo_info_on) {
					photo_info = true;
					Toast.makeText(context, "photo info on", Toast.LENGTH_SHORT)
							.show();
				} else {
					photo_info = false;
					Toast.makeText(context, "photo info off",
							Toast.LENGTH_SHORT).show();
				}
				editor.putBoolean(Echo.PHOTO_INFO_SP, photo_info);
				editor.commit();
				pop.dismiss();
			}
		};
		tvOn.setOnClickListener(mListener);
		tvOff.setOnClickListener(mListener);
		pop.setContentView(view);
		pop.showAsDropDown(viewHolder.tvsetPhotoInfo, setitemWidth + 1,
				-setitemHeight);
	}

	// Photo Fit
	private void showPhotoFitPopupWindow() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.window_photo_fit, null);
		TextView tvOn = (TextView) view.findViewById(R.id.tvset_photo_fit_on);
		TextView tvOff = (TextView) view.findViewById(R.id.tvset_photo_fit_off);
		OnClickListener mListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.tvset_photo_fit_on) {
					photo_fit = true;
				} else if (v.getId() == R.id.tvset_photo_fit_off) {
					photo_fit = false;
				}
				editor.putBoolean(Echo.SPLIT_SP, false);
				editor.putBoolean(Echo.PHOTO_FIT_SP, photo_fit);
				editor.commit();
				pop.dismiss();
				removeThreads();
				initView();
				initListener();
				initData();
				initImage();
				initImageAnim();
				showImage(position);
				if (!photo_fit) {
					scroll.post(scrollrunnableDown);
				}
				autoShow();
			}
		};
		tvOn.setOnClickListener(mListener);
		tvOff.setOnClickListener(mListener);
		pop.setContentView(view);
		pop.showAsDropDown(viewHolder.tvsetPhotoFit, setitemWidth + 1,
				-setitemHeight);
	}

	// Version
	private void showVersionPopupWindow() {
		String sdTotal = InfoUtils.getStorageTotalSize(context, "/mnt/external_sd");
		String sdAvailable = InfoUtils.getStorageAvailableSize(context, "/mnt/external_sd");
		String usbTotal = InfoUtils.getStorageTotalSize(context, "/mnt/usb_storage");
		String usbAvailable = InfoUtils.getStorageAvailableSize(context, "/mnt/usb_storage");
		String flashTotal = InfoUtils.getStorageTotalSize(context, "/storage/emulated/0");
		String flashAvailable = InfoUtils.getStorageAvailableSize(context, "/storage/emulated/0");
		View view = LayoutInflater.from(context).inflate(
				R.layout.window_version, null);
		TextView tvVersion = (TextView) view.findViewById(R.id.tvset_show_version);
		TextView tvStorageInfo = (TextView) view.findViewById(R.id.tvset_storage_info);
		TextView tvManufacture = (TextView) view.findViewById(R.id.tvset_manufacture);
		tvStorageInfo.setText("Storage Space"
				+"\nSD T:"+sdTotal+" A:"+sdAvailable
				+"\nUSB T:"+usbTotal+" A:"+usbAvailable
				+"\nFLASH T:"+flashTotal+" A:"+flashAvailable);
//		tvManufacture.setText("Manufaturer:\nMJS Technology Co.,Limited");
		OnClickListener click =  new OnClickListener() {

			@Override
			public void onClick(View v) {
				pop.dismiss();
			}
		};
		tvVersion.setOnClickListener(click);
		tvStorageInfo.setOnClickListener(click);
		tvManufacture.setOnClickListener(click);
		pop.setContentView(view);
		pop.showAsDropDown(viewHolder.tvsetVersion, setitemWidth, -5
				* setitemHeight);
	}

	// Reset
	private void showResetPopupWindow() {
		View view = LayoutInflater.from(context).inflate(R.layout.window_reset,
				null);
		TextView tvsetReset = (TextView) view
				.findViewById(R.id.tvset_reset_yes);
		tvsetReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pop.dismiss();
				Intent intent = new Intent();
				intent.setAction("com.echo.mypf.MASTERCLEAR");
				sendBroadcast(intent);

			}
		});
		pop.setContentView(view);
		pop.showAsDropDown(viewHolder.tvsetReset, setitemWidth + 1,
				-setitemHeight);
	}

	// 获取当前日期
	@SuppressLint("SimpleDateFormat")
	private String getCurrentDate(String type) {
		SimpleDateFormat formatter;
		if (type.equals("year")) {
			formatter = new SimpleDateFormat("yyyy");
		} else if (type.equals("month")) {
			formatter = new SimpleDateFormat("MM");
		} else if (type.equals("day")) {
			formatter = new SimpleDateFormat("dd");
		} else if (type.equals("hour")) {
			formatter = new SimpleDateFormat("HH");
		} else if (type.equals("minute")) {
			formatter = new SimpleDateFormat("mm");
		} else {
			formatter = new SimpleDateFormat("yyyyMMdd");
		}
		Date curDate = new Date(System.currentTimeMillis());
		String date = formatter.format(curDate);
		return date;
	}

	// 日期格式转换
	private String formatDate(int month, int year) {
		String[] months = getResources().getStringArray(R.array.month_name);
		return months[month - 1] + " " + year;
	}

	// 按键监听事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getRepeatCount() != 0) {
			return false;
		}
		switch (event.getKeyCode()) {
		case Echo.KEY_MENU:
			int isShowSetting = rlSetings.getVisibility();
			if (isShowSetting == View.VISIBLE) {
				SETTINGS_SHOWING = false;
				isShowSetting = View.GONE;
				autoShow();
				Toast.makeText(context, "play", Toast.LENGTH_SHORT).show();
			} else {
				SETTINGS_SHOWING = true;
				isShowSetting = View.VISIBLE;
				timer.cancel();
				Toast.makeText(context, "pause", Toast.LENGTH_SHORT).show();
			}
			rlSetings.setVisibility(isShowSetting);
			viewHolder.tvsetDateTime.requestFocus();
			break;
		case Echo.KEY_CALL_TIME:
//			int isShowDate = rlDateTime.getVisibility();
//			if (isShowDate == View.VISIBLE) {
//				CALENDAR_SHOWING = false;
//				isShowDate = View.GONE;
//				TIME_SHOWING = true;
//				rlTextClock.setVisibility(View.VISIBLE);
//			} else if(TIME_SHOWING == true){
//				TIME_SHOWING = false;
//				rlTextClock.setVisibility(View.GONE);
//			} else if(CALENDAR_SHOWING == false && TIME_SHOWING == false){
//				CALENDAR_SHOWING = true;
//				isShowDate = View.VISIBLE;
//			}
//			rlDateTime.setVisibility(isShowDate);
//			if (CALENDAR_SHOWING) {
//				mViewPager.requestFocus();
//			}
//			int isShowClock = rlTextClock.getVisibility();
			if (rlTextClock.getVisibility() == View.GONE &&
			rlDateTime.getVisibility() == View.GONE) {
				TIME_SHOWING = true;
				rlTextClock.setVisibility(View.VISIBLE);
			}else if (rlTextClock.getVisibility() == View.VISIBLE) {
				TIME_SHOWING = false;
				rlTextClock.setVisibility(View.GONE);
				CALENDAR_SHOWING = true;
				rlDateTime.setVisibility(View.VISIBLE);
			}else if (rlTextClock.getVisibility() == View.GONE &&
			rlDateTime.getVisibility() == View.VISIBLE) {
				CALENDAR_SHOWING = false;
				rlDateTime.setVisibility(View.GONE);
			}
			break;
		case Echo.KEY_ENTER:
			playFlag = !playFlag;
			if (playFlag) {
				Toast.makeText(context, "play", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "pause", Toast.LENGTH_SHORT).show();
			}
			break;
		case Echo.KEY_LEFT:
			if (FLAG_LEFT) {
				FLAG_LEFT = false;
				if (shuffle) {
					position = shuffleRandom();
				} else {
					if (position > 0) {
						position--;
					} else {
						position = imgPathList.size() - 1;
					}
				}

				timer.cancel();
				mHandler.sendEmptyMessage(0);
				autoShow();
				Toast.makeText(context, "last one", Toast.LENGTH_SHORT).show();
				FLAG_LEFT = true;
			}
			break;
		case Echo.KEY_RIGHT:
			if (FLAG_RIGHT) {
				FLAG_RIGHT = false;
				if (shuffle) {
					position = shuffleRandom();
				} else {
					if (position < imgPathList.size() - 1) {
						position++;
					} else {
						position = 0;
					}
				}
				timer.cancel();
				mHandler.sendEmptyMessage(0);
				autoShow();
				Toast.makeText(context, "next one", Toast.LENGTH_SHORT).show();
				FLAG_RIGHT = true;
			}
			break;
		case Echo.KEY_ZOOM:
			Intent intent = new Intent(context, PhotoZoomActivity.class);
			intent.putExtra(Echo.PATH_TYPE, pathType);
			intent.putExtra(Echo.POSITION, position);
			intent.putExtra(Echo.ZOOM_ROTATE, 0);
			startActivity(intent);
			finish();
			break;
		case Echo.KEY_ROTATE:
			Intent intent2 = new Intent(context, PhotoZoomActivity.class);
			intent2.putExtra(Echo.PATH_TYPE, pathType);
			intent2.putExtra(Echo.POSITION, position);
			intent2.putExtra(Echo.ZOOM_ROTATE, 1);
			startActivity(intent2);
			finish();
			break;
		case Echo.KEY_BACK:
			if (SETTINGS_SHOWING || CALENDAR_SHOWING) {
				if (SETTINGS_SHOWING) {
					rlSetings.setVisibility(View.GONE);
					SETTINGS_SHOWING = false;
				} else if (CALENDAR_SHOWING) {
					rlDateTime.setVisibility(View.GONE);
					CALENDAR_SHOWING = false;
				}
				return true;
			} else {
				editor.putString(Echo.PATH_TYPE_SP, pathType);
				editor.putInt(Echo.POSITION_SP, position);
				editor.commit();
				removeThreads();
			}
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 洗牌函数
	private int shuffleRandom() {
		int pos = (int) (Math.random() * imgPathList.size());
		return pos;
	}

	private void removeThreads() {
		if (timer != null) {
			timer.cancel();
		}
		scroll.removeCallbacks(scrollrunnableDown);
		scroll.removeCallbacks(scrollrunnableUp);
		hscroll.removeCallbacks(scrollrunnableRight);
		hscroll.removeCallbacks(scrollrunnableLeft);
	}

	@Override
	protected void onStop() {
		editor.putString(Echo.PATH_TYPE_SP, pathType);
		editor.putInt(Echo.POSITION_SP, position);
		editor.commit();
		removeThreads();
		super.onStop();
	}

	@Override
	protected void onPause() {
		editor.putString(Echo.PATH_TYPE_SP, pathType);
		editor.putInt(Echo.POSITION_SP, position);
		editor.commit();
		super.onPause();
	}

	// 设置面板viewHolder
	private class ViewHolder {
		TextView tvsetDateTime;
		TextView tvsetAutoPower;
		TextView tvsetLanguage;
		TextView tvsetBrightness;
		TextView tvsetSplit;
		TextView tvsetInterval;
		TextView tvsetTransition;
		TextView tvsetShuffle;
		TextView tvsetPhotoInfo;
		TextView tvsetPhotoFit;
		TextView tvsetVersion;
		TextView tvsetReset;
	}

	// 日历监听器
	@Override
	public void clickDate(CustomDate date) {
	}

	// 日历监听器
	@Override
	public void changeDate(CustomDate date) {
		monthText.setText(formatDate(date.month, date.year));
	}

	// 向下滚动
	private Runnable scrollrunnableDown = new Runnable() {
		@SuppressLint("NewApi")
		@Override
		public void run() {
			Log.e(tag, "Runnable--Down");
			int off = mLayout.getMeasuredHeight() - scroll.getHeight();
			if (off > 0) {
				scroll.scrollBy(0, 1);
				if (scroll.getScrollY() == off) {
					Thread.currentThread().interrupt();
					scroll.postDelayed(scrollrunnableUp, 80);
				} else {
					scroll.postDelayed(scrollrunnableDown, 80);
				}
			} else {
				hscroll.postDelayed(scrollrunnableRight, 80);
			}
		}
	};
	// 向上滚动
	private Runnable scrollrunnableUp = new Runnable() {
		@SuppressLint("NewApi")
		@Override
		public void run() {
			Log.e(tag, "Runnable--Up");
			int off = mLayout.getMeasuredHeight() - scroll.getHeight();
			if (off > 0) {
				scroll.scrollBy(0, -1);
				if (scroll.getScrollY() == 0) {
					Thread.currentThread().interrupt();
					scroll.postDelayed(scrollrunnableDown, 80);
				} else {
					scroll.postDelayed(scrollrunnableUp, 80);
				}
			} else {
				scroll.post(scrollrunnableRight);
			}
		}
	};
	// 向右滚动
	private Runnable scrollrunnableRight = new Runnable() {
		@SuppressLint("NewApi")
		@Override
		public void run() {
			// Log.e(tag, "Runnable--Right");
			int off = mLayout.getMeasuredWidth() - hscroll.getWidth();
			if (off > 0) {
				hscroll.scrollBy(1, 0);
				if (hscroll.getScrollX() == off) {
					Thread.currentThread().interrupt();
					hscroll.postDelayed(scrollrunnableLeft, 80);
				} else {
					hscroll.postDelayed(scrollrunnableRight, 80);
				}
			} else {
				scroll.post(scrollrunnableDown);
			}
		}
	};
	// 向左滚动
	private Runnable scrollrunnableLeft = new Runnable() {
		@SuppressLint("NewApi")
		@Override
		public void run() {
			// Log.e(tag, "Runnable--Left");
			int off = mLayout.getMeasuredWidth() - hscroll.getWidth();
			if (off > 0) {
				hscroll.scrollBy(-1, 0);
				if (hscroll.getScrollX() == 0) {
					Thread.currentThread().interrupt();
					hscroll.postDelayed(scrollrunnableRight, 80);
				} else {
					hscroll.postDelayed(scrollrunnableLeft, 80);
				}
			} else {
				scroll.post(scrollrunnableDown);
			}
		}
	};

}
