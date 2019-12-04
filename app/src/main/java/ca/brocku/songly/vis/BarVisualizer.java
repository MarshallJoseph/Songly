package ca.brocku.songly.vis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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

    private float[] points;
    private int radius;
    private boolean radiusInitialized = true;

    Boolean bar = false;
    Boolean circle = true;
    Boolean line = false;

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
        if (bar) {
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
        if (circle) {
            if (radiusInitialized) {
                radius = getHeight() < getWidth() ? getHeight() : getWidth();
                radius = (int) (radius * 0.65 / 2);
                radiusInitialized = false;
                paint.setStrokeWidth(5);
            }
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paint);
            if (bytes != null) {
                if (points == null || points.length < bytes.length * 4) {
                    points = new float[bytes.length * 4];
                }
                double angle = 0;
                for (int i = 0; i < 120; i++, angle += 3) {
                    int x = (int) Math.ceil(i * 8.5);
                    int t = ((byte) (-Math.abs(bytes[x]) + 128)) * (getHeight() / 4) / 128;

                    points[i * 4] = (float) (getWidth() / 2
                            + radius
                            * Math.cos(Math.toRadians(angle)));

                    points[i * 4 + 1] = (float) (getHeight() / 2
                            + radius
                            * Math.sin(Math.toRadians(angle)));

                    points[i * 4 + 2] = (float) (getWidth() / 2
                            + (radius + t)
                            * Math.cos(Math.toRadians(angle)));

                    points[i * 4 + 3] = (float) (getHeight() / 2
                            + (radius + t)
                            * Math.sin(Math.toRadians(angle)));
                }
                canvas.drawLines(points, paint);
            }
            super.onDraw(canvas);
        }
        if (line) {
            if (bytes != null) {
                Paint middleLine = new Paint();
                middleLine.setColor(paint.getColor());
                float barWidth = getWidth() / numBars;
                float div = bytes.length / numBars;
                canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, middleLine);
                paint.setStrokeWidth(barWidth - gap);
                for (int i = 0; i < numBars; i++) {
                    int bytePosition = (int) Math.ceil(i * div);
                    int top = getHeight() / 2
                            + (128 - Math.abs(bytes[bytePosition]))
                            * (getHeight() / 2) / 128;

                    int bottom = getHeight() / 2
                            - (128 - Math.abs(bytes[bytePosition]))
                            * (getHeight() / 2) / 128;

                    float barX = (i * barWidth) + (barWidth / 2);
                    canvas.drawLine(barX, bottom, barX, getHeight() / 2, paint);
                    canvas.drawLine(barX, top, barX, getHeight() / 2, paint);
                }
                super.onDraw(canvas);
            }
        }

    }

    public void setPaintColour(int colour) {
        paint.setColor(colour);
    }

    public void moreBars() {
        if (bar || line) {
            if (numBars < 50) {
                numBars++;
            }
        }
        if (circle) {
            if (paint.getStrokeWidth() < 10) {
                paint.setStrokeWidth(paint.getStrokeWidth()+1);
            }
        }
    }

    public void lessBars() {
        if (bar || line) {
            if (numBars > 1) {
                numBars--;
            }
        }
        if (circle) {
            if (paint.getStrokeWidth() > 1) {
                paint.setStrokeWidth(paint.getStrokeWidth()-1);
            }
        }
    }

    public void moreGap() {
        if (bar || line) {
            if (gap < 10) {
                gap++;
            }
        }
        if (circle) {
            if (radius < 400) {
                System.out.println(radius);
                radius+=5;
            }
        }
    }

    public void lessGap() {
        if (bar || line) {
            if (gap > 10) {
                gap--;
            }
        }
        if (circle) {
            if (radius > 1) {
                radius-=5;
            }
        }
    }

    public void setVisualizerType(Boolean bar, Boolean circle, Boolean line) {
        radiusInitialized = true;
        this.bar = bar;
        this.circle = circle;
        this.line = line;
    }

}
