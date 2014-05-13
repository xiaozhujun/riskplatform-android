package org.whut.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class BoderTextView extends TextView {

	public BoderTextView(Context context) {
		super(context);
		
	}
	public BoderTextView(Context context,AttributeSet attrs)
	{
		super(context,attrs);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint=new Paint();
		paint.setColor(android.graphics.Color.DKGRAY);
		
		canvas.drawLine(this.getWidth()-1, 0, this.getWidth()-1, this.getHeight()-1, paint);
		super.onDraw(canvas);
	}
	
}
