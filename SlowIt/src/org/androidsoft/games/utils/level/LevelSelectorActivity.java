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
import android.widget.GridView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pierre
 */
public abstract class LevelSelectorActivity extends Activity implements LevelClickListener.OnLevelClickedListener
{

    private int[] mGrids;
    private List<List> mLevels = new ArrayList<List>();

    public abstract int[] getResButtons();

    public abstract int[] getGrids();

    public abstract int getLayout();

    public abstract int getLevelPerGrid();

    public abstract Class<?> getGameActivity();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        setContentView(getLayout());

        mGrids = getGrids();
        for (int i = 0; i < mGrids.length; i++)
        {
            List<Level> listGridLevels;
            if (mLevels.size() < (i + 1))
            {
                listGridLevels = getInitLevelList(i);
                mLevels.add(listGridLevels);
            } else
            {
                listGridLevels = mLevels.get(i);
            }
            GridView g = (GridView) findViewById(mGrids[i]);
            g.setAdapter(new LevelAdapter(this, listGridLevels, getResButtons()));
            g.setOnItemClickListener(new LevelClickListener(this, listGridLevels, this));
        }


    }

    private List<Level> getInitLevelList(int grid)
    {
        List<Level> list = new ArrayList<Level>();
        for (int i = 0; i < getLevelPerGrid(); i++)
        {
            int status = ((i == 0) && (grid == 0)) ? 0 : -1;
            list.add(new Level(i + 1, status, grid));

        }
        return list;

    }

    public void startLevel(Level level)
    {
        Intent intent = new Intent(this, getGameActivity());
        intent.putExtra(Level.BUNDLE, level.getBundle());
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            int level = extras.getInt(Level.KEY_LEVEL);
            int score = extras.getInt(Level.KEY_SCORE);
            int status = extras.getInt(Level.KEY_STATUS);
            int grid = extras.getInt(Level.KEY_GRID);

            Toast.makeText(this, "Level : " + level + " - Score : " + score, Toast.LENGTH_LONG).show();
            List<Level> listGridLevels = mLevels.get(grid);
            Level l = listGridLevels.get(level - 1);
            if (score > 0)
            {
                l.updateStatus(status);
                l.updateScore(score);
                unlockNextLevel( level , grid , listGridLevels );
                update();
            }
        }
    }

    void unlockNextLevel(int level, int grid, List<Level> list)
    {
        Level lNext = null;


        if (level < getLevelPerGrid())
        {
            lNext = list.get(level);
        } else
        {
            grid++;
            if (grid < mGrids.length)
            {
                List<Level> listNextGrid = mLevels.get(grid);
                lNext = listNextGrid.get(0);
            }

        }
        if (lNext != null)
        {
            lNext.unlock();
        }
    }

    public void onLevelClicked(Level level)
    {
        if (level.getStatus() != Level.LOCKED)
        {
            startLevel(level);
        }
    }

    private void update()
    {
        for (int i = 0; i < mGrids.length; i++)
        {
            List<Level> listGridLevels = mLevels.get(i);
            GridView g = (GridView) findViewById(mGrids[i]);
            g.setAdapter(new LevelAdapter(this, listGridLevels, getResButtons()));
        }
    }
}
