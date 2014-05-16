package com.andy.share;

import com.andy.share.wxapi.WXShare;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	/**
	 * 按钮点击事件，前提是在WXShare文件中配置你申请的APP_ID，否则调不起微信分享功能
	 * @param v
	 */
	public void onButtonClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.shareText:
			// 第一个参数为true,表示分享文本到朋友圈
			WXShare.getInstance(this).shareTextMessage(true, "分享文本消息内容", "文本描述");
			break;
		case R.id.shareImg:
			// 第一个参数为false,表示分享图片给某个微信好友
			Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			WXShare.getInstance(this).shareImgMessage(false, bMap);
			break;
		case R.id.shareWebPage:
			// 第一个参数为true,表示分享网页到朋友圈
			String webPageUrl = "http://www.baidu.com/";
			String title = "网页标题";
			String description = "分享网页的内容描述";
			Bitmap webImg = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			WXShare.getInstance(this).shareWebPage(true, webPageUrl, title, description, webImg);
			break;
		default:
			break;
		}
	}

}
