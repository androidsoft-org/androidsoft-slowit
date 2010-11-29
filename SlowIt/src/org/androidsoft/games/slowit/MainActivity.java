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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * @author pierre
 */
public class MainActivity extends AbstractMainActivity
{

    private ViewGroup mView;
    private View mGameView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mView = (ViewGroup) findViewById(R.id.gameview);
        mGameView = new GameView(this, null, this, savedInstanceState);

        mView.addView(mGameView);

        if (savedInstanceState != null)
        {
            for (String key : savedInstanceState.keySet())
            {
                Log.d("MainActivity:savedInstanceState", key + "=" + savedInstanceState.getString(key));
            }
        }

    }

    @Override
    protected void onResume()
    {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
    }

    protected void newGame()
    {
    }

    protected View getGameView()
    {
        return mView;
    }
}



