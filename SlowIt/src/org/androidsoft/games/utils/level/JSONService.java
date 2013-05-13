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

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Pierre LEVY
 */
public class JSONService
{
    private static final String KEY_GRIDS = "grids";
    private static final String KEY_GRID = "grid";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_STATUS = "status";
    private static final String KEY_SCORE = "score";
    
    
    public static String getJSON( List<List<Level>> list ) throws JSONException
    {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        for (List<Level> l : list)
        {
            JSONArray a = new JSONArray();
            for (Level level : l)
            {
                JSONObject object = new JSONObject();
                object.put(KEY_GRID, level.getGrid());
                object.put(KEY_LEVEL, level.getLevel());
                object.put(KEY_STATUS, level.getStatus());
                object.put(KEY_SCORE, level.getScore());
                a.put(object);
            }
            array.put(a);
        }
        json.put( KEY_GRIDS, array);
        return json.toString();
    }
    
    
    public static void load( List<List<Level>> list , String json ) throws JSONException
    {
        JSONObject js = new JSONObject( json );
        JSONArray array = js.getJSONArray( KEY_GRIDS);
        for( int i = 0 ; i < array.length() ;i++ )
        {
            List<Level> l = list.get(i);
            JSONArray a = array.getJSONArray(i);
            for( int j = 0 ; j < a.length() ; j++ )
            {
                JSONObject object = a.getJSONObject(j);
                Level level = l.get(j);
                level.setGrid( object.getInt( KEY_GRID) );
                level.setLevel( object.getInt( KEY_LEVEL) );
                level.setStatus( object.getInt(KEY_STATUS) );
                level.setScore( object.getInt(KEY_SCORE) );
                
            }
        }
        
    }
}