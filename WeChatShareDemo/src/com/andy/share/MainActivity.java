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
	 * ��ť����¼���ǰ������WXShare�ļ��������������APP_ID�����������΢�ŷ�����
	 * @param v
	 */
	public void onButtonClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.shareText:
			// ��һ������Ϊtrue,��ʾ�����ı�������Ȧ
			WXShare.getInstance(this).shareTextMessage(true, "�����ı���Ϣ����", "�ı�����");
			break;
		case R.id.shareImg:
			// ��һ������Ϊfalse,��ʾ����ͼƬ��ĳ��΢�ź���
			Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			WXShare.getInstance(this).shareImgMessage(false, bMap);
			break;
		case R.id.shareWebPage:
			// ��һ������Ϊtrue,��ʾ������ҳ������Ȧ
			String webPageUrl = "http://www.baidu.com/";
			String title = "��ҳ����";
			String description = "������ҳ����������";
			Bitmap webImg = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			WXShare.getInstance(this).shareWebPage(true, webPageUrl, title, description, webImg);
			break;
		default:
			break;
		}
	}

}
