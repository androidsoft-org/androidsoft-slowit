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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import org.androidsoft.games.utils.level.LevelManager;

/**
 *
 * @author pierre
 */
public class GameActivity extends Activity
{
    private View mGameView;
    private LevelManager mLevelManager;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mLevelManager = new LevelManager(this, getIntent());

        mGameView = new GameView(this, null, this, savedInstanceState  , mLevelManager );
        setContentView(mGameView);

        if (savedInstanceState != null)
        {
            for (String key : savedInstanceState.keySet())
            {
                Log.d("MainActivity:savedInstanceState", key + "=" + savedInstanceState.getString(key));
            }
        }

    }

}
