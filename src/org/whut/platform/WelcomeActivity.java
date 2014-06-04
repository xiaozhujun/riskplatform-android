package org.whut.platform;

import java.util.ArrayList;

import org.whut.application.MyApplication;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class WelcomeActivity extends Activity{
		
	//翻页控件
	private ViewPager mViewPager;
	
	//这5个是底部显示当前状态点imageView
	private ImageView mPage0;
	private ImageView mPage1;
	private ImageView mPage2;
	private ImageView mPage3;


	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//去掉标题栏全屏显示
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);        
        mViewPager = (ViewPager)findViewById(R.id.whatsnew_viewpager);
    	MyApplication.getInstance().addActivity(this);
        
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());        
        mPage0 = (ImageView)findViewById(R.id.page0);
        mPage1 = (ImageView)findViewById(R.id.page1);
        mPage2 = (ImageView)findViewById(R.id.page2);
        mPage3 = (ImageView)findViewById(R.id.page3);
          
      /*
       * 这里是每一页要显示的布局，根据应用需要和特点自由设计显示的内容
       * 以及需要显示多少页等
       */
        LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.gallery_one, null);
        View view2 = mLi.inflate(R.layout.gallery_two, null);
        View view3 = mLi.inflate(R.layout.gallery_three, null);
        View view4 = mLi.inflate(R.layout.gallery_four, null);
      	/*
      	 * 这里将每一页显示的view存放到ArrayList集合中
      	 * 可以在ViewPager适配器中顺序调用展示
      	 */
        
        Button btn_start = (Button) view4.findViewById(R.id.button1);
        
        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        
        
        /*
      	 * 每个页面的Title数据存放到ArrayList集合中
      	 * 可以在ViewPager适配器中调用展示
      	 */
        final ArrayList<String> titles = new ArrayList<String>();
        titles.add("tab1");
        titles.add("tab2");
        titles.add("tab3");
        titles.add("tab4");
        
        //填充ViewPager的数据适配器
        MyPagerAdapter mPagerAdapter = new MyPagerAdapter(views,titles);
		mViewPager.setAdapter(mPagerAdapter);
		btn_start.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
    }    
    

   class MyOnPageChangeListener implements OnPageChangeListener {
    	
    	
		public void onPageSelected(int page) {
			
			//翻页时当前page,改变当前状态园点图片
			switch (page) {
			case 0:				
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));
				break;
			case 1:
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page));
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page));
				break;
			case 2:
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));
				mPage3.setImageDrawable(getResources().getDrawable(R.drawable.page));
				break;
			case 3:
				mPage3.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
			//	mPage4.setImageDrawable(getResources().getDrawable(R.drawable.page));
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page));
				break;
//			case 4:
//				mPage4.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
//				mPage3.setImageDrawable(getResources().getDrawable(R.drawable.page));
//				mPage5.setImageDrawable(getResources().getDrawable(R.drawable.page));
//				break;
//			case 5:
//				mPage5.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
//				mPage4.setImageDrawable(getResources().getDrawable(R.drawable.page));
//				mPage6.setImageDrawable(getResources().getDrawable(R.drawable.page));
//				break;
//			case 6:
//				mPage6.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
//				mPage5.setImageDrawable(getResources().getDrawable(R.drawable.page));
//				break;
			}
		}
		
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		
		public void onPageScrollStateChanged(int arg0) {
		}
	    
    }
    
    class MyPagerAdapter extends PagerAdapter{
    	private ArrayList<View> views;
    	@SuppressWarnings("unused")
    	private ArrayList<String> titles;
    	public MyPagerAdapter(ArrayList<View> views,ArrayList<String> titles){
    		
    		this.views = views;
    		this.titles = titles;
    	}
    	
    	@Override
    	public int getCount() {
    		return this.views.size();
    	}

    	@Override
    	public boolean isViewFromObject(View arg0, Object arg1) {
    		return arg0 == arg1;
    	}
    	
    	public void destroyItem(View container, int position, Object object) {
    		((ViewPager)container).removeView(views.get(position));
    	}
    	
    	//页面view
    	public Object instantiateItem(View container, int position) {
    		((ViewPager)container).addView(views.get(position));
    		return views.get(position);
    	}
    }

	
}
