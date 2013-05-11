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

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.androidsoft.games.slowit.R;

public abstract class LevelSelectorActivity extends Activity implements OnLevelClickedListener
{

    private static final String KEY_GRID_COUNT = "GRID_COUNT";
    private static final String KEY_GRID_SIZE = "GRID_SIZE";
    private static final String KEY_ID = "LEVEL_ID";
    private static final String KEY_STATUS = "LEVEL_STATUS";
    private static final String KEY_SCORE = "LEVEL_SCORE";
    static final int NUM_GRIDS = 5;
    static final int NUM_COLORS = 5;
    static final int NUM_LEVEL_PER_GRID = 12;
    static int[] sColorsId =
    {
        R.color.blue,
        R.color.violet,
        R.color.green,
        R.color.orange,
        R.color.red
    };
    static int[] sDarkColorsId =
    {
        R.color.dark_blue,
        R.color.dark_violet,
        R.color.dark_green,
        R.color.dark_orange,
        R.color.dark_red
    };
    static int[] sColors = new int[NUM_COLORS];
    static int[] sDarkColors = new int[NUM_COLORS];
    static int[] sButtonShape =
    {
        R.drawable.button_blue,
        R.drawable.button_violet,
        R.drawable.button_green,
        R.drawable.button_orange,
        R.drawable.button_red,
    };
    MyAdapter mAdapter;
    ViewPager mPager;
    static List<List<Level>> mLevels;
    static Context mContext;
    static Bitmap mBitmapLock;
    static Graphics mGraphics;
    static OnLevelClickedListener mListener;

    public abstract Class<?> getGameActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.grid_pager);

        Resources res = getResources();
        initColors(res, sColorsId, sColors);
        initColors(res, sDarkColorsId, sDarkColors);
        initGraphics(res);
        initGrids();

        mAdapter = new MyAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mContext = getApplicationContext();
        mListener = this;



    }

    private void initColors(Resources res, int[] ids, int[] colors)
    {
        for (int i = 0; i < ids.length; i++)
        {
            colors[i] = res.getColor(ids[i]);
        }
    }

    private static List<Level> getInitLevelList(int grid)
    {
        List<Level> list = new ArrayList<Level>();
        for (int i = 0; i < getLevelPerGrid(); i++)
        {
            int status = ((i == 0) && (grid == 0)) ? 0 : -1;
            list.add(new Level(grid, i + 1, status, 0));

        }
        return list;

    }

    private static int getLevelPerGrid()
    {
        return NUM_LEVEL_PER_GRID;
    }

    private void initGrids()
    {
        mLevels = new ArrayList<List<Level>>();

        for (int i = 0; i < NUM_GRIDS; i++)
        {
            List<Level> list = getInitLevelList(i);
            mLevels.add(list);
        }
    }

    private void initGraphics(Resources res)
    {
        mGraphics = new Graphics();
        mGraphics.setBitmapLock(BitmapFactory.decodeResource(res, R.drawable.lock));
        mGraphics.setBitmap1star(BitmapFactory.decodeResource(res, R.drawable.star1));
        mGraphics.setBitmap2stars(BitmapFactory.decodeResource(res, R.drawable.star2));
        mGraphics.setBitmap3stars(BitmapFactory.decodeResource(res, R.drawable.star3));
        mGraphics.setButtonShapeResId(sButtonShape);
        mGraphics.setLightColors(sColors);
        mGraphics.setDarkColors(sDarkColors);
        mGraphics.setViewWidth(600);
    }

    /////////////////////////////////////////////////////////////////////////////
    // Game play
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
                    update(grid);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    void unlockNextLevel(int level, int grid, List<Level> list)
    {
        Level lNext = null;


        if (level < getLevelPerGrid())
        {
            lNext = list.get(level);
        }
        else
        {
            grid++;
            if (grid < NUM_GRIDS)
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
        if (gridcount == NUM_GRIDS)
        {
            for (int i = 0; i < gridcount; i++)
            {
                List<Level> list = mLevels.get(i);
                int gridsize = prefs.getInt(KEY_GRID_SIZE, 1);
                for (int j = 0; j < gridsize; j++)
                {
                    Level level = list.get(j);
                    level.setGrid(i);
                    level.setLevel(prefs.getInt(getPrefKey(KEY_ID, i, j + 1), 0));
                    level.setStatus(prefs.getInt(getPrefKey(KEY_STATUS, i, j + 1), 0));
                    level.setScore(prefs.getInt(getPrefKey(KEY_SCORE, i, j + 1), 0));
                }
            }
        }
//        updateGrids();

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
        int gridcount = NUM_GRIDS;


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

    private void update(int grid)
    {
        mAdapter.notifyDataSetChanged();
    }

    public class MyAdapter extends FragmentPagerAdapter
    {

        HashMap< Integer, LevelFragment> mMapFragment = new HashMap< Integer, LevelFragment>();

        public MyAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public int getCount()
        {
            return NUM_GRIDS;
        }

        @Override
        public LevelFragment getItem(int position)
        {
            LevelFragment f = mMapFragment.get(position);
            if (f == null)
            {
                f = LevelFragment.newInstance(position, mContext, mLevels, mGraphics, mListener);
                mMapFragment.put(position, f);
            }
            return f;
        }

        @Override
        public int getItemPosition(Object object)
        {
            return POSITION_NONE;
        }
        
    }
}
