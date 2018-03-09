package com.echo.mypf.custom;

public class Echo {

	//存储类型路径
	public static final String PATH_TYPE = "storage_path";
	public static final String POSITION = "img_position";
	public static final String PATH_SD = "/mnt/ext";
	public static final String PATH_USB = "/mnt/usb";
	public static final String PATH_FLASH = "/storage";
	public static final String PATH_FLASH_UMS = "/mnt/int";
	//共享首选项
	public static final String SHARED_PREFERENCES_NAME = "share_preferences";
	public static final String MAIN_FOCUS_POSITION = "focus_saved";
	public static final String PATH_TYPE_SP = "path_type";
	public static final String POSITION_SP = "posistion";
	public static final String BRIGHTNESS_SP = "brightness";
	public static final String LANGUAGE_SP = "language";
	public static final String SPLIT_SP = "split";
	public static final String TRANSITION_SP = "transition";
	public static final String INTERVAL_SP = "interval";
	public static final String SHUFFLE_SP = "shuffle";
	public static final String PHOTO_INFO_SP = "photo_info";
	public static final String PHOTO_FIT_SP = "photo_fit";
	//存储设备挂载广播类型
	public static final String ACTION_MEDIA_REMOVED = "android.intent.action.MEDIA_REMOVED";
	public static final String ACTION_MEDIA_UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED";
	public static final String ACTION_MEDIA_CHECKING = "android.intent.action.MEDIA_CHECKING";
	public static final String ACTION_MEDIA_MOUNTED = "android.intent.action.MEDIA_MOUNTED";
	public static final String ACTION_MEDIA_BAD_REMOVAL = "android.intent.action.MEDIA_BAD_REMOVAL";
	public static final String ACTION_MEDIA_EJECT = "android.intent.action.MEDIA_EJECT";
	public static final String ACTION_MEDIA_SCANNER_STARTED = "android.intent.action.MEDIA_SCANNER_STARTED";
	public static final String ACTION_MEDIA_SCANNER_FINISHED = "android.intent.action.MEDIA_SCANNER_FINISHED";
	public static final String ACTION_MEDIA_SCANNER_SCAN_FILE = "android.intent.action.MEDIA_SCANNER_SCAN_FILE";
	//遥控键上报值
	public static final int KEY_SLIDESHOW = 131;
	public static final int KEY_MENU = 82;
	public static final int KEY_CALL_TIME = 132;
	public static final int KEY_BACK = 4;
	public static final int KEY_ZOOM = 134;
	public static final int KEY_EDIT = 137;
	public static final int KEY_SELECT = 136;
	public static final int KEY_ROTATE = 133;
	public static final int KEY_UP = 19;
	public static final int KEY_DOWN = 20;
	public static final int KEY_LEFT = 21;
	public static final int KEY_RIGHT = 22;
	public static final int KEY_ENTER = 66;
	
	//图片粘贴路径
	public static final String FLASH_PASTE_PATH = "/storage/emulated/0/Pictures/";
	public static final String FLASH_UMS_PASTE_PATH = "/mnt/internal_sd/Pictures/";
	public static final String SD_PASTE_PATH = "/mnt/external_sd/Pictures/";
	public static final String USB_PASTE_PATH = "/mnt/usb_storage/Pictures/";
	
	public static final String PASTE_IMAGES = "paste_images";
	public static final String PASTE_TYPE = "paste_type";
	
	public static final String ZOOM_ROTATE = "zoom_rotate";
	
	public static final String IS_WAKEUP = "isWakeUp";
	public static final String HOUR_SCREEN_ON_SP = "hour_screen_on";
	public static final String MINUTE_SCREEN_ON_SP = "minute_screen_on";
	public static final String AM_SCREEN_ON_SP = "am_screen_on";
	public static final String MODE_SCREEN_ON_SP = "mode_screen_on";
	public static final String HOUR_SCREEN_OFF_SP = "hour_screen_off";
	public static final String MINUTE_SCREEN_OFF_SP = "minute_screen_off";
	public static final String AM_SCREEN_OFF_SP = "am_screen_off";
	public static final String MODE_SCREEN_OFF_SP = "mode_screen_off";
	public static final int AlarmID[] = {0,1,2,3,4,5,6,7};
	
}
