/* Copyright (c) 2010-2013 Pierre LEVY androidsoft.org
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.androidsoft.games.slowit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.text.MessageFormat;
import org.androidsoft.games.utils.level.LevelManager;

/**
 *
 * @author Pierre Levy
 */
class GameView extends SurfaceView implements SurfaceHolder.Callback
{

    private static final int EVENT_LEVEL_DONE = 1;
    private static final int EVENT_LEVEL_FAILED = 2;
    private static final String MSG_DATA_EVENT = "event";
    private static final String MSG_DATA_LEVEL = "level";
    private static final String MSG_DATA_SCORE = "score";
    private Context mContext;
    /**
     * The thread that actually draws the animation
     */
    private GameThread thread;
    private float mPreviousX;
    private float mPreviousY;
    private Activity mActivity;
    private Ball ball;
    private LevelManager mLevelManager;
    private int mScore;
    private int mStatus;

    public GameView(Context context, AttributeSet attrs, Activity activity, Bundle savedInstanceState, LevelManager levelManager)
    {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mActivity = activity;
        mLevelManager = levelManager;
        mContext = context;

        thread = new GameThread(holder, context, new Handler()
        {
            @Override
            public void handleMessage(Message m)
            {
                int event = m.getData().getInt(MSG_DATA_EVENT);
                int level = mLevelManager.getLevel();
                int score = m.getData().getInt(MSG_DATA_SCORE);
                switch (event)
                {
                    case EVENT_LEVEL_DONE:
                        onLevelDone(level, score);
                        break;

                    case EVENT_LEVEL_FAILED:
                        onLevelFailed(level, score);
                        break;

                    default:
                        break;


                }
            }
        });


        if (savedInstanceState == null)
        {
            thread.setState(GameThread.STATE_READY);
        }
        else
        {
            thread.restoreState(savedInstanceState);
        }


        setFocusable(true);
    }

    void onLevelDone(int level, int score)
    {
        Resources res = mContext.getResources();
        String title = res.getString(R.string.title_level_done);
        Object[] args =
        {
            level, score
        };
        String message = MessageFormat.format(res.getString(R.string.message_level_done), args);
        String button = res.getString(R.string.next_level);
        int icon = R.drawable.icon_level_done;
        mScore = score;
        mStatus = 1;
        if (mScore > 15)
        {
            mStatus = 2;
        }
        if (mScore > 30)
        {
            mStatus = 3;
        }
        if (score > mLevelManager.getHiScore())
        {
            title = res.getString(R.string.title_hiscore);
            message = MessageFormat.format(res.getString(R.string.message_hiscore), args);
            button = res.getString(R.string.button_continue);
            icon = R.drawable.icon_hiscore;
        }
        showLevelDialog(title, message, icon, button);
    }

    void onLevelFailed(int level, int score)
    {
        Resources res = mContext.getResources();
        String title = res.getString(R.string.title_level_failed);
        String message = res.getString(R.string.message_level_failed, level);
        String button = res.getString(R.string.redo_level);
        int icon = R.drawable.icon;
        mScore = -1;
        mStatus = 0;
        showLevelDialog(title, message, icon, button);
    }

