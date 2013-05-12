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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import java.util.List;

/**
 *
 * @author Pierre Levy
 */
public class LevelAdapter extends BaseAdapter
{

    private Context mContext;
    private List<Level> mLevels;
    Graphics mGraphics;


    public LevelAdapter(Context c, List<Level> levels , Graphics graphics )
    {
        mContext = c;
        mLevels = levels;
        mGraphics = graphics;
    }

    public int getCount()
    {
        return mLevels.size();
    }

    public Object getItem(int position)
    {
        return position;
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        LevelView imageView;
        Level level = mLevels.get(position);
        int width = parent.getWidth();
//        Log.d( "androidsoft.org" , "View width" +  parent.getWidth());
//        mGraphics.setViewWidth(width);
        if (convertView == null)
        {
            imageView = new LevelView(mContext, level , mGraphics );
            imageView.setLayoutParams(new GridView.LayoutParams( mGraphics.getButtonWidth(), mGraphics.getButtonWidth() ));
            imageView.setAdjustViewBounds(false);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);
        } else
        {
            imageView = (LevelView) convertView;
        }

        imageView.setImageResource( mGraphics.getButtonShapeResId( level.getGrid()) );
        return imageView;
    }
}
