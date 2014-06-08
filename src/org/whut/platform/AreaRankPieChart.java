package org.whut.platform;

import java.util.HashMap;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.whut.client.CasClient;
import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;

public class AreaRankPieChart extends Activity {
	String[] Name = { "风险等级 0", "风险等级  1", "风险等级  2", "风险等级  3", "风险等级  4", "风险等级  5","风险等级  6", "风险等级  7", "风险等级  8", "风险等级  9","风险等级  10"};
	
	
	int[] RankValues;
	private static int[] COLORS = new int[] {Color.GRAY,Color.parseColor("#00FFFF"),Color.parseColor("#00FF7F"),Color.parseColor("#20B2AA"),Color.parseColor("#32CD32"),Color.parseColor("#FFFF00"),Color.parseColor("#DAA520"),Color.parseColor("#FFA500"),Color.parseColor("#FF4500"),Color.parseColor("#FF1493"),Color.RED};
	
	private CategorySeries mSeries = new CategorySeries("");
	private DefaultRenderer mRenderer = new DefaultRenderer();
	private GraphicalView mChartView;
	private ProgressDialog dialog;
	
	private String province;
	private String city;
	private String area;
	
	
	public Myhandle handler;
	private ImageView lefe_btn;
	private TextView tv_topbar_middle_detail;
	private TextView tv_topbar_right_map;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setTitle("Pie Chart");
		setContentView(R.layout.activity_rankpiechart);
		
		
		//对话框初始化
		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在描绘图形，请稍后...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
	
	
		
		province=getIntent().getStringExtra("province");
		city=getIntent().getStringExtra("city");
		area=getIntent().getStringExtra("area");
		
		lefe_btn = (ImageView) findViewById(R.id.iv_topbar_left_back);
		lefe_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		tv_topbar_middle_detail = (TextView) findViewById(R.id.tv_topbar_middle_detail);
		tv_topbar_middle_detail.setText(province+city+area);

		
		tv_topbar_right_map = (TextView) findViewById(R.id.tv_topbar_right_map);
		tv_topbar_right_map.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		handler=new Myhandle();
		new Thread(new AreaThread()).start();
		
		mRenderer.setApplyBackgroundColor(true);

		mRenderer.setChartTitle("风险分布");
		mRenderer.setChartTitleTextSize(60);
	
		mRenderer.setLabelsTextSize(40);
		mRenderer.setLegendTextSize(40);
		
		mRenderer.setLabelsColor(Color.BLACK);
		
		
		mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
	    
	    mRenderer.setZoomEnabled(true); 
		mRenderer.setStartAngle(180);

		if (mChartView == null) 
		{
			LinearLayout layout = (LinearLayout) findViewById(R.id.PieChart);
			mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
			mRenderer.setClickEnabled(true);
			mRenderer.setSelectableBuffer(10);
			layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		else 
		{
			mChartView.repaint();
		}
		
		mChartView.setOnClickListener(new View.OnClickListener() 
		{  
			@Override  
			public void onClick(View v) 
			{  
				SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();  
			  
				if (seriesSelection == null) 
				{  
				//	Toast.makeText(AreaRankPieChart.this,"No chart element was clicked",Toast.LENGTH_SHORT).show();  
				}
				else 
				{  
					Toast.makeText(AreaRankPieChart.this, Name[(seriesSelection.getPointIndex())] + " : " + seriesSelection.getValue()+"%", Toast.LENGTH_SHORT).show();  
				}  
			}  
		});		
		
		mChartView.setOnLongClickListener(new View.OnLongClickListener() 
		{  
			@Override  
			public boolean onLongClick(View v) 
			{  
				SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();  
				
				if (seriesSelection == null) 
				{  
					//Toast.makeText(AreaRankPieChart.this,"No chart element was long pressed", Toast.LENGTH_SHORT).show();  
					return false;   
				}
				else 	
				{  
					//Toast.makeText(AreaRankPieChart.this, Name[seriesSelection.getPointIndex()], Toast.LENGTH_SHORT).show();  
					return true;         
				}  
			}  
		});
	}

	@SuppressLint("HandlerLeak")
	class Myhandle extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			if (msg.what==1) {
				fillPieChart();	
				dialog.dismiss();
			}
		}
		
	}
	
	class AreaThread implements Runnable {

		@Override
		public void run() {

			HashMap<String, String> params = new HashMap<String, String>();
				params.put("province", province);
				params.put("city", city);
				params.put("area", area);
				Message msg = Message.obtain();
				String message = CasClient.getInstance().doPost(
						UrlStrings.GET_AREA_INFO, params);
				try {
					RankValues=JsonUtils.GetRankStatistics(message);
					msg.what = 1;
				} catch (Exception e) {

					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}

	
	public void fillPieChart() 
	{
		int sum=0;
		for(int i:RankValues)
		{
			sum+=i;
		}
		for (int i = 0; i < RankValues.length; i++) 
		{
			mSeries.add(Name[i] + " (" + (int)(100*(RankValues[i])/sum) + "%) ", RankValues[i]);			
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
			mRenderer.addSeriesRenderer(renderer);
			if (mChartView != null)
				mChartView.repaint();
		}
	}
}
