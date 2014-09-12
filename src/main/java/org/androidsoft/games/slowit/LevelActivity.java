/* Copyright (c) 2010-2014 Pierre LEVY androidsoft.org
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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.androidsoft.games.utils.level.LevelSelectorActivity;

/**
 *
 * @author Pierre Levy
 */
public class LevelActivity extends LevelSelectorActivity
{

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        
    }

 
    @Override
    public Class<?> getGameActivity()
    {
        return GameActivity.class;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.credits_menu:
                credits();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void credits()
    {
        Intent intent = new Intent( this , CreditsActivity.class );
        startActivity(intent);
    }
}
