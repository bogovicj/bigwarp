/*-
 * #%L
 * BigWarp plugin for Fiji.
 * %%
 * Copyright (C) 2015 - 2021 Howard Hughes Medical Institute.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
package bigwarp.source;

import bdv.viewer.Interpolation;
import bdv.viewer.Source;
import bdv.viewer.SourceAndConverter;
import bigwarp.BigWarp.BigWarpData;
import mpicbg.spim.data.sequence.VoxelDimensions;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

public class GridSource< T extends RealType< T >> implements Source< T >
{
	
	public enum GRID_TYPE { MOD, LINE };
	
	protected final String name;
	
	protected final BigWarpData<?> sourceData;
	
	protected final Interval interval;

	protected final GridRealRandomAccessibleRealInterval<T> gridImg;
	
	protected T type;
	
	public GridSource( String name, BigWarpData< ? > data, T t, RealTransform warp  )
	{
		this.name = name;
		this.type = t.copy();
		sourceData = data;
		
		interval = sourceData.sources.get( sourceData.targetSourceIndices[ 0 ] ).getSpimSource().getSource( 0, 0 );
		gridImg = new GridRealRandomAccessibleRealInterval<T>( interval, t, warp );
	}
	
	public void setGridSpacing( double spacing )
	{
		gridImg.ra.setGridSpacing( spacing );
	}
	
	public void setGridWidth( double width )
	{
		gridImg.ra.setGridWidth( width );
	}

	public void setWarp( RealTransform warp )
	{
		gridImg.ra.warp = warp;
	}
	
	@Override
	public boolean isPresent( int t )
	{
		return ( t == 0 );
	}
	
	public void setMethod( GRID_TYPE method )
	{
		gridImg.ra.setMethod( method );
	}

	@Override
	public RandomAccessibleInterval<T> getSource( int t, int level ) 
	{
		return Views.interval( Views.raster( 
				getInterpolatedSource( t, level, Interpolation.NEARESTNEIGHBOR ) ), 
				interval );
	}

	@Override
	public RealRandomAccessible<T> getInterpolatedSource( int t, int level, Interpolation method ) 
	{
		return gridImg;
	}

	@Override
	public void getSourceTransform( int t, int level, AffineTransform3D transform )
	{
		sourceData.sources.get( 0 ).getSpimSource().getSourceTransform( t, level, transform );
	}

	@Override
	public T getType()
	{
		return type;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public VoxelDimensions getVoxelDimensions()
	{
		return sourceData.sources.get( sourceData.targetSourceIndices[ 0 ] ).getSpimSource().getVoxelDimensions();
	}

	@Override
	public int getNumMipmapLevels() 
	{
		return 1;
	}
	
}
