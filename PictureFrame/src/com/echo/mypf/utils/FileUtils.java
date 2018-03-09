package com.echo.mypf.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.echo.mypf.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class FileUtils {
	public static String tag = "FileUtils";
	private static final String CACHE = "/Pictures";

	public static ArrayList<String> getImageFilePath(Context context,
			String whichPath) {

		ArrayList<String> arrayList = new ArrayList<String>();
		// 指定要查询的uri资源
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		// 获取ContentResolver
		ContentResolver contentResolver = context.getContentResolver();
		// 查询的字段
		String[] projection = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE };
		// 排序
		String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
		Cursor cursor = contentResolver.query(uri, projection, null, null,
				sortOrder);
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			String fPath = cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media.DATA));
			if (fPath.substring(0, 8).equals(whichPath)) {
				arrayList.add(fPath);
			}
			while (cursor.moveToNext()) {
				// 获得图片所在的路径(可以使用路径构建URI)
				String tPath = cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
				if (tPath.substring(0, 8).equals(whichPath)) {
					arrayList.add(tPath);
				}

			}
			// 关闭cursor
			cursor.close();
		}
		return arrayList;
	}

	// public static ArrayList<String> getImageSource(Activity activity) {
	// CustomApplication customApplication = (CustomApplication)
	// activity.getApplication();
	// return customApplication.getImagePath();
	// }

	public static void ImgLoader(String path, ImageView iv) {

		ImageLoader imageLoader = ImageLoader.getInstance();
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading)
				// 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.error)
				// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.error)
				// 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true)
				// 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true)
				// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true)
				// .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565)
				// .displayer(new RoundedBitmapDisplayer(5)) // 设置成圆角图片
				.build(); // 构建完成
		imageLoader.displayImage(path, iv, options);

	}

	public static Uri getUriFromPath(Context context, String path) {
		Uri mUri = Uri.parse("content://media/external/images/media");
		Uri mImageUri = null;
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			String data = cursor.getString(cursor
					.getColumnIndex(MediaStore.MediaColumns.DATA));
			if (path.equals(data)) {
				int ringtoneID = cursor.getInt(cursor
						.getColumnIndex(MediaStore.MediaColumns._ID));
				mImageUri = Uri.withAppendedPath(mUri, "" + ringtoneID);
				break;
			}
			cursor.moveToNext();
		}
		return mImageUri;
	}

	public static void saveImage(Bitmap bitmap, String imageName)
			throws Exception {
		String filePath = isExistsFilePath();

		FileOutputStream fos = null;
		// File file = new File("/storage/emulated/0/Copy", imageName);
		File file = new File(imageName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			if (null != fos) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
				if (!bitmap.isRecycled()) {
					bitmap.recycle();
					System.gc();
				}
				Log.e("fos:", "successed");
			} else {
				Log.e("fos:", "failed");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取sd卡的缓存路径， 一般在卡中sdCard就是这个目录
	 * 
	 * @return SDPath
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取根目录
		} else {
			Log.e("ERROR", "没有内存卡");
		}
		return sdDir.toString();
	}

	/**
	 * 获取缓存文件夹目录 如果不存在创建 否则则创建文件夹
	 * 
	 * @return filePath
	 */
	private static String isExistsFilePath() {
		String filePath = getSDPath() + CACHE;
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return filePath;
	}

	public static int deleteImage(Context mContext, String deleteImgPath) {
		File file = new File(deleteImgPath);
		if (file == null || !file.exists() || file.isDirectory()) {
			Log.e(tag, "file is null or not exists:" + deleteImgPath);
		} else {
			Log.e(tag, "delete start" + deleteImgPath);
			file.delete();
			Log.e(tag, "delete success" + deleteImgPath);
		}
		Uri uri = getUriFromPath(mContext, deleteImgPath);
		ContentResolver cr = mContext.getContentResolver();
		int i = cr.delete(uri, null, null);
		return i;
	}

	public static void copyImage(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			Log.e("newfile:", "" + newPath);
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				Log.e("oldfile:", "" + oldfile);
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				File file = new File(newPath);
				Log.e(tag, "file:" + file.toString());
				if (!file.exists()) {
					file.createNewFile();
				}
				FileOutputStream fs = new FileOutputStream(file);
				byte[] buffer = new byte[100 * 1024];
				// byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				FileDescriptor fd = fs.getFD();
				fd.sync();
				inStream.close();
				fs.flush();
				fs.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	public static int deleteImages(Context mContext,
			ArrayList<String> deleteImgPaths) {
		int flag = 0;
		for (int i = 0; i < deleteImgPaths.size(); i++) {
			File file = new File(deleteImgPaths.get(i));
			if (file == null || !file.exists() || file.isDirectory()) {
			} else {
				file.delete();
				Log.e(tag, "delete success" + deleteImgPaths.get(i));
			}
			Uri uri = getUriFromPath(mContext, deleteImgPaths.get(i));
			ContentResolver cr = mContext.getContentResolver();
			flag = cr.delete(uri, null, null);
			Log.e(tag, "delete success:" + flag);
		}
		return 0;
	}

	// public static void copyfile(File fromFile, File toFile, Boolean rewrite)
	// {
	// if (!fromFile.exists()) {
	// return;
	// }
	// if (!fromFile.isFile()) {
	// return;
	// }
	// if (!fromFile.canRead()) {
	// return;
	// }
	// if (!toFile.getParentFile().exists()) {
	// toFile.getParentFile().mkdirs();
	// }
	// if (toFile.exists() && rewrite) {
	// toFile.delete();
	// }
	// // 当文件不存时，canWrite一直返回的都是false
	// // if (!toFile.canWrite()) {
	// // MessageDialog.openError(new Shell(),"错误信息","不能够写将要复制的目标文件" +
	// // toFile.getPath());
	// // Toast.makeText(this,"不能够写将要复制的目标文件", Toast.LENGTH_SHORT);
	// // return ;
	// // }
	// try {
	// java.io.FileInputStream fosfrom = new java.io.FileInputStream(
	// fromFile);
	// java.io.FileOutputStream fosto = new FileOutputStream(toFile);
	// byte bt[] = new byte[1024];
	// int c;
	// while ((c = fosfrom.read(bt)) > 0) {
	// fosto.write(bt, 0, c); // 将内容写到新文件当中
	// }
	// fosfrom.close();
	// fosto.close();
	// } catch (Exception ex) {
	// Log.e("readfile", ex.getMessage());
	// }
	//
	// }

	// 复制文件
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		if (true) {
			// 若复盖则进行拷贝
			// 新建文件输入流并对它进行缓冲
			LOG(" _____ before FileInputStream");
			FileInputStream input = new FileInputStream(sourceFile);
			BufferedInputStream inBuff = new BufferedInputStream(input);

			// 新建文件输出流并对它进行缓冲
			LOG(" _____ before FileOutputStream");
			FileOutputStream output = new FileOutputStream(targetFile);
			LOG(" _____ before BufferedOutputStream");
			BufferedOutputStream outBuff = new BufferedOutputStream(output);
			// 缓冲数组
			LOG(" _____ before new byte");
			byte[] b = new byte[4 * 1024];
			int len;
			LOG(" _____ before copy while");
			while ((len = inBuff.read(b)) != -1) {
				if (true) {
					outBuff.write(b, 0, len);
				} else {
					// 若被中断则结束拷贝

					LOG("+++++++++++++++is_enable_copy == false");
					break;
				}
			}
			LOG(" _____ after  copy while");
			// 刷新此缓冲的输出流
			outBuff.flush();
			// 发送sync广播，通知设备把拷贝的内容写入外接设备中
			FileDescriptor fd = output.getFD();
			fd.sync();

			// 关闭流
			inBuff.close();
			outBuff.close();
			output.close();
			input.close();
		}
	}

	private static void LOG(String string) {
		Log.e("test", string);
	}
}
