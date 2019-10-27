package org.anchoranalysis.gui.videostats.internalframe.annotator.tool;

/*-
 * #%L
 * anchor-gui-annotation
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IQuerySelectedPoints;

import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.mark.Mark;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMap;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;

class FindPoints {

	public static Cfg findMarksContainingPnt( Cfg cfg, Point3d pnt, RegionMap regionMap, int regionID ) {
		
		Cfg cfgOut = new Cfg();
		
		RegionMembershipWithFlags rm = regionMap.membershipWithFlagsForIndex(regionID);
		
		// Find marks that contain the point x, y
		for( Mark m : cfg ) {
			byte membership = m.evalPntInside(pnt);
			if (rm.isMemberFlag(membership)) {
				cfgOut.add(m);
			}
		
		}
		return cfgOut;
	}
	
	public static List<Point3i> findSelectedPointsNear( Point3d pnt, IQuerySelectedPoints selectedPoints ) {
		Point3i pntInt = new Point3i((int)pnt.getX(), (int)pnt.getY(), (int)pnt.getZ());
		return findSelectedPointsNear(pntInt, selectedPoints);
	}

	private static List<Point3i> findSelectedPointsNear( Point3i pnt, IQuerySelectedPoints selectedPoints ) {
		
		List<Point3i> listPoints = selectedPoints.selectedPointsAsIntegers();
		
		List<Point3i> nearPoints = new ArrayList<>();
		
		// 5 pixels
		int distThrshld = 5;
		int distThrshldSq = distThrshld*distThrshld;
		
		// Find marks that contain the point x, y
		for( Point3i p : listPoints ) {
			int distSq = distFromPoints(p,pnt);
			if( distSq<distThrshldSq) {
				nearPoints.add(p);
			}
		}
		return nearPoints;
	}
	
	private static int distFromPoints( Point3i p1, Point3i p2 ) {
		int px = p1.getX() - p2.getX();
		int py = p1.getY() - p2.getY();
		int pz = p1.getZ() - p2.getZ();
		
		return (px*px) + (py*py) + (pz*pz);
	}
}