    void showLevelDialog(String title, String message, int icon, String button)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setIcon(icon);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(button,
                new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
                mLevelManager.end(mScore, mStatus);
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(R.string.quit),
                new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                mActivity.finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        if (!thread.mRun)
        {
            thread.doStart();
        }
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                ball = thread.mBalls.getNearestBall(x, thread.mCanvasHeight - y);
                mPreviousX = x;
                mPreviousY = y;
                for (Ball b : thread.mBalls)
                {
                    Log.d(Constants.LOG_TAG, "Ball #" + b.mIndex + " X=" + b.mX + " Y=" + b.mY);

                }
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                ball.setVelocity(dx, -dy);
                mPreviousX = x;
                mPreviousY = y;
                break;

            default:
                break;
        }
        return true;
    }

    /**
     * Standard window-focus override. Notice focus lost so we can pause on
     * focus lost. e.g. user switches to take a call.
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus)
    {
        if (!hasWindowFocus)
        {
            thread.pause();
        }
        else
        {
            thread.unpause();
        }
    }


    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        thread.setSurfaceSize(width, height);
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder)
    {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        if (!thread.mRun)
        {
            thread.setRunning(true);
            thread.start();
            thread.doStart();
        }
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry)
        {
            try
            {
                thread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    /**
     * Thread managing the drawing
     */
    class GameThread extends Thread
    {

        private static final String KEY_LEVEL = "mLevel";
        private static final String KEY_SCORE = "mScore";
        private static final String KEY_LEVEL_DURATION = "mLevelDuration";
        private static final String KEY_RADIUS = "mRadius";
        private static final String KEY_MODE = "mMode";
        private static final String KEY_RUN = "mRun";
        private static final String KEY_TIME_LEVEL = "mTimeLevel";
        public static final int STATE_LEVEL_FAILED = 1;
        public static final int STATE_PAUSE = 2;
        public static final int STATE_READY = 3;
        public static final int STATE_RUNNING = 4;
        public static final int STATE_WIN = 5;
        public static final int STATE_GAME_OVER = 6;
        private static final int LEVEL_DURATION = 40;
        private static final int SLOWIT_DURATION = 10;
        // drawing variables
        private final SurfaceHolder mSurfaceHolder;
        private final Handler mHandler;
        private final Paint mInsideTextPaint;
        private final Paint mTimeTextPaint;
        private final Paint mOutsidePaint;
        private final Paint mInsidePaint;
        private Bitmap mBackgroundImage;
        private int mCanvasHeight = 1;
        private int mCanvasWidth = 1;
        private int mLevel;
        private int mMode;
        private boolean mRun = false;
        private boolean mInside;
        private int mTimeInside;
        private double mStartInside;
        private double mStartLevel;
        private int mTimeLevel;
        private BallList mBalls;
        private int mRadius;
        private int mScore;
        private int mLevelDuration;

        public GameThread(SurfaceHolder surfaceHolder, Context context, Handler handler)
        {
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;
            mMode = STATE_READY;

            Resources res = context.getResources();
            Drawable background;
            switch (mLevelManager.getGrid())
            {
                case 0:
                    background = res.getDrawable(R.drawable.background1);
                    break;
                case 1:
                    background = res.getDrawable(R.drawable.background2);
                    break;
                case 2:
                    background = res.getDrawable(R.drawable.background3);
                    break;
                case 3:
                    background = res.getDrawable(R.drawable.background4);
                    break;
                case 4:
                    background = res.getDrawable(R.drawable.background5);
                    break;
                default:
                    background = res.getDrawable(R.drawable.background1);
                    break;
            }
            mBackgroundImage = drawableToBitmap(background);

            mInsideTextPaint = new Paint();
            mInsideTextPaint.setAntiAlias(true);
            mInsideTextPaint.setARGB(255, 255, 255, 255);
            mInsideTextPaint.setTextSize(40);
            mInsideTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mInsideTextPaint.setTextAlign(Paint.Align.CENTER);


            mTimeTextPaint = new Paint();
            mTimeTextPaint.setAntiAlias(true);
            mTimeTextPaint.setARGB(100, 255, 255, 255);
            mTimeTextPaint.setTextSize(100);
            mTimeTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mTimeTextPaint.setTextAlign(Paint.Align.CENTER);

            mOutsidePaint = new Paint();
            mOutsidePaint.setAntiAlias(true);
            mOutsidePaint.setARGB(100, 0, 0, 0);
            mInsidePaint = new Paint();
            mInsidePaint.setAntiAlias(true);
            mInsidePaint.setARGB(100, 255, 255, 255);

            BallList.setBallImage(res.getDrawable(R.drawable.ball));
            Log.d(Constants.LOG_TAG, "GameThread constructor");


        }

        public final Bitmap drawableToBitmap(Drawable drawable)
        {
            if (drawable instanceof BitmapDrawable)
            {
                return ((BitmapDrawable) drawable).getBitmap();
            }

//            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
            Bitmap bitmap = Bitmap.createBitmap(50, 50, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        }

        /**
         * Starts the game, setting parameters for the current difficulty.
         */
        public void doStart()
        {
            synchronized (mSurfaceHolder)
            {
                Log.d(Constants.LOG_TAG, "doStart");
                setState(STATE_RUNNING);
                mTimeLevel = 0;
                mStartLevel = System.currentTimeMillis();
                mInside = false;
                mTimeInside = -1;
                mLevel = mLevelManager.getLevel() + 10 * mLevelManager.getGrid();
                int nBallCount = 1 + (mLevel / 7);
                float fDensity = mContext.getResources().getDisplayMetrics().density;
                mRadius = (int) ((50f - 5f * (mLevel % 7) + nBallCount * 10f) * fDensity);
                int nInitialVelocity = 5 * (mLevel % 7);
                mBalls = new BallList(mCanvasWidth, mCanvasHeight, nBallCount, nInitialVelocity);
                mLevelDuration = LEVEL_DURATION + 20 * nBallCount;
            }
        }

        /**
         * Pauses the physics update & animation.
         */
        public void pause()
        {
            synchronized (mSurfaceHolder)
            {
                Log.d(Constants.LOG_TAG, "pause");
                if (mMode == STATE_RUNNING)
                {
                    setState(STATE_PAUSE);
                }
            }
        }

        /**
         * Resumes from a pause.
         */
        public void unpause()
        {
            // Move the real time clock up to now
            synchronized (mSurfaceHolder)
            {
                Log.d(Constants.LOG_TAG, "unpause");
                if (mBalls != null)
                {
                    mBalls.setLastTime(System.currentTimeMillis() + 100);
                }
                mStartLevel = System.currentTimeMillis() - mTimeLevel * 1000.0;

            }
            setState(STATE_RUNNING);
        }

        /**
         * Dump game state to the provided Bundle. Typically called when the
         * Activity is being suspended.
         *
         * @return Bundle with this view's state
         */
        public Bundle saveState(Bundle map)
        {
            synchronized (mSurfaceHolder)
            {
                Log.d(Constants.LOG_TAG, "saveState");
                if (map != null)
                {
                    map.putBoolean(KEY_RUN, mRun);
                    map.putInt(KEY_MODE, mMode);
                    map.putInt(KEY_LEVEL, mLevel);
                    map.putInt(KEY_LEVEL_DURATION, mLevelDuration);
                    map.putInt(KEY_RADIUS, mRadius);
                    map.putInt(KEY_SCORE, mScore);
                    map.putInt(KEY_TIME_LEVEL, mTimeLevel);
                    mBalls.saveState(map);
                }
            }
            return map;
        }

        /**
         * Restores game state from the indicated Bundle. Typically called when
         * the Activity is being restored after having been previously
         * destroyed.
         *
         * @param savedState Bundle containing the game state
         */
        public synchronized void restoreState(Bundle savedState)
        {
            synchronized (mSurfaceHolder)
            {
                Log.d(Constants.LOG_TAG, "restoreState");
                setState(STATE_PAUSE);
                if (mBalls != null)
                {
                    mBalls.restoreState(savedState);
                }
                mMode = savedState.getInt(KEY_MODE);
                mLevel = savedState.getInt(KEY_LEVEL);
                mLevelDuration = savedState.getInt(KEY_LEVEL_DURATION);
                mScore = savedState.getInt(KEY_SCORE);
                mRadius = savedState.getInt(KEY_RADIUS);
                mTimeLevel = savedState.getInt(KEY_TIME_LEVEL);

            }
        }

        @Override
        public void run()
        {
            while (mRun)
            {
                Canvas c = null;
                try
                {
                    c = mSurfaceHolder.lockCanvas(null);
                    if (c != null)
                    {
                        synchronized (mSurfaceHolder)
                        {
                            if (mMode == STATE_RUNNING)
                            {
                                updatePhysics();
                            }
                            doDraw(c);
                        }
                    }
                }
                finally
                {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null)
                    {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        /**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         *
         * @param b true to run, false to shut down
         */
        public void setRunning(boolean b)
        {
            mRun = b;
        }

        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         *
         * @see #setState(int, CharSequence)
         * @param mode one of the STATE_* constants
         */
        public void setState(int mode)
        {
            synchronized (mSurfaceHolder)
            {
                setState(mode, null);
            }
        }

        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         *
         * @param mode one of the STATE_* constants
         * @param message string to add to screen or null
         */
        public void setState(int mode, CharSequence message)
        {
            /*
             * This method optionally can cause a text message to be displayed
             * to the user when the mode changes. Since the View that actually
             * renders that text is part of the main View hierarchy and not
             * owned by this thread, we can't touch the state of that View.
             * Instead we use a Message + Handler to relay commands to the main
             * thread, which updates the user-text View.
             */
            synchronized (mSurfaceHolder)
            {
                Log.d(Constants.LOG_TAG, "setState:" + mode);
                mMode = mode;

                if ((mMode == STATE_WIN) || (mMode == STATE_LEVEL_FAILED))
                {
                    int event = EVENT_LEVEL_FAILED;

                    if (mMode == STATE_WIN)
                    {
                        event = EVENT_LEVEL_DONE;
                    }


                    Message msg = mHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putInt(MSG_DATA_EVENT, event);
                    b.putInt(MSG_DATA_LEVEL, mLevel);
                    b.putInt(MSG_DATA_SCORE, mScore);
                    msg.setData(b);
                    mHandler.sendMessage(msg);
                }
            }
        }

        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height)
        {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder)
            {
                mCanvasWidth = width;
                mCanvasHeight = height;

                BallList.setSurfaceSize(width, height);

                // don't forget to resize the background image
                mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
            }
        }

        private void doDraw(Canvas canvas)
        {
            Resources res = mContext.getResources();
            canvas.drawBitmap(mBackgroundImage, 0, 0, null);

            if (mInside)
            {
                canvas.drawCircle(mCanvasWidth / 2, mCanvasHeight / 2, mRadius, mInsidePaint);
                canvas.drawText("" + (SLOWIT_DURATION - mTimeInside), mCanvasWidth / 2, mCanvasHeight / 2 + 15, mInsideTextPaint);
            }
            else
            {
                canvas.drawCircle(mCanvasWidth / 2, mCanvasHeight / 2, mRadius, mOutsidePaint);
            }

            canvas.drawText(res.getString(R.string.label_remaining_time, mLevelDuration - mTimeLevel), mCanvasWidth / 2, mCanvasHeight - 50, mTimeTextPaint);
            if (mBalls != null)
            {
                mBalls.draw(canvas);
            }
        }

        private void updatePhysics()
        {
            //           Log.d( Constants.LOG_TAG , "updatePhysics");
            final long now = System.currentTimeMillis();

            if (mBalls == null)
            {
                return;
            }

            mBalls.update(now);
            mInside = mBalls.isInside(mRadius);
            mTimeLevel = (int) ((now - mStartLevel) / 1000.0);
            if (mTimeLevel >= mLevelDuration)
            {
                levelFailed();
            }

            if (mInside)
            {
                if (mStartInside == -1)
                {
                    mStartInside = System.currentTimeMillis();
                }
                mTimeInside = (int) ((now - mStartInside) / 1000.0);
            }
            else
            {
                mStartInside = -1;
            }
            if (mTimeInside >= SLOWIT_DURATION)
            {
                levelDone();
            }

        }

        void levelDone()
        {
            mScore = mLevelDuration - mTimeLevel;
            setState(STATE_WIN);

        }

        void levelFailed()
        {
            mScore = 0;
            setState(STATE_LEVEL_FAILED);
        }
    }
}
