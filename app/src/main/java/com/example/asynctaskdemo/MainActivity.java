package com.example.asynctaskdemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	private ProgressBar progressBar1;
	private ImageView iv_pic;
	private Button bt_cancle;
	private Button bt_down;
	private ImageLoader loader;
	private static final String ImageUrl = "http://i1.cqnews.net/sports/attachement/jpg/site82/2011-10-01/2960950278670008721.jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		loader = new ImageLoader();

		
	}

	private void initView() {
		bt_down = (Button) findViewById(R.id.bt_down);
		bt_cancle = (Button) findViewById(R.id.bt_cancle);

		bt_down.setOnClickListener(this);
		bt_cancle.setOnClickListener(this);
		bt_cancle.setEnabled(false);

		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar1.setVisibility(View.GONE);
		iv_pic = (ImageView) findViewById(R.id.iv_pic);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_down:
			 loader.execute(ImageUrl);

			break;

		case R.id.bt_cancle:
			loader.cancel(true);
			Toast.makeText(getApplicationContext(), "取消下载", 0).show();
			break;

		}
		
	}
	
	class ImageLoader extends AsyncTask<String,Integer,Bitmap>{

		//���� �����÷���  ���Զ� ui���� ��ʼ���Ĳ���
		protected void onPreExecute() {
			bt_cancle.setEnabled(true);
			bt_down.setEnabled(true);
			progressBar1.setVisibility(View.VISIBLE);
			progressBar1.setProgress(0);
		}
		//ʵ�� ��ʱ���� 
		protected Bitmap doInBackground(String... url) {
			// TODO Auto-generated method stub
			
			 try {
				  URL u;
				  HttpURLConnection conn = null;
				  InputStream in = null;
				  OutputStream out = null;
				  final String filename = "local_temp_image";
				  try {
				      u = new URL(url[0]);
				      conn = (HttpURLConnection) u.openConnection();
				      conn.setDoInput(true);
				      conn.setDoOutput(false);
				      conn.setConnectTimeout(20 * 1000);
				      in = conn.getInputStream();
				      out = openFileOutput(filename, Context.MODE_PRIVATE);
				      byte[] buf = new byte[8196];
				      int seg = 0;
				      final long total = conn.getContentLength();
				      long current = 0;
				      
				      while (!isCancelled() && (seg = in.read(buf)) != -1) {
				   out.write(buf, 0, seg);
				   current += seg;
				   int progress = (int) ((float) current / (float) total * 100f);
				   //onProgressUpdate(Progress...),��publishProgress���������ú�UI �߳̽�������������Ӷ��ڽ�����չʾ����Ľ�չ���������ͨ��һ������������չʾ��
				   publishProgress(progress);
				   SystemClock.sleep(1000);
				      }
				  } finally {
				      if (conn != null) {
				   conn.disconnect();
				      }
				      if (in != null) {
				   in.close();
				      }
				      if (out != null) {
				   out.close();
				      }
				  }
				  return BitmapFactory.decodeFile(getFileStreamPath(filename).getAbsolutePath());
				     } catch (MalformedURLException e) {
				  e.printStackTrace();
				     } catch (IOException e) {
				  e.printStackTrace();
				     }
				     return null;

		}
		
		//doInBackground ִ����� ���� ��� ������ cancle()  doInBackground�Ի�ִ�� ���� ������� onPostExecute ����
		protected void onPostExecute(Bitmap result) {
			  if (result != null) {
				  iv_pic.setImageBitmap(result);
				     }
			  progressBar1.setProgress(100);
			  progressBar1.setVisibility(View.GONE);
			  bt_down.setEnabled(false);

		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			progressBar1.setProgress(values[0]);
		}
		
	}

	
}
