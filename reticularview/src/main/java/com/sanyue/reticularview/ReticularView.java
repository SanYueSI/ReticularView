package com.sanyue.reticularview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;

public class ReticularView extends View {
    private Paint textPaint;
    private int textWidth = 0;
    private int textHeight = 0;
    private int shapeNumber = 3;
    private float angle ;
    private int width;
    private int height;

    private int cycleNumber=3;

    private int[] bgColors= new int[]{Color.RED};
    private int[] linColors = new int[]{Color.BLUE};

    private float maxValue=20;
    private ArrayList<PointF> tipsPointFS = new ArrayList<>();
    private ArrayList<Values> data;

    private float radius;
    private int centerX, centerY;
    private int regionCircleColor=Color.MAGENTA;
    private boolean isRegionFill;
    private boolean isFill;
    private int linWidth =3;
    private int textSize =12;
    private int textColor = Color.RED;
    private int regionColorAlpha = 255;



    private Build build;

    private Paint regionPaint;
    private Paint linsPaint;
    private Paint polygonPaint;

    private Paint regionCirclePaint;
    private int regionColor= Color.GREEN;

    public ReticularView(Context context) {
        this(context, null);
    }

    public ReticularView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReticularView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //中心坐标
        width= w;
        height =h;
        centerX = w / 2;
        centerY = h / 2;
        init();
        super.onSizeChanged(w, h, oldw, oldh);

    }


    private void init() {
        if(build == null){
           build = new Build();
        }

        //边框
        polygonPaint = new Paint();
        polygonPaint.setColor(bgColors[0]);
        polygonPaint.setStyle(Paint.Style.STROKE);
        polygonPaint.setStrokeWidth(dp2px(linWidth));

        //文字
        textPaint= new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(dp2px(textSize));


        //对角线
        linsPaint = new Paint();
        linsPaint.setStyle(Paint.Style.STROKE);
        linsPaint.setStrokeWidth(dp2px(linWidth));


        //绘制区域
        regionPaint =  new Paint();
        regionPaint.setStyle(Paint.Style.STROKE);
        if(isRegionFill){
            regionPaint.setStyle(Paint.Style.FILL);
        }
        regionPaint.setColor(regionColor);
        regionPaint.setAlpha(regionColorAlpha);
        //绘制区域的原点
        if(regionCircleColor!=0){
            regionCirclePaint = new Paint();
            regionCirclePaint.setColor(regionCircleColor);
            regionCirclePaint.setStyle(Paint.Style.FILL);
        }

        if(data!=null && !data.isEmpty()){
            textWidth = getTextWH(textPaint,data.get(0).getName())[0];
            textHeight = getTextWH(textPaint,data.get(0).getName())[1];
            Log.e("MyView", textWidth + "-*-"+textHeight );
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPolygon(canvas);
        drawLines(canvas);
        drawRegion(canvas);
        drawText(canvas);
    }


    /**
     * 绘制形状
     * @param canvas
     */
    private void drawPolygon(Canvas canvas) {
        radius = Math.min(height - (textHeight * 2), width - (textWidth * 2)) / 2;
        angle = (float) (Math.PI * 2 / shapeNumber);
        float endX = 0, endY = 0;
        Path path = new Path();
        float r = radius / cycleNumber;//r是蜘蛛丝之间的间距

        for (int i = cycleNumber; i >= 1; i--) {
            path.reset();
            float curR = r * i;//当前半径
            if (bgColors.length > 0 && bgColors.length == cycleNumber) {
                if (bgColors.length != cycleNumber) {
                    throw new IndexOutOfBoundsException("背景颜色的数量请对应cycleNumber数量");
                }
                if(isFill){
                    polygonPaint.setStyle(Paint.Style.FILL);
                }
                polygonPaint.setColor(bgColors[i - 1]);
            }
            path.moveTo(centerX, centerY - curR);
            for (int j = 0; j < shapeNumber; j++) {
                endX = (float) (centerX + curR * Math.sin(angle * j));
                endY = (float) (centerY - curR * Math.cos(angle * j));
                path.lineTo(endX, endY);
                if(data!=null && !data.isEmpty()){
                    if (i == cycleNumber) {
                        PointF pointF = new PointF();
                        pointF.setName(data.get(j).getName());
                        pointF.setX((int) endX);
                        pointF.setY((int) endY);
                        tipsPointFS.add(pointF);
                    }
                }

            }
            path.close();
            canvas.drawPath(path, polygonPaint);

        }

    }

    /**
     * 绘制直线
     */
    private void drawLines(Canvas canvas) {
        if(linColors.length==1){
                linsPaint.setColor(linColors[0]);
                Path path = new Path();
                for (int i = 0; i < shapeNumber; i++) {
                    path.reset();
                    path.moveTo(centerX, centerY);
                    float x = (float) (centerX + radius * Math.sin(angle * i));
                    float y = (float) (centerY - radius * Math.cos(angle * i));
                    path.lineTo(x, y);
                    canvas.drawPath(path, linsPaint);

                }
        }else {
            float r = radius / cycleNumber;
            for (int i = cycleNumber; i >= 1; i--) {
                float curR = r * i;//当前半径
                if (linColors.length > 0 && linColors.length == cycleNumber) {
                    linsPaint.setColor(linColors[i - 1]);
                }else {
                    throw new IndexOutOfBoundsException("线条颜色的数量请对应圈数");
                }
                for (int j = 0; j < shapeNumber; j++) {
                    float endX = (float) (centerX + curR * Math.sin(angle * j));
                    float endY = (float) (centerY - curR * Math.cos(angle * j));
                    canvas.drawLine(centerX, centerY, endX, endY, linsPaint);
                }
            }
        }
    }

    /**
     * 绘制区域
     *
     * @param canvas
     */
    private void drawRegion(Canvas canvas) {
        if(data==null ||data.isEmpty()) return;
        if(data.size()!=shapeNumber){
            throw new IndexOutOfBoundsException("data请保持和shapeNumber数量一致");
        }
        Path path = new Path();
        if(isRegionFill){
            regionPaint.setStyle(Paint.Style.FILL);
        }else {
            regionPaint.setStyle(Paint.Style.STROKE);
        }
        regionPaint.setStrokeWidth(10);
        for (int j = 0; j < data.size(); j++) {
            float curR = radius * data.get(j).getValue() / maxValue;//当前半径
            float endX = (float) (centerX + curR * Math.sin(angle * j));
            float endY = (float) (centerY - curR * Math.cos(angle * j));
            if (j == 0) {
                path.moveTo(endX, endY);
            }else {
                path.lineTo(endX, endY);
            }

        }
        path.close();
//        绘制填充区域
        canvas.drawPath(path, regionPaint);

        for (int j = 0; j < data.size(); j++) {
            float curR = radius * data.get(j).getValue() / maxValue;//当前半径
            float endX = (float) (centerX + curR * Math.sin(angle * j));
            float endY = (float) (centerY - curR * Math.cos(angle * j));
            if(regionCirclePaint!=null){
                canvas.drawCircle(endX, endY, 20, regionCirclePaint);
            }
        }


    }

    /**
     *  绘制文字
     * @param canvas
     */

    private void drawText(Canvas canvas){
        for (int i = 0; i < tipsPointFS.size(); i++) {
            int xy[]= getDrawTextXY(tipsPointFS.get(i),textPaint);
            canvas.drawText(tipsPointFS.get(i).getName(), xy[0],
                    xy[1],textPaint);
        }
    }

    public Build drawView(){
        if(build==null){
            build = new Build();
        }
        return  build;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    class PointF {
        String name;
        int x;
        int y;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    public static class Values{
        private String name;
        private float value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    }


    private int[] getTextWH(Paint paint, String str) {
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        int w = rect.width();
        int h = rect.height();
        return new int[]{w, h};
    }



    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, Resources.getSystem().getDisplayMetrics());
    }

    private int[] getDrawTextXY(PointF f, Paint paint){
        int[] wh = getTextWH(paint,f.getName());
        int x = f.getX(),y = f.getY();
        if(x-1>centerX) {
             x = f.getX()+wh[0]/2;
        }

        if(x+1<centerX){
            x = f.getX()-wh[0]/2;
        }

        if(y-1>centerY){
            y = (int) (f.getY() + wh[1]*1.5);
        }
        if(y+1<centerY){
            y = f.getY() - wh[1];
        }

        return new int[]{ x, y};
    }



    public final class Build{

        /**
         * 绘制值的最大值
         * @param max
         * @return
         */
        public Build setMaxValue(float max) {
            maxValue = max;
            return this;
        }

        /**
         * 绘制值的区域线条颜色
         * @param color
         * @return
         */
        public Build setRegionColor(int color) {
            regionColor = color;
            return this;
        }

        /**
         * 绘制值的区域颜色 填充模式以及透明度
         * @param color
         * @param isFill
         * @param alpha
         * @return
         */
        public Build setRegionColor(int color,boolean isFill,int alpha) {
            isRegionFill =isFill;
            regionColor = color;
            regionColorAlpha =alpha;
            return this;
        }

        /**
         * 数据
         * @param values
         * @return
         */
        public Build setData(ArrayList<Values> values) {
            if(values!=null && !values.isEmpty()){
                data = values;
            }
            return this;
        }

        /**
         * 边框线条以及对角线的宽度
         * @param width
         * @return
         */

        public Build setLinWidth(int width) {
            linWidth = width;
            return this;
        }

        /**
         * 文字大小
         * @param size
         * @return
         */
        public Build setTextSize(int size) {
            textSize = size;
            return this;
        }

        /**
         * 文本颜色
         * @param color
         * @return
         */
        public Build setTextColor(int color) {
            textColor = color;
            return this;
        }

        /**
         * 对角线的颜色
         * @param colors
         * @return
         */

        public Build setLinColors(int... colors) {
           linColors = colors;
            return this;
        }

        /**
         * 绘制几何样式
         * @param number
         * @return
         */
        public Build setShapeNumber(int number) {
            if (number<2) {
                throw new IndexOutOfBoundsException("shapeNumber得大于2不然没法绘制几何");
            }
            shapeNumber = number;
            return this;
        }

        /**
         * 绘制圈数
         * @param number
         * @return
         */
        public Build setCycleNumber(int number) {
            cycleNumber = number;
            return this;
        }


        /**
         * 绘制形状的颜色是否填充 支持单个颜色
         * @param fill 是否填充
         * @param colors 颜色
         * @return
         */

        public Build setPolygonBgColors(boolean fill, int... colors) {
            bgColors = colors;
            isFill = fill;
            return this;
        }

        /**
         *
         * 绘制区域上的原点
         * @param circleColor
         * @return
         */
        public Build setDrawRegionCircle(int circleColor) {
            regionCircleColor = circleColor;
            return this;
        }

        //重新绘制图形
        public void build(){
            invalidate();
        }

    }

}