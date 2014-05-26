package org.whut.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class BottomTextView extends TextView {

	public BottomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	public BottomTextView(Context context)
	{
		super(context);
	}
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint=new Paint();
		paint.setColor(android.graphics.Color.GRAY);
		canvas.drawLine(0, this.getHeight()-1, this.getWidth()-1, this.getHeight()-1, paint);
		super.onDraw(canvas);
	}
	

}

