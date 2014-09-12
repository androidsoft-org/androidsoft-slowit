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

import android.graphics.Bitmap;

/**
 *
 * @author pierre
 */
public class Graphics
{

    private Bitmap bitmapLock;
    private Bitmap bitmap1star;
    private Bitmap bitmap2stars;
    private Bitmap bitmap3stars;
    private int[] darkColors;
    private int[] mediumColors;
    private int[] lightColors;
    private int[] buttonShapeResId;
    private int viewWidth;
    private String[] gridTitles;

    /**
     * @return the bitmapLock
     */
    public Bitmap getBitmapLock()
    {
        return bitmapLock;
    }

    /**
     * @param bitmapLock the bitmapLock to set
     */
    public void setBitmapLock(Bitmap bitmapLock)
    {
        this.bitmapLock = bitmapLock;
    }

    /**
     * @return the bitmap1star
     */
    public Bitmap getBitmap1star()
    {
        return bitmap1star;
    }

    /**
     * @param bitmap1star the bitmap1star to set
     */
    public void setBitmap1star(Bitmap bitmap1star)
    {
        this.bitmap1star = bitmap1star;
    }

    /**
     * @return the bitmap2stars
     */
    public Bitmap getBitmap2stars()
    {
        return bitmap2stars;
    }

    /**
     * @param bitmap2stars the bitmap2stars to set
     */
    public void setBitmap2stars(Bitmap bitmap2stars)
    {
        this.bitmap2stars = bitmap2stars;
    }

    /**
     * @return the bitmap3stars
     */
    public Bitmap getBitmap3stars()
    {
        return bitmap3stars;
    }

    /**
     * @param bitmap3stars the bitmap3stars to set
     */
    public void setBitmap3stars(Bitmap bitmap3stars)
    {
        this.bitmap3stars = bitmap3stars;
    }

    /**
     * @return the lightColors
     */
    public int getColors(int index)
    {
        return mediumColors[ index];
    }

    /**
     * @param colors the lightColors to set
     */
    public void setColors(int[] colors)
    {
        this.mediumColors = colors;
    }

    /**
     * @return the darkColors
     */
    public int getDarkColors(int index)
    {
        return darkColors[ index];
    }

    /**
     * @param darkColors the darkColors to set
     */
    public void setDarkColors(int[] darkColors)
    {
        this.darkColors = darkColors;
    }

    /**
     * @return the lightColors
     */
    public int getLightColors(int index)
    {
        return lightColors[ index];
    }

    /**
     * @param lightColors the lightColors to set
     */
    public void setLightColors(int[] lightColors)
    {
        this.lightColors = lightColors;
    }

    /**
     * @return the buttonShapeResId
     */
    public int getButtonShapeResId(int index)
    {
        return buttonShapeResId[ index];
    }

    /**
     * @param buttonShapeResId the buttonShapeResId to set
     */
    public void setButtonShapeResId(int[] buttonShapeResId)
    {
        this.buttonShapeResId = buttonShapeResId;
    }

    void setViewWidth(int width)
    {
        viewWidth = width;
    }

    /**
     * @return the viewWidth
     */
    public int getViewWidth()
    {
        return viewWidth;
    }
    
    public int getButtonWidth()
    {
        return (viewWidth - 20) / 4;
    }

    
    
    public void setGridTitles( String[] titles )
    {
        gridTitles = titles;
    }
    
    public CharSequence getGridTitle(int index )
    {
        return gridTitles[ index ];
    }
}
