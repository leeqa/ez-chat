package com.abidingtech.rednewsapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.callback.ClickListenerCallback;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

public class MessageSwipeToReply extends ItemTouchHelper.Callback {
    private  static MessageSwipeToReply _this;
    private ClickListenerCallback clickListenerCallback;
    Context mContext;

    private View mView;
    private boolean swipeBack=false;
    private Drawable imageDrawable;
    private  boolean startTrack=false;
    private float dX=0f;
    private RecyclerView.ViewHolder currentItemViewHolder;
    private long lastReplyButtonAnimationTime=0;
    private float replyButtonProgress=0f;
    private Drawable shareRound;
    private boolean isVibrate;


    public  static MessageSwipeToReply getInstance(ClickListenerCallback clickListenerCallback,Context context){
        return _this=new MessageSwipeToReply(clickListenerCallback,context);
    }

    public MessageSwipeToReply(ClickListenerCallback clickListenerCallback, Context mContext) {
        this.clickListenerCallback = clickListenerCallback;
        this.mContext = mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        mView=viewHolder.itemView;
        imageDrawable=mContext.getDrawable(R.drawable.ic_redo);
        shareRound=mContext.getDrawable(R.drawable.button_background);
        return  ItemTouchHelper.Callback.makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection)
    {
        if(swipeBack){
            swipeBack=false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(recyclerView, viewHolder);
        }
        if (mView.getTranslationX() < convertTodp(130) || dX < this.dX) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            this.dX = dX;
            startTrack = true;
        }
        currentItemViewHolder = viewHolder;
        drawReplyButton(c);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void drawReplyButton(Canvas c) {
        if (currentItemViewHolder == null) {
            return;
        }
        float translationX = mView.getTranslationX();
        long newTime = System.currentTimeMillis();
        long dt = Math.min(17, newTime - lastReplyButtonAnimationTime);
        lastReplyButtonAnimationTime = newTime;
        boolean showing = translationX >= convertTodp(30);
        if (showing) {
            if (replyButtonProgress < 1.0f) {
                replyButtonProgress += dt / 180.0f;
                if (replyButtonProgress > 1.0f) {
                    replyButtonProgress = 1.0f;
                } else {
                    mView.invalidate();
                }
            }
        } else if (translationX <= 0.0f) {
            replyButtonProgress = 0f;
            startTrack = false;
        } else {
            if (replyButtonProgress > 0.0f) {
                replyButtonProgress -= dt / 180.0f;
                if (replyButtonProgress < 0.1f) {
                    replyButtonProgress = 0f;
                } else {
                    mView.invalidate();
                }
            }
        }
        int alpha;
        float scale;
        if (showing) {
            if (replyButtonProgress <= 0.8f) {
               scale= 1.2f * (replyButtonProgress / 0.8f);
            } else {
                scale=1.2f - 0.2f * ((replyButtonProgress - 0.8f) / 0.2f);
            }
            alpha = (int) Math.min(255f, 255 * (replyButtonProgress / 0.8f));
        } else {
            scale = replyButtonProgress;
            alpha = (int) Math.min(255f, 255 * replyButtonProgress);
        }
        shareRound.setAlpha(alpha);

        imageDrawable.setAlpha(alpha);
//        if (startTrack) {
//            if (mView.getTranslationX() >= convertTodp(100)) {
//                mView.performHapticFeedback(
//                        HapticFeedbackConstants.KEYBOARD_TAP,
//                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
//                );
//                isVibrate = true;
//            }
//        }

        int x;
    if (mView.getTranslationX() > convertTodp(130)) {
            x=convertTodp(130) / 2;
        } else {
            x=(int)(mView.getTranslationX() / 2);
        }

        float y = (mView.getTop() + mView.getMeasuredHeight() / 2);
    shareRound.setColorFilter(Color.TRANSPARENT,PorterDuff.Mode.MULTIPLY);
//        PorterDuffColorFilter(ContextCompat.getColor(mContext,R.color.transparent_background), PorterDuff.Mode.MULTIPLY)

        shareRound.setBounds(
                (int)(x - convertTodp(18) * scale),
                (int)(y - convertTodp(18) * scale),
                (int)(x + convertTodp(18) * scale),
                (int)(y + convertTodp(18) * scale)
        );
        shareRound.draw(c);
        imageDrawable.setBounds(
                (int) (x - convertTodp(12) * scale),
                (int) (y - convertTodp(11) * scale),
                (int) (x + convertTodp(12) * scale),
                (int) (y + convertTodp(10) * scale)
        );
        imageDrawable.draw(c);
        shareRound.setAlpha(255);
        imageDrawable.setAlpha(255);
    }

    private void setTouchListener(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        recyclerView.setOnTouchListener((v, event) -> {
            swipeBack =(event.getAction()== MotionEvent.ACTION_CANCEL|| event.getAction()==MotionEvent.ACTION_UP);
            if (swipeBack) {
                if (Math.abs(mView.getTranslationX()) >= MessageSwipeToReply.this.convertTodp(130)) {
                    clickListenerCallback.setBrandId(viewHolder.getAdapterPosition());
                }
            }
            return false;
        });
    }
    private int convertTodp(int pixel) {
        return (int) (pixel / ((float) mContext.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        //return AndroidUtils.dp(pixel.toFloat(), context)
        }
}
