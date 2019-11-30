package ca.brocku.songly.vis.type;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import ca.brocku.songly.vis.AbstractVisualizer;

public class Bar extends AbstractVisualizer {

    private float numBars = 20;
    private int gap = 1;

    public Bar(Context c) {
        super(c);
    }

    public Bar(Context c,
               @Nullable AttributeSet attrs) {
        super(c, attrs);
    }

    public Bar(Context c, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(c, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bytes != null) {
            float barWidth = getWidth() / numBars;
            float div = bytes.length / numBars;
            paint.setStrokeWidth(barWidth - gap);
            for (int i = 0; i< numBars; i++) {
                int bytePosition = (int) Math.ceil(i*div);
                int top = getHeight() + ((byte) (Math.abs(bytes[bytePosition]) + 128)) * getHeight() / 128;
                float barX = (i * barWidth) + (barWidth / 2);
                canvas.drawLine(barX, getHeight(), barX, top, paint);
            }
            super.onDraw(canvas);
        }
    }

}
