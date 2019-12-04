package ca.brocku.songly.vis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BarVisualizer extends View {

    protected byte[] bytes;
    protected Paint paint;
    protected Visualizer visualizer;

    private float numBars = 20;
    private int gap = 1;


    public BarVisualizer(Context c) {
        super(c);
    }

    public BarVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void release() {
        visualizer.release();
        bytes = null;
        invalidate();
    }

    public void visualizerSetup (int audioSessionId) {
        visualizer = new Visualizer(audioSessionId);
        visualizer.setEnabled(false);
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                BarVisualizer.this.bytes = waveform;
                invalidate();
            }
            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
        visualizer.setEnabled(true);
    }

    protected void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Source: GautamChibde/android-audio-visualizer
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
