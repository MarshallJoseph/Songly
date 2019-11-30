package ca.brocku.songly.vis;

import android.content.Context;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

abstract public class AbstractVisualizer extends View {

    protected byte[] bytes;
    protected Paint paint;
    protected Visualizer visualizer;

    public AbstractVisualizer (Context c) {
        super(c);
    }

    public AbstractVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        init();
    }

    public AbstractVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
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
                AbstractVisualizer.this.bytes = waveform;
                invalidate();
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
        visualizer.setEnabled(true);
    }

    private void init(AttributeSet attributeSet) {
        paint = new Paint();
    }

    protected abstract void init();

}
