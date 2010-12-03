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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 *
 * @author pierre
 */
public class LevelManager
{

    private Activity mActivity;
    private int mLevel;
    private int mStatus;
    private int mHiScore;
    private int mGrid;

    public LevelManager(Activity activity, Intent intent)
    {
        mActivity = activity;
        Bundle bundle = intent.getBundleExtra(Level.BUNDLE);
        mLevel = bundle.getInt(Level.KEY_LEVEL);
        mStatus = bundle.getInt(Level.KEY_STATUS);
        mHiScore = bundle.getInt(Level.KEY_SCORE);
        mGrid = bundle.getInt(Level.KEY_GRID);
    }

    public void end(int score, int status)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(Level.KEY_LEVEL, mLevel);
        bundle.putInt(Level.KEY_SCORE, score);
        bundle.putInt(Level.KEY_STATUS, status);
        bundle.putInt(Level.KEY_GRID, mGrid);
        Intent mIntent = new Intent();
        mIntent.putExtras(bundle);
        mActivity.setResult(Activity.RESULT_OK, mIntent);
        mActivity.finish();
    }

    public int getLevel()
    {
        return mLevel;
    }

    public int getHiScore()
    {
        return mHiScore;
    }

    public int getStatus()
    {
        return mStatus;
    }

    public int getGrid()
    {
        return mGrid;
    }
}
