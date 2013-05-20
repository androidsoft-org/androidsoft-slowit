/* Copyright (c) 2010-2013 Pierre LEVY androidsoft.org
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.androidsoft.games.slowit.Constants;
import org.androidsoft.games.slowit.R;
import org.json.JSONException;

public abstract class LevelSelectorActivity extends FragmentActivity implements OnLevelClickedListener
{

    private static final String KEY_JSON = "JSON";
    private static final String DEFAULT = "DEF_JSON";
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
    static int[] sLightColorsId =
    {
        R.color.light_blue,
        R.color.light_violet,
        R.color.light_green,
        R.color.light_orange,
        R.color.light_red
    };
    static int[] sColors = new int[NUM_COLORS];
    static int[] sLightColors = new int[NUM_COLORS];
    static int[] sDarkColors = new int[NUM_COLORS];
    static int[] sButtonShape =
    {
        R.drawable.button_blue,
        R.drawable.button_violet,
        R.drawable.button_green,
        R.drawable.button_orange,
        R.drawable.button_red,
    };
    static int[] sGridTitlesId =
    {
        R.string.grid1_title,
        R.string.grid2_title,
        R.string.grid3_title,
        R.string.grid4_title,
        R.string.grid5_title
    };
    static String[] sGridTitles = new String[NUM_GRIDS];
    MyAdapter mAdapter;
    ViewPager mPager;
    static List<List<Level>> mLevels;
    static Context mContext;
    static Bitmap mBitmapLock;
    static Graphics mGraphics;
    static OnLevelClickedListener mListener;

    public abstract Class<?> getGameActivity();

    
    ////////////////////////////////////////////////////////////////////////////
    // Life circle events
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.grid_pager);

        Resources res = getResources();
        initGraphics(res);
        initGrids();

        mAdapter = new MyAdapter(this.getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mContext = getApplicationContext();
        mListener = this;

    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onPause()
    {
        Log.d( Constants.LOG_TAG, "onPause");
        super.onPause();

        save();

    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onResume()
    {
        Log.d( Constants.LOG_TAG, "onResume");
        super.onResume();

        restore();

    }

    ////////////////////////////////////////////////////////////////////////////
    // Initializations
    
    private void initGrids()
    {
        mLevels = new ArrayList<List<Level>>();

        for (int i = 0; i < NUM_GRIDS; i++)
        {
            List<Level> list = new ArrayList<Level>();
            for (int j = 0; j < NUM_LEVEL_PER_GRID; j++)
            {
                int status = ((j == 0) && (i == 0)) ? 0 : -1;
                list.add(new Level(i, j + 1, status, 0));

            }
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

        initColors(res, sColorsId, sColors);
        initColors(res, sDarkColorsId, sDarkColors);
        initColors(res, sLightColorsId, sLightColors);
        mGraphics.setColors(sColors);
        mGraphics.setLightColors(sLightColors);
        mGraphics.setDarkColors(sDarkColors);
        mGraphics.setViewWidth(600);

        initLabels(res, sGridTitlesId, sGridTitles);
        mGraphics.setGridTitles(sGridTitles);
    }

    private void initColors(Resources res, int[] ids, int[] colors)
    {
        for (int i = 0; i < ids.length; i++)
        {
            colors[i] = res.getColor(ids[i]);
        }
    }

    private void initLabels(Resources res, int[] ids, String[] labels)
    {
        for (int i = 0; i < ids.length; i++)
        {
            labels[i] = res.getString(ids[i]);
        }
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
        Log.d( Constants.LOG_TAG, "onActivityResult");

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
                    save();
                    unlockNextLevel(level, grid, listGridLevels);
                    updateUI();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    void unlockNextLevel(int level, int grid, List<Level> list)
    {
        Level lNext = null;


        if (level < NUM_LEVEL_PER_GRID )
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
            save();
        }
    }

    public void onLevelClicked(Level level)
    {
        if (level.getStatus() != Level.LOCKED)
        {
            startLevel(level);
        }
    }

    void restore()
    {
        SharedPreferences prefs = getPreferences(0);
        String json = prefs.getString(KEY_JSON, DEFAULT);
        if (!json.equals(DEFAULT))
        {
            try
            {
                JSONService.load(mLevels, json);
            }
            catch (JSONException ex)
            {
                Log.e( Constants.LOG_TAG , "on Resume : Error loading JSON : " + ex.getMessage());
            }
        }

    }

    void save()
    {
        SharedPreferences.Editor editor = getPreferences(0).edit();
        try
        {
            editor.putString(KEY_JSON, JSONService.getJSON(mLevels));
            editor.commit();
        }
        catch (JSONException ex)
        {
            Log.e(Constants.LOG_TAG , " onPause : Error writing JSON data : " + ex.getMessage());
        }

    }

    private void updateUI()
    {
        mAdapter.notifyDataSetChanged();
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // Pager adapter

    public class MyAdapter extends FragmentPagerAdapter
    {

        HashMap< Integer, Fragment> mMapFragment = new HashMap< Integer, Fragment>();

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
        public Fragment getItem(int position)
        {
            Fragment f = mMapFragment.get(position);
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
