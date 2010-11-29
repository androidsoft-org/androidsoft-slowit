/* Copyright (c) 2010 Pierre LEVY androidsoft.org
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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

/**
 *
 * @author pierre
 */
public class LevelView extends ImageView
{
    Level mLevel;
    int mStatus;
    public LevelView( Context context , Level level )
    {
        super(context);
        mLevel = level;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if( mLevel.getStatus() != Level.LOCKED)
            {
        canvas.save();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setARGB( 100, 255, 255, 255);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(24);
        canvas.drawText( "" + mLevel.getLevel(), 70 / 2, 70 / 2, paint);
        canvas.restore();
        }
    }

}
