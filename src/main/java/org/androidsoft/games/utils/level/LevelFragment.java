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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.viewpagerindicator.CirclePageIndicator;
import java.util.List;
import org.androidsoft.games.slowit.R;

/**
 *
 * @author pierre
 */
public class LevelFragment extends Fragment
{

    int mNum;
    static Context mContext;
    static List<List<Level>> mLevels;
    static Graphics mGraphics;
    static OnLevelClickedListener mListener;
    static ViewPager mPager;
    GridView mGridView;
    CirclePageIndicator mCircleIndicator;
    
    private void setContext( Context context, List<List<Level>> levels, Graphics graphics, OnLevelClickedListener listener, ViewPager pager )
    {
        mContext = context;
        mLevels = levels;
        mGraphics = graphics;
        mListener = listener;
        mPager = pager;
    }

    /**
     * Create a new instance of CountingFragment, providing "num" as an
     * argument.
     */
    static Fragment newInstance( int num , Context context, List<List<Level>> levels, Graphics graphics, OnLevelClickedListener listener, ViewPager pager )
    {
        LevelFragment f = new LevelFragment();
        f.setContext( context, levels, graphics, listener, pager );

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    /**
     * The Fragment's UI is just a simple text view showing its instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.grid, container, false);
        v.setBackgroundColor(mGraphics.getLightColors( mNum ));
        View tv = v.findViewById(R.id.text);
        tv.setBackgroundColor( mGraphics.getDarkColors( mNum ));
        ((TextView) tv).setText(mGraphics.getGridTitle( mNum ));
        mGridView = (GridView) v.findViewById(R.id.level_grid1);
        mGridView.setAdapter((ListAdapter) new LevelAdapter(mContext, mLevels.get(mNum), mGraphics));
        mGridView.setOnItemClickListener(new LevelClickListener( mContext, mLevels.get(mNum), mListener ));
        
                //Bind the title indicator to the adapter
        mCircleIndicator = (CirclePageIndicator) v.findViewById(R.id.circles);
        mCircleIndicator.setViewPager( mPager );

        final float density = getResources().getDisplayMetrics().density;
        mCircleIndicator.setBackgroundColor(mGraphics.getDarkColors( mNum ));
        mCircleIndicator.setRadius(8 * density);
        mCircleIndicator.setPageColor(mGraphics.getLightColors( mNum ));
        mCircleIndicator.setFillColor( 0xFFFFFFFF );
        mCircleIndicator.setStrokeColor(0xFFFFFFFF);
        mCircleIndicator.setStrokeWidth(1 * density);

        return v;
    }

    void update()
    {
        if( mGridView != null )
        {
            mGridView.setAdapter((ListAdapter) new LevelAdapter(mContext, mLevels.get(mNum), mGraphics));
            mGridView.invalidate();
        }
        if( mCircleIndicator != null )
        {
            mCircleIndicator.invalidate();
        }
    }



}
