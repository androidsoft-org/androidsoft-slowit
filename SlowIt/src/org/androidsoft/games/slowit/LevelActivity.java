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
package org.androidsoft.games.slowit;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import org.androidsoft.games.utils.level.LevelSelectorActivity;

/**
 *
 * @author pierre
 */
public class LevelActivity extends LevelSelectorActivity
{
    private static final int LEVEL_COUNT = 9;
    private int[] buttons =
    {
        R.drawable.button_locked,
        R.drawable.button_new,
        R.drawable.button_1star,
        R.drawable.button_2stars,
        R.drawable.button_3stars
    };

    private int[] grids = { R.id.level_grid1 , R.id.level_grid2 };

    @Override
    public void onCreate(Bundle icicle)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate( icicle );

/*
        HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.level_scrollview);
        hsv.setHorizontalScrollBarEnabled(false);
*/   }


    public int[] getResButtons()
    {
        return buttons;
    }

    @Override
    public int[] getGrids()
    {
        return grids;
    }

    @Override
    public int getLayout()
    {
        return R.layout.level_selector;
    }

    @Override
    public int getLevelPerGrid()
    {
        return LEVEL_COUNT;
    }

    @Override
    public Class<?> getGameActivity()
    {
        return GameActivity.class;
    }
}
