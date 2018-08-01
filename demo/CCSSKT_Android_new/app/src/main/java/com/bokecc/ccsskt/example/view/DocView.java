package com.bokecc.ccsskt.example.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.util.DensityUtil;
import com.bokecc.sskt.CCInteractSession;
import com.bokecc.sskt.IDocView;
import com.bokecc.sskt.doc.DrawInfo;
import com.bokecc.sskt.doc.LinePoint;
import com.bokecc.sskt.doc.PageInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.graphics.Bitmap.createBitmap;


/**
 * 展示doc的view类
 */
public class DocView extends ImageView implements IDocView {

    private final class BGSize {
        int width;
        int height;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BGSize bgSize = (BGSize) o;

            if (width != bgSize.width) return false;
            return height == bgSize.height;

        }

        @Override
        public int hashCode() {
            int result = width;
            result = 31 * result + height;
            return result;
        }
    }

    private static final String TAG = DocView.class.getSimpleName();

    private ExecutorService executorService;
    private Context context;
    private Handler handler;
    private Bitmap pptBitmap;
    private Bitmap mRenderedBitmap;
    private Bitmap mBlankRenderedBitmap, mPageRenderedBitmap;
    private Canvas renderedCanvas;
    private int currentPage = -1;
    private String currentDocId = "WhiteBorad";
    private String currentFileName = "WhiteBorad";
    private String currentColor ;
    private String currentDrawId;
    private String currentdrawid;
    private float currentSize = 1.5f;
    private DrawInfo drawInfo;
    private PageInfo pageInfo;
    private boolean isBlankBitmap = true;
    private BGSize mBGSize = new BGSize();
    private BGSize mBitSize = new BGSize();
    private int mRole;
    private int availableWidth, availableHeight;
    private int drawWidth, drawHeight;

    private Paint mPaint;
    private Path mPath;
    private float mLastX;
    private float mLastY;
    private boolean isInterceptor = false, canDraw = false;

    public static Map<String, Map<Integer, ArrayList<ArrayList<LinePoint>>>> drawingData = new HashMap<>(); // 绘图信息结构
    private ArrayList<LinePoint> linePoints; // 单条数据

    public DocView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        handler = new Handler(Looper.getMainLooper());
        renderedCanvas = new Canvas();
        this.drawInfo = new DrawInfo();
        executorService = Executors.newFixedThreadPool(5);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        // 设置尺寸 web1 3 5  Android2.5f 7.5f 12.5f
        mPaint.setStrokeWidth(currentSize);

    }

    private synchronized void addPageGraphics(String docId, int pageIndex, ArrayList<LinePoint> points) {
        if (drawingData.containsKey(docId)) {
            if (drawingData.get(docId).containsKey(pageIndex)) {
                drawingData.get(docId).get(pageIndex).add(points);
            } else {
                ArrayList<ArrayList<LinePoint>> list = new ArrayList<>();
                list.add(points);
                drawingData.get(docId).put(pageIndex, list);
            }
        } else {
            ArrayList<ArrayList<LinePoint>> list = new ArrayList<>();
            Map<Integer, ArrayList<ArrayList<LinePoint>>> map = new HashMap<>();
            list.add(points);
            map.put(pageIndex, list);
            drawingData.put(docId, map);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isInterceptor) {
            int top, bottom, left, right;
            final float x = event.getX();
            final float y = event.getY();
            top = (getHeight() - drawHeight) / 2;
            bottom = top + drawHeight;
            left = (getWidth() - drawWidth) / 2;
            right = left + drawWidth;
//            Log.e(TAG, "dispatchTouchEvent: [ " + left + "-" + top + "-" + right + "-" + bottom + " ]");
            if ((x >= left && x <= right) && (y >= top && y <= bottom)) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastX = x;
                        mLastY = y;
                        if (mPath == null) {
                            mPath = new Path();
                        }
                        mPath.moveTo(x, y);
                        currentDrawId = CCInteractSession.getInstance().getUserIdInPusher() + System.currentTimeMillis();
                        LinePoint linePoint = new LinePoint(currentDrawId, (x - left) / drawWidth, (y - top) / drawHeight);
                        linePoints = new ArrayList<>();
                        linePoints.add(linePoint);
                        canDraw = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mPath != null) {
                            mPath.reset();
                        }
                        if (canDraw) {
                            CCInteractSession.getInstance().sendLine(currentFileName, currentPage, currentDocId, drawWidth, drawHeight,
                                    currentColor, currentSize, linePoints, currentDrawId);
                            addPageGraphics(currentDocId, currentPage, linePoints);
                            canDraw = false;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (canDraw) {
                            mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                            linePoint = new LinePoint(currentDrawId, (x - left) / drawWidth, (y - top) / drawHeight);
                            linePoints.add(linePoint);
                            mLastX = x;
                            mLastY = y;
                        } else {
                            if (mPath != null) {
                                mPath.reset();
                            }
                        }
                        invalidate();
                        break;
                }
            } else {
                if (mPath != null) {
                    mPath.reset();
                }
                if (canDraw && CCInteractSession.getInstance().isRoomLive()) {
                    CCInteractSession.getInstance().sendLine(currentFileName, currentPage, currentDocId, drawWidth, drawHeight,
                            currentColor, currentSize, linePoints, currentDrawId);
                    addPageGraphics(currentDocId, currentPage, linePoints);
                    canDraw = false;
                }
            }
            return true;
        } else {
            if (mPath != null) {
                mPath.reset();
            }
        }
        return super.dispatchTouchEvent(event);
    }
    private DocWebView mDocWebView;
    public void setDocSetVisibility(DocWebView mDocView){
        this.mDocWebView = mDocView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
    }

    public void reset() {
        canDraw = false;
    }

    public void setTouchInterceptor(boolean isInterceptor,int role) {
        this.isInterceptor = isInterceptor;
        this.mRole = role;
        if(mRole == 0){
            currentColor = String.valueOf(Integer.parseInt("e33423", 16));
            mPaint.setColor(Color.parseColor("#e33423"));
        } else if(mRole == 1) {
            currentColor = String.valueOf(Integer.parseInt("4a9fda", 16));
            mPaint.setColor(Color.parseColor("#4a9fda"));
        } else {
            currentColor = String.valueOf(Integer.parseInt("4a9fda", 16));
            mPaint.setColor(Color.parseColor("#4a9fda"));
        }
    }

    public void setStrokeWidth(float width) {
        currentSize = width;
        mPaint.setStrokeWidth(width);
    }

    public void setColor(int color, int colorStr) {
        currentColor = Integer.toString(colorStr);
        mPaint.setColor(color);
    }

    public void undo() {
        if (drawingData.get(currentDocId) != null) {
            ArrayList<ArrayList<LinePoint>> list = drawingData.get(currentDocId).get(currentPage);
            if (list != null && !list.isEmpty()) {
                String drawid = list.get(list.size() - 1).get(0).drawid;
                list.remove(list.size() - 1);
                CCInteractSession.getInstance().undo(currentFileName, currentPage, currentDocId, drawid); // 发通知
            }
        }
    }

    public void setWhiteboard(int width, int height,boolean isWhite) {
        mBGSize.width = width;
        mBGSize.height = height;
        mBlankRenderedBitmap = Config.sDocBG.get(mBGSize.hashCode());
        if (mBlankRenderedBitmap == null) {
            mBlankRenderedBitmap = createBitmap(width, height, Bitmap.Config.RGB_565);
            // 这个地方使用了一个静态的map作为内存缓冲的作用，是和当前的业务逻辑结合的。当切换布局的时候，造成view不断的被创建，但是绘制画板的缓冲背景是么有必要被不断的创建的
            Config.sDocBG.put(mBGSize.hashCode(), mBlankRenderedBitmap);
        }
        if(isWhite){
            pageInfo = new PageInfo();
        }
        startDrawing();
    }

    /**
     * 设置背景图片
     *
     * @param pageInfo 当前页的信息
     */
    public void setDocBackground(final PageInfo pageInfo) {
        canDraw = false;
        this.pageInfo = pageInfo;
        currentPage = pageInfo.getPageIndex();
        currentDocId = pageInfo.getDocId();
        currentFileName = pageInfo.getFileName();
        if(pageInfo.isUseSDK()){
            mDocWebView.setPPTBackground(pageInfo);
            isBlankBitmap = true;
            startDrawing();
            return ;
        }
        if ("#".equals(pageInfo.getPageUrl())) {
            isBlankBitmap = true;
            startDrawing();
            return;
        } else {
            isBlankBitmap = false;
        }
        super.post(new Runnable() {
            @Override
            public void run() {
                mDocWebView.setVisibility(View.GONE);
            }
        });

        String url;
        if (pageInfo.getPageUrl().startsWith("http") ||
                pageInfo.getPageUrl().startsWith("https")) {
            url = pageInfo.getPageUrl();
        } else {
            url = "https:" + pageInfo.getPageUrl();
        }
        runOnUIThread(new ImageTask(url));

    }

    public void setDocBackground(String url, int currentPage, String currentDocId, String currentFileName) {
        this.currentFileName = currentFileName;
        this.currentDocId = currentDocId;
        this.currentPage = currentPage;
        handler.post(new ImageTask(url));
    }

    public void rotate(boolean isRotate) {
        float scale, scale1;
        if (isRotate) {
            scale = (float) (DensityUtil.getHeight(context) * 1.0 / DensityUtil.getWidth(context));
            scale1 = (float) (drawWidth * 1.0 / drawHeight);

            if (scale >= scale1) {
                drawHeight = DensityUtil.getWidth(context);
                drawWidth = (int) (drawHeight * scale1);
            } else {
                drawWidth = DensityUtil.getHeight(context);
                drawHeight = (int) (drawWidth / scale1);
            }

            if (pptBitmap != null) {
                pptBitmap = getBitmap(pptBitmap, drawWidth);
            }

        } else {
            scale = (float) (mBGSize.width * 1.0 / mBGSize.height);
            scale1 = (float) (availableWidth * 1.0 / availableHeight);

            if (scale >= scale1) {
                drawHeight = mBGSize.height;
                drawWidth = (int) (drawHeight * scale1);
            } else {
                drawWidth = mBGSize.width;
                drawHeight = (int) (drawWidth / scale1);
            }

            if (pptBitmap != null) {
                pptBitmap = getBitmap(pptBitmap, drawWidth);
            }
        }
    }


    private void runOnUIThread(Runnable runnable) {
        handler.post(runnable);
    }

    private Runnable drawTask = new Runnable() {
        @Override
        public void run() {
            startDrawing();
        }
    };

    private final class DrawDataTask implements Runnable {

        public JSONObject data;

        public DrawDataTask(JSONObject data) {
            this.data = data;
        }

        @Override
        public void run() {
            drawInfo.addDrawInfo(data);
            currentdrawid = drawInfo.getDrawid();
            // 判断当前背景图片是否加载完成
            if (pageInfo == null || pageInfo.getPageIndex() != currentPage || !pageInfo.getDocId().equals(currentDocId)) {
                return;
            }
            handler.post(drawTask);
        }
    }

    /**
     * 绘图
     *
     * @param jsonObject 绘图信息
     */
    public void drawContent(final JSONObject jsonObject) {
        // 劣势 不断的创建对象
        executorService.submit(new DrawDataTask(jsonObject));
        // 在UI线程执行 数据量大 可能会卡 或者出现 ANR
//        drawInfo.addDrawInfo(drawJson);
//        // 判断当前背景图片是否加载完成
//        if (pageInfo == null || pageInfo.getPageIndex() != currentPage || !pageInfo.getDocId().equals(currentDocId)) {
//            return;
//        }
//        handler.post(drawTask);
    }

    @Override
    public void drawContent(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                drawContent(new JSONObject(jsonObject.getString("data")));
            } catch (JSONException ignored) {
            }
        }
    }
    private Bitmap renderedBitmap;
    private void startDrawing() {

        if (isBlankBitmap) {
            availableWidth = mBGSize.width;
            availableHeight = mBGSize.height;
            drawWidth = getWidth();
            drawHeight = getHeight();
            if (drawWidth == 0) {
                drawWidth = mBGSize.width;
                drawHeight = mBGSize.height;
            }
            if(pageInfo.isUseSDK()) {
                renderedBitmap = Bitmap.createBitmap(availableWidth, availableHeight, Bitmap.Config.ARGB_8888);
                renderedCanvas.setBitmap(renderedBitmap);
            } else {
                mRenderedBitmap = mBlankRenderedBitmap;
                renderedCanvas.setBitmap(mRenderedBitmap);
            }

            Paint paint = new Paint();
            if(pageInfo.isUseSDK()){
                super.post(new Runnable() {
                    @Override
                    public void run() {
                        setBackgroundColor(Color.argb(0,255,255,255));
                    }
                });
                paint.setARGB(0, 255, 255, 255);
                renderedCanvas.drawRect(0, 0, availableWidth, availableHeight, paint);
            } else {

                paint.setARGB(255, 255, 255, 255);
                renderedCanvas.drawRect(0, 0, availableWidth, availableHeight, paint);
            }
        } else {
            if (pptBitmap == null) {
                return;
            }
            setBackgroundColor(Color.parseColor("#a6000000"));
            mBitSize.width = drawWidth;
            mBitSize.height = drawHeight;
            mPageRenderedBitmap = Config.sDocBG.get(mBitSize.hashCode());
            if (mPageRenderedBitmap == null) {
                mPageRenderedBitmap = createBitmap(drawWidth, drawHeight, Bitmap.Config.RGB_565);
                Config.sDocBG.put(mBitSize.hashCode(), mPageRenderedBitmap);
            }
            mRenderedBitmap = mPageRenderedBitmap;
            renderedCanvas.setBitmap(mPageRenderedBitmap);
            renderedCanvas.drawBitmap(pptBitmap, 0, 0, null);

        }

        if (drawInfo != null && pageInfo != null) {
            drawInfo.startDrawing(pageInfo, renderedCanvas, drawWidth, drawHeight);
        }
        post(new Runnable() {
            @Override
            public void run() {
                if(pageInfo.isUseSDK()){
                    setImageBitmap(renderedBitmap);
                } else {
                    setImageBitmap(mRenderedBitmap);
                }
            }
        });

    }

    private Bitmap getBitmap(Bitmap bitmap, int width) {
        // TODO: 2017/10/24  优化加一个缓冲机制
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Log.e(TAG, "[ " + drawWidth + "-" + drawHeight + " ] [ " + w + "-" + h + " ]");
        float scale = (float) width / w;
        int height = (int) (h * scale);
        mBitSize.width = width;
        mBitSize.height = height;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * 清除view的绘图信息
     */
    public void clear() {
        post(new Runnable() {
            @Override
            public void run() {
                if(!pageInfo.isUseSDK()){
                    setImageBitmap(mRenderedBitmap);
                }
            }
        });
        if(drawingData.get(currentDocId) != null){
            if(drawInfo != null){
                drawInfo.clear();
                CCInteractSession.getInstance().clear(currentFileName, currentPage, currentDocId, currentdrawid);
            }
        }
    }
    public void clearAll(){
        post(new Runnable() {
            @Override
            public void run() {
                setImageBitmap(mRenderedBitmap);
            }
        });
        drawInfo.clearAll();
    }
    public void recycle() {
        if (pptBitmap != null) {
            pptBitmap.recycle();
            pptBitmap = null;
        }
        if (mPageRenderedBitmap != null) {
            mPageRenderedBitmap.recycle();
            mPageRenderedBitmap = null;
        }
        if (mRenderedBitmap != null) {
            mRenderedBitmap = null;
        }
    }

    private void startScale(final Bitmap resource) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                availableWidth = resource.getWidth();
                availableHeight = resource.getHeight();
                // 计算可绘制区域
                float scale, scale1;
                scale = (float) (getWidth() * 1.0 / getHeight());
                scale1 = (float) (availableWidth * 1.0 / availableHeight);
                if (scale >= scale1) {
                    drawHeight = getHeight();
                    drawWidth = (int) (drawHeight * scale1);
                } else {
                    drawWidth = getWidth();
                    drawHeight = (int) (drawWidth / scale1);
                }
                // 缩放pptBitmap
                Bitmap bitmap = getBitmap(resource, drawWidth);
                // TODO: 2017/10/22  做一个缓冲架构 关联尺寸的
                // https://github.com/googlesamples/android-DisplayingBitmaps
                // https://github.com/chrisbanes/Android-BitmapCache
                pptBitmap = bitmap;
                post(new Runnable() {
                    @Override
                    public void run() {
                        startDrawing();
                    }
                });
            }
        }).start();
    }

    private final class ImageTask implements Runnable {

        private String url;

        public ImageTask(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            Glide.with(context.getApplicationContext()).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    startScale(resource);
                }
            });
        }
    }

}
