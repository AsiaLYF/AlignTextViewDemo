package com.example.textviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by admin on 2016/10/13.
 */

public class AlignTextView extends TextView {

    private static final String TAG = "AlignTextView";
    private static final int SUFFIXMODE_FULL_WIDTH = 1;
    private static final int SUFFIXMODE_HALF_WIDTH = 2;
    private Context context;
    /**文字*/
    private String text;
    /**后缀符号全角或半角*/
    private int suffixMode;
    /**后缀*/
    private String suffixStr;

    public AlignTextView(Context context) {
        this(context, null);
    }

    public AlignTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlignTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;

        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.AlignTextView);
        text= typedArray.getString(R.styleable.AlignTextView_text);
        suffixMode= typedArray.getInt(R.styleable.AlignTextView_suffixMode,0);
        typedArray.recycle();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(getTextSize());
        paint.setColor(getTextColors().getDefaultColor());

        Rect targetRect = new Rect(0,0,getWidth(),getHeight());

        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        //下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        //问题宽度
        float textPx=getTextSize();
        //Textview控件总宽度
        int maxTextViewWidth=getWidth();

        if(TextUtils.isEmpty(text)){
            super.onDraw(canvas);
            return;
        }

        //情况一:无后缀
        if(suffixMode==0){
            char[] chars = text.toCharArray();
            int spec= (int) ((maxTextViewWidth-chars.length*textPx)/(chars.length-1));
            for (int i = 0; i < chars.length; i++) {
                canvas.drawText(String.valueOf(chars[i]), i * (textPx+spec), baseline, paint);
            }
            //情况二:有后缀
        }else{
            //后缀字符
            suffixStr=text.substring(text.length() - 1, text.length());
            //后缀宽度
            int suffixWidth=getSuffixWidth(paint,suffixStr,suffixMode);
            char[] chars = text.substring(0,text.length() - 1).toCharArray();
            int spec= (int) ((maxTextViewWidth-chars.length*textPx-suffixWidth)/(chars.length-1));
            for (int i = 0; i < chars.length; i++) {
                canvas.drawText(String.valueOf(chars[i]), i * (textPx+spec), baseline, paint);
            }
            canvas.drawText(suffixStr,maxTextViewWidth-suffixWidth,baseline,paint);
        }
        super.onDraw(canvas);
    }

    /**
     * 计算后缀的宽度
     * @param paint
     * @param suffixStr
     * @param suffixMode
     * @return
     */
    private int getSuffixWidth(Paint paint, String suffixStr, int suffixMode){
        if(TextUtils.isEmpty(suffixStr) || suffixMode==0){
            return 0;
        }
        Rect rect=new Rect();
        switch (suffixMode){
            case SUFFIXMODE_FULL_WIDTH:
                paint.getTextBounds("啊",0,1,rect);//计算出一个全角字符的宽度
                break;
            case SUFFIXMODE_HALF_WIDTH:
                paint.getTextBounds("a",0,1,rect);//计算出一个半角字符的宽度
                break;
        }
        return rect.width();
    }

    public void setAlingText(String text){
        this.text=text;
        invalidate();
    }

}
