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

/**
 *
 * @author pierre
 */
public class Ball {

    double mX;
    double mY;
    double mDX;
    double mDY;
    int mIndex;

    static int width;
    static int height;

    Ball( double x , double y, double dx , double dy, int index )
    {
        mX = x;
        mY = y;
        mDX = dx;
        mDY = dy;
        mIndex = index;
    }

    double getDistance(double x, double y)
    {
        return Math.sqrt( Math.pow( mX - x , 2.0 ) + Math.pow(mY - y , 2.0 ));
    }

    void setVelocity(float dx, float dy)
    {
        mDX = mDX + dx;
        mDY = mDY + dy;
    }

}
