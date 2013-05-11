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

import android.os.Bundle;

/**
 *
 * @author Pierre Levy
 */
public class Level
{

    public static final String BUNDLE = "levelBundle";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_STATUS = "status";
    public static final String KEY_SCORE = "score";
    public static final String KEY_GRID = "grid";
    public static final int LOCKED = -1;
    public static final int UNLOCKED = 0;
    public static final int DONE_1STAR = 1;
    public static final int DONE_2STAR = 2;
    public static final int DONE_3STAR = 3;
    private int mLevel;
    private int mStatus;
    private int mScore;
    private int mGrid;

    public Level( int grid , int level, int status , int score )
    {
        mLevel = level;
        mStatus = status;
        mGrid = grid;
        mScore = score;
    }

    public int getStatus()
    {
        return mStatus;
    }

    public void setStatus(int status)
    {
        mStatus = status;
    }

    public int getLevel()
    {
        return mLevel;
    }

    /**
     * @param mLevel the mLevel to set
     */
    public void setLevel(int level)
    {
        this.mLevel = level;
    }

    /**
     * @return the score
     */
    public int getScore()
    {
        return mScore;
    }

    /**
     * @param score the score to set
     */
    public void setScore(int score)
    {
        mScore = score;
    }

    /**
     * @return the Grid
     */
    public int getGrid()
    {
        return mGrid;
    }

    /**
     * @param Grid the Grid to set
     */
    public void setGrid(int Grid)
    {
        mGrid = Grid;
    }

    public Bundle getBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_LEVEL, mLevel);
        bundle.putInt(KEY_STATUS, mStatus);
        bundle.putInt(KEY_SCORE, mScore);
        bundle.putInt(KEY_GRID, mGrid);
        return bundle;
    }

    void updateStatus(int status)
    {
        if (status > mStatus)
        {
            mStatus = status;
        }

    }

    void updateScore(int score)
    {
        if (score > mScore)
        {
            mScore = score;
        }

    }

    void unlock()
    {
        if (mStatus == LOCKED)
        {
            mStatus = UNLOCKED;
        }
    }
}