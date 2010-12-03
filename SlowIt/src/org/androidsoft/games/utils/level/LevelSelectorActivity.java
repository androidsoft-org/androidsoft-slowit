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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pierre
 */
public abstract class LevelSelectorActivity extends Activity implements LevelClickListener.OnLevelClickedListener
{

    private static final String KEY_GRID_COUNT = "GRID_COUNT";
    private static final String KEY_GRID_SIZE = "GRID_SIZE";
    private static final String KEY_ID = "LEVEL_ID";
    private static final String KEY_STATUS = "LEVEL_STATUS";
    private static final String KEY_SCORE = "LEVEL_SCORE";
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
        Log.d(LevelSelectorActivity.class.getName(), "onCreate");
        super.onCreate(icicle);

        setContentView(getLayout());

    }

    private void initGrids()
    {
        Log.d(LevelSelectorActivity.class.getName(), "initGrids");
        int[] grids = getGrids();
        for (int i = 0; i < grids.length; i++)
        {
            List<Level> listGridLevels;
            if (mLevels.size() < (i + 1))
            {
                listGridLevels = getInitLevelList(i);
                mLevels.add(listGridLevels);
            }
        }

    }

    private void updateGrids()
    {
        Log.d(LevelSelectorActivity.class.getName(), "updateGrids");
        int[] grids = getGrids();
        for (int i = 0; i < grids.length; i++)
        {
            List<Level> listGridLevels = mLevels.get(i);
            GridView g = (GridView) findViewById(grids[i]);
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
            list.add(new Level(grid, i + 1, status, 0));

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
        Log.d(LevelSelectorActivity.class.getName(), "onActivityResult");
        super.onActivityResult(requestCode, resultCode, intent);

        if (intent != null)
        {
            Bundle extras = intent.getExtras();
            if (extras != null)
            {
                int level = extras.getInt(Level.KEY_LEVEL);
                int score = extras.getInt(Level.KEY_SCORE);
                int status = extras.getInt(Level.KEY_STATUS);
                int grid = extras.getInt(Level.KEY_GRID);

                List<Level> listGridLevels = mLevels.get(grid);
                Level l = listGridLevels.get(level - 1);
                if (score > 0)
                {
                    l.updateStatus(status);
                    l.updateScore(score);
                    updateLevel(grid, level, l.getStatus(), l.getScore());
                    unlockNextLevel(level, grid, listGridLevels);
                    update();
                }
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
            if (grid < getGrids().length)
            {
                List<Level> listNextGrid = mLevels.get(grid);
                lNext = listNextGrid.get(0);
            }

        }
        if (lNext != null)
        {
            lNext.unlock();
            updateLevel(grid, lNext.getLevel(), lNext.getStatus(), lNext.getScore());
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
        int[] grids = getGrids();
        for (int i = 0; i < grids.length; i++)
        {
            List<Level> listGridLevels = mLevels.get(i);
            GridView g = (GridView) findViewById(grids[i]);
            g.setAdapter(new LevelAdapter(this, listGridLevels, getResButtons()));
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onResume()
    {
        Log.d(LevelSelectorActivity.class.getName(), "onResume");
        super.onResume();

        SharedPreferences prefs = getPreferences(0);
        int gridcount = prefs.getInt(KEY_GRID_COUNT, 0);
        if (gridcount == 0)
        {
            initGrids();
        } else
        {
            mLevels.clear();
            for (int i = 0; i < gridcount; i++)
            {
                List<Level> list = new ArrayList<Level>();
                int gridsize = prefs.getInt(KEY_GRID_SIZE, 1);
                for (int j = 0; j < gridsize; j++)
                {
                    int id = prefs.getInt(getPrefKey(KEY_ID, i, j + 1), 0);
                    int status = prefs.getInt(getPrefKey(KEY_STATUS, i, j + 1), 0);
                    int score = prefs.getInt(getPrefKey(KEY_SCORE, i, j + 1), 0);
                    Level level = new Level(i, id, status, score);
                    list.add(level);
                }
                mLevels.add(list);
            }
        }
        updateGrids();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onPause()
    {
        Log.d(LevelSelectorActivity.class.getName(), "onPause");
        super.onPause();

        SharedPreferences.Editor editor = getPreferences(0).edit();
        int gridcount = getGrids().length;


        editor.putInt(KEY_GRID_COUNT, gridcount);
        for (int i = 0; i < gridcount; i++)
        {
            List<Level> list = mLevels.get(i);
            int gridsize = list.size();
            editor.putInt(KEY_GRID_SIZE, gridsize);
            for (Level level : list)
            {
                editor.putInt(getPrefKey(KEY_ID, i, level.getLevel()), level.getLevel());
                editor.putInt(getPrefKey(KEY_STATUS, i, level.getLevel()), level.getStatus());
                editor.putInt(getPrefKey(KEY_SCORE, i, level.getLevel()), level.getScore());
            }

        }
        editor.commit();
    }

    private String getPrefKey(String key, int grid, int level)
    {
        return "LEVEL-" + key + grid + ":" + level;
    }

    private void updateLevel(int grid, int level, int status, int score)
    {
        Log.d(LevelSelectorActivity.class.getName(), "updateLevel " + grid + " " + level + " " + status + " " + score);
        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.putInt(getPrefKey(KEY_STATUS, grid, level), status);
        editor.putInt(getPrefKey(KEY_SCORE, grid, level), score);
        editor.commit();
    }
}
