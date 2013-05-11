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
package org.androidsoft.games.utils.level;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.ImageView;

/**
 *
 * @author Pierre Levy
 */
public class LevelView extends ImageView
{

    Level mLevel;
    int mStatus;
    Graphics mGraphics;

    public LevelView(Context context, Level level, Graphics graphics)
    {
        super(context);
        mLevel = level;
        mGraphics = graphics;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int width = mGraphics.getButtonWidth();
        
        if (mLevel.getStatus() != Level.LOCKED)
        {
            canvas.save();
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setARGB(255, 255, 255, 255);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize( width / 3 );
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            canvas.drawText("" + mLevel.getLevel(), width / 2, width / 2, paint);
            
            Bitmap bitmapScore = null;
            if( mLevel.getStatus() == Level.DONE_1STAR )
            {
                bitmapScore = mGraphics.getBitmap1star();
            } 
            else if( mLevel.getStatus() == Level.DONE_2STAR )
            {
                bitmapScore = mGraphics.getBitmap2stars();
            } 
            else if( mLevel.getStatus() == Level.DONE_3STAR )
            {
                bitmapScore = mGraphics.getBitmap3stars();
            } 
            if( bitmapScore != null )
            {
                canvas.drawBitmap( bitmapScore , ( width - bitmapScore.getWidth()) / 2 , width / 2 , paint);
            }
            
            canvas.restore();
        }
        else
        {
            canvas.save();
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            Bitmap bitmapLock = mGraphics.getBitmapLock();
            canvas.drawBitmap( bitmapLock , ( width - bitmapLock.getWidth()) / 2 , ( width - bitmapLock.getHeight()) / 2, paint);
            canvas.restore();
            
        }
    }
}
