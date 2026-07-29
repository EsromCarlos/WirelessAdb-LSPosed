package dev.wirelessadb.autostart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/** 设计稿里抱着数据线的小机器人。 */
public final class RobotMascotView extends View {
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint soft = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path cable = new Path();
    private final RectF body = new RectF();

    public RobotMascotView(Context context) {
        this(context, null);
    }

    public RobotMascotView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RobotMascotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeCap(Paint.Cap.ROUND);
        stroke.setStrokeJoin(Paint.Join.ROUND);
        soft.setStyle(Paint.Style.FILL);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = resolveSize(dp(136), widthMeasureSpec);
        int h = resolveSize(dp(118), heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    @Override protected void onDraw(Canvas canvas) {
        float s = Math.min(getWidth() / 136f, getHeight() / 118f);
        canvas.save();
        canvas.scale(s, s);
        stroke.setStrokeWidth(2.2f);
        stroke.setColor(0xFF4A526E);

        // body shadow + body
        soft.setColor(0xFF8DABC5);
        body.set(59, 22, 141, 98);
        canvas.drawRoundRect(offset(body, 5, 6), 30, 28, soft);
        fill.setColor(0xFFC8EDF9);
        canvas.drawRoundRect(body, 30, 28, fill);
        canvas.drawRoundRect(body, 30, 28, stroke);

        // eyes
        fill.setColor(0xFFFFFBF6);
        canvas.drawCircle(86, 58, 8, fill);
        canvas.drawCircle(114, 58, 8, fill);
        canvas.drawCircle(86, 58, 8, stroke);
        canvas.drawCircle(114, 58, 8, stroke);
        fill.setColor(0xFF4A526E);
        canvas.drawCircle(86, 58, 2.2f, fill);
        canvas.drawCircle(114, 58, 2.2f, fill);

        // smile
        Path smile = new Path();
        smile.moveTo(96, 72);
        smile.quadTo(100, 78, 104, 72);
        canvas.drawPath(smile, stroke);

        // antenna
        canvas.drawLine(118, 30, 128, 16, stroke);
        fill.setColor(0xFFF9C1D4);
        canvas.drawCircle(102, 10, 9, fill);
        canvas.drawCircle(102, 10, 9, stroke);
        fill.setColor(0xFF4A526E);
        canvas.drawCircle(94, 26, 3.5f, fill);

        // hands
        fill.setColor(0xFFF9C1D4);
        canvas.save();
        canvas.rotate(18, 80, 100);
        canvas.drawRoundRect(70, 88, 94, 116, 12, 12, fill);
        canvas.drawRoundRect(70, 88, 94, 116, 12, 12, stroke);
        canvas.restore();
        canvas.save();
        canvas.rotate(-18, 130, 100);
        canvas.drawRoundRect(118, 88, 142, 116, 12, 12, fill);
        canvas.drawRoundRect(118, 88, 142, 116, 12, 12, stroke);
        canvas.restore();

        // connector
        soft.setColor(0xFFE3BD6D);
        canvas.drawCircle(25, 98, 18, soft);
        fill.setColor(0xFFFFF2AC);
        canvas.drawCircle(20, 96, 18, fill);
        canvas.drawCircle(20, 96, 18, stroke);
        fill.setColor(0xFFFFFBF6);
        canvas.drawCircle(14, 91, 5.5f, fill);
        canvas.drawCircle(27, 91, 5.5f, fill);
        canvas.drawCircle(14, 91, 5.5f, stroke);
        canvas.drawCircle(27, 91, 5.5f, stroke);
        fill.setColor(0xFF4A526E);
        canvas.drawCircle(14, 91, 1.6f, fill);
        canvas.drawCircle(27, 91, 1.6f, fill);

        // cable
        cable.reset();
        cable.moveTo(20, 78);
        cable.cubicTo(37, 78, 27, 106, 49, 106);
        cable.cubicTo(71, 106, 62, 83, 84, 83);
        cable.cubicTo(102, 83, 96, 100, 114, 100);
        cable.rLineTo(16, 0);
        stroke.setStrokeWidth(3f);
        canvas.drawPath(cable, stroke);
        stroke.setColor(0xFFF9C1D4);
        stroke.setStrokeWidth(1.2f);
        canvas.drawPath(cable, stroke);

        canvas.restore();
    }

    private RectF offset(RectF src, float dx, float dy) {
        return new RectF(src.left + dx, src.top + dy, src.right + dx, src.bottom + dy);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
