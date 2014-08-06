package com.andy.share.wxapi;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class WXShare {

	private static final String APP_ID = "your_appid";
	private static WXShare instance = null;
	private static IWXAPI api = null;
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001; // 4.2 ����֧�ַ��͵�����Ȧ
	private Context mContext;
	
	private WXShare(Context context) {
		this.mContext = context;
	}
	
	/**
	 * ע��΢��
	 * @param mContext
	 * @return
	 */
	public static WXShare getInstance(Context context) {
		if (instance == null) {			
			//΢�ŷ����ʼ���� ͨ��WXAPIFactory��������ȡIWXAPI��ʵ��
	    	api = WXAPIFactory.createWXAPI(context, APP_ID, false);
	    	api.registerApp(APP_ID);
	    	instance = new WXShare(context);
		}
		
		return instance;
	}
	
	/**
	 * ����WXAppExtendObject���͵����ݣ�ֻ�ܷ��������
	 * @param title ����
	 * @param imgPath ͼƬ·��
	 * @param description  ���ݵļ������
	 * @param extInfo ������Ϣ��������ת��ֵ����תĿ���������ַ��� ����2KB��С
	 */
	public void shareAppDataToFriend(String title, String imgPath, String description, String extInfo) {
		try {
			WXAppExtendObject appdata = new WXAppExtendObject();
			//appdata.filePath:Local directory of the file provided for applications 
			//NOTE: The length should be within 10KB and content size within 10MB.
			//appdata.filePath��appdata.fileData����һ
			String path = imgPath;
			appdata.fileData = Util.readFromFile(path, 0, -1);
			appdata.extInfo = extInfo;

			final WXMediaMessage msg = new WXMediaMessage();
			// ���б�Ҫ��ͼƬͬ��Ҫ�����Ŵ����ɲο�ͼƬ����
			msg.setThumbImage(Util.extractThumbNail(path, 150, 150, true));
			msg.title = title;
			msg.description = description;
			msg.mediaObject = appdata;
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("appdata");
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneSession;
			if (api != null) {
				api.sendReq(req);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * ΢�ŷ����ı���Ϣ
	 * @param isTimeLine �Ƿ��������Ȧ��falseΪ���������
	 * @param text �ı�����
	 */
	public void shareTextMessage(boolean isTimeLine, String text, String description) {
		try {
			if (text == null || text.length() == 0) {
				return;
			}		
			// ��ʼ��һ��WXTextObject����
			WXTextObject textObj = new WXTextObject();
			textObj.text = text;

			// ��WXTextObject�����ʼ��һ��WXMediaMessage����
			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = textObj;
			// �����ı����͵���Ϣʱ��title�ֶβ�������
			// msg.title = "Will be ignored";
			msg.description = description;

			// ����һ��Req
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("text"); // transaction�ֶ�����Ψһ��ʶһ������
			req.message = msg;
			if (isTimeLine == false) {
				req.scene = SendMessageToWX.Req.WXSceneSession;
			} else {
				if (isSupportTimeLine()) {
					req.scene = SendMessageToWX.Req.WXSceneTimeline;
				} else {
					Toast.makeText(mContext, "����΢�Ű汾��֧�ַ�������Ȧ", Toast.LENGTH_SHORT).show();
					return;
				}
			}				
			// ����api�ӿڷ������ݵ�΢��
			api.sendReq(req);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * ΢�ŷ���ͼƬ��Ϣ
	 * @param isTimeLine �Ƿ��������Ȧ��falseΪ���������
	 * @param bMap ͼƬ
	 */
	public void shareImgMessage(boolean isTimeLine, Bitmap bMap) {
		try {			
			if(bMap == null){				
				return;
			}
			// Ϊ��֤�ܽ�Ϊ�����ķ����Ȱ��������ѹ����С����ѹ��������ֱ������ͼС��32k
			final int thumb_size_height = 600;
			Bitmap bmp = bMap;
			int bitmapHeight = bmp.getHeight();
			int bitmapWidth = bmp.getWidth();
			final int thumb_size_width = bitmapWidth * thumb_size_height / bitmapHeight;
			WXImageObject imgObj = new WXImageObject(bmp);
			
			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = imgObj;
			//msg.description = "����ͼƬ";
			
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, thumb_size_width, thumb_size_height, true);
			bmp.recycle();
			byte[] outByteArray = Util.compressImage2ByteArray(thumbBmp, true);
			if (outByteArray.length / 1024 >= 32) {
				Toast.makeText(mContext, "��Ǹ������ͼƬ̫�󣬷���ʧ�ܣ�", Toast.LENGTH_SHORT).show();
				return;
			}
			msg.thumbData = outByteArray;// ��������ͼ  NOTE: The file size should be within 32KB.
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("img");
			req.message = msg;
			if (isTimeLine == false) {
				req.scene = SendMessageToWX.Req.WXSceneSession;
			} else {
				if (isSupportTimeLine()) {
					req.scene = SendMessageToWX.Req.WXSceneTimeline;
				} else {
					Toast.makeText(mContext, "����΢�Ű汾��֧�ַ�������Ȧ", Toast.LENGTH_SHORT).show();
					return;
				}
			}	
			api.sendReq(req);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	 
	/**
	 * ΢�ŷ�����ҳ��Ϣ
	 * @param isTimeLine �Ƿ��������Ȧ��falseΪ���������
	 * @param webPageUrl ��ҳ����ַ
	 * @param title ����
	 * @param description ��������
	 * @param bMap ��ҳ�����е����СͼƬ
	 */
	public void shareWebPage(boolean isTimeLine, String webPageUrl, String title, String description, Bitmap bMap) {
		try {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = webPageUrl;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = title;
			msg.description = description;
			Bitmap thumb = bMap;
			byte[] outByteArray = Util.compressImage2ByteArray(thumb, true);
			if (outByteArray.length / 1024 >= 32) {
				Toast.makeText(mContext, "��Ǹ������ͼƬ̫�󣬷���ʧ�ܣ�", Toast.LENGTH_SHORT).show();
				return;
			}
			msg.thumbData = outByteArray;
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			if (isTimeLine == false) {
				req.scene = SendMessageToWX.Req.WXSceneSession;
			} else {
				if (isSupportTimeLine()) {
					req.scene = SendMessageToWX.Req.WXSceneTimeline;
				} else {
					Toast.makeText(mContext, "����΢�Ű汾��֧�ַ�������Ȧ", Toast.LENGTH_SHORT).show();
					return;
				}
			}	
			api.sendReq(req);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Transaction ID corresponding to this request.
	 * @param type
	 * @return
	 */
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	/**
	 * ����΢��
	 * @return
	 */
	public boolean lanuchWX() {
		return api.openWXApp();
	}	
    
	/**
	 * �Ƿ�֧�ַ��͵�����Ȧ
	 * @return
	 */
    public boolean isSupportTimeLine() {
    	int wxSdkVersion = api.getWXAppSupportAPI();
		if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
			return true;
		} else {
			return false;
		}
    }
    
    /**
     * ΢��App�Ƿ��Ѱ�װ
     * @return
     */
    public boolean isWXAppInstalled() {
    	try {
    		return api.isWXAppInstalled();	
		} catch (Exception e) {
			return false;
		}    		
    }
    
    /**
     * ����onReq��onResp��Ӧ
     * ��WXEntryActivity��onCreate�е���
     * @param intent
     * @param handler
     */
    public void handleIntent(Intent intent, IWXAPIEventHandler handler) {
    	if (api != null) {
    		api.handleIntent(intent, handler);
    	}        
    }
}
