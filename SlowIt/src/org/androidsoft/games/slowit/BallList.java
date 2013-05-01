/* Copyright (c) 2010-2011 Pierre LEVY androidsoft.org
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

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import java.util.ArrayList;

/**
 *
 * @author Pierre Levy
 */
public class BallList extends ArrayList<Ball>
{

    private static final String KEY_BALL_COUNT = "mBallCount";
    private static final String KEY_BALL_X = "mX";
    private static final String KEY_BALL_Y = "mY";
    private static final String KEY_BALL_DX = "mDX";
    private static final String KEY_BALL_DY = "mDY";
    private static Drawable mBallImage;
    private static int mBallWidth;
    private static int mBallHeight;
    private static int mCanvasWidth;
    private static int mCanvasHeight;
    private double mLastTime;
    private int mBallCount;

    BallList(int width, int height, int nBallCount, int nInitialVelocity)
    {
        mCanvasWidth = width;
        mCanvasHeight = height;
        mLastTime = System.currentTimeMillis() + 100;
        mBallCount = nBallCount;

        for (int i = 0; i < nBallCount; i++)
        {
            double x = mCanvasWidth / 2;
            double y = mCanvasHeight - (i + 1) * mBallHeight;
            double dx = (20 + (4 * nInitialVelocity));
            double dy = 20 + (4 * nInitialVelocity);
            dx = ((i % 2) == 0) ? -dx : dx;
            add(new Ball(x, y, dx, dy, i));
        }

    }

    static void setSurfaceSize(int width, int height)
    {
        mCanvasWidth = width;
        mCanvasHeight = height;
    }

    static void setBallImage(Drawable image)
    {
        mBallImage = image;
        mBallWidth = mBallImage.getIntrinsicWidth();
        mBallHeight = mBallImage.getIntrinsicHeight();
    }

    void draw(Canvas canvas)
    {
        for (Ball ball : this)
        {
            final int yTop = mCanvasHeight - ((int) ball.mY + mBallHeight / 2);
            final int xLeft = (int) ball.mX - mBallWidth / 2;

            mBallImage.setBounds(xLeft, yTop, xLeft + mBallWidth, yTop + mBallHeight);
            mBallImage.draw(canvas);

        }
    }

    void update(final long now)
    {

        // Do nothing if mLastTime is in the future.
        // This allows the game-start to delay the start of the physics
        // by 100ms or whatever.
        if (mLastTime > now)
        {
            return;
        }


        final double elapsed = (now - mLastTime) / 1000.0;


        for (Ball b : this)
        {
            b.mX += elapsed * b.mDX;
            b.mY += elapsed * b.mDY;

            mLastTime = now;


            final double xLowerBound = mBallWidth / 2;
            if (b.mX < xLowerBound)
            {
                b.mX = xLowerBound;
                b.mDX = -b.mDX;
            }

            final double xHigherBound = mCanvasWidth - mBallWidth / 2;
            if (b.mX > xHigherBound)
            {
                b.mX = xHigherBound;
                b.mDX = -b.mDX;
            }

            final double yLowerBound = mBallHeight / 2;
            if ((b.mY < yLowerBound))
            {
                b.mY = yLowerBound;
                b.mDY = -b.mDY;
            }

            final double yHigherBound = mCanvasHeight - mBallHeight / 2;
            if (b.mY > yHigherBound)
            {
                b.mY = yHigherBound;
                b.mDY = -b.mDY;
            }
        }
        if (size() > 1)
        {
            manageCollision();
        }
    }

    void manageCollision()
    {
        for (int i = 0; i < size() - 1; i++)
        {
            Ball a = get(i);
            for (int j = i + 1; j < size(); j++)
            {
                Ball b = get(j);
                double dist = a.getDistance(b.mX, b.mY);
                if (dist < mBallHeight)
                {
                    doCollision(a, b, dist);
                }
            }

        }
    }

    void doCollision(Ball a, Ball b, double dist)
    {
        double r = (mBallWidth - dist) / 2;
        if (((a.mX - b.mX) * (a.mX - b.mX)) < ((a.mY - b.mY) * (a.mY - b.mY)))
        {
            a.mDY = -a.mDY;
            b.mDY = -b.mDY;
            if( a.mY < b.mY )
            {
                a.mY = a.mY - r;
                b.mY = b.mY + r;
            }
            else
            {
                a.mY = a.mY + r;
                b.mY = b.mY - r;
            }
        } else
        {
            a.mDX = -a.mDX;
            b.mDX = -b.mDX;
            if( a.mX < b.mX )
            {
                a.mX = a.mX - r;
                b.mX = b.mX + r;
            }
            else
            {
                a.mX = a.mX + r;
                b.mX = b.mX - r;
            }
        }
    }


    void restoreState( Bundle savedState)
    {
        clear();
        mBallCount = savedState.getInt(KEY_BALL_COUNT);
        for (int i = 0; i < mBallCount; i++)
        {
            double x = savedState.getDouble(KEY_BALL_X);
            double y = savedState.getDouble(KEY_BALL_Y);
            double dx = savedState.getDouble(KEY_BALL_DX);
            double dy = savedState.getDouble(KEY_BALL_DY);
            Ball ball = new Ball(x, y, dx, dy, i);
            add(ball);
        }

    }

    void saveState(Bundle map)
    {
        map.putInt(KEY_BALL_COUNT, mBallCount);
        for (int i = 0; i < mBallCount; i++)
        {
            Ball ball = get(i);
            map.putDouble(KEY_BALL_X + i, ball.mX);
            map.putDouble(KEY_BALL_Y + i, ball.mY);
            map.putDouble(KEY_BALL_DX + i, ball.mDX);
            map.putDouble(KEY_BALL_DY + i, ball.mDY);
        }
    }

    void onMotion(float x, float y, float dx, float dy)
    {
        System.out.printf("x=%f y=%f", x, y);
        Ball ball = getNearestBall(x, y);
        System.out.printf("mX=%f mY=%f", ball.mX, ball.mY);
        ball.setVelocity(dx, dy);
    }

    Ball getNearestBall(float x, float y)
    {
        Ball ballNearest = get(0);
        double distNearest = ballNearest.getDistance(x, y);

        for (int i = 1; i < size(); i++)
        {
            Ball ball = get(i);
            double dist = ball.getDistance(x, y);
            if (dist < distNearest)
            {
                ballNearest = ball;
                distNearest = dist;
            }
        }
        return ballNearest;

    }

    boolean isInside(
            int mRadius)
    {
        for (Ball ball : this)
        {
            if (ball.getDistance(mCanvasWidth / 2, mCanvasHeight / 2) > mRadius)
            {
                return false;
            }
        }
        return true;
    }

    void setLastTime(long lasttime)
    {
        mLastTime = lasttime;
    }
}

