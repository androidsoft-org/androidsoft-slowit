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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.List;

/**
 *
 * @author Pierre Levy
 */
public class LevelClickListener implements OnItemClickListener
{
    private Context mContext;
    private List<Level> mLevels;
    private OnLevelClickedListener mListener;


    public interface OnLevelClickedListener
    {
        void onLevelClicked( Level level );
    }

    public LevelClickListener( Context context , List<Level> levels , OnLevelClickedListener listener )
    {
        mContext = context;
        mLevels = levels;
        mListener = listener;

    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id)
    {
        System.out.println("Click - postion : " + position );
        Level level = mLevels.get(position);
        mListener.onLevelClicked(level);
    }
}
