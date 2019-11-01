package org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate;

import java.awt.Color;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import javax.swing.event.EventListenerList;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.gui.videostats.internalframe.annotator.SaveMonitor;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.ConfirmResetStateChangedListener;

import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.gui.videostats.internalframe.markredraw.ColoredCfg;

class CurrentState implements IQuerySelectedPoints {

	private SaveMonitor saveMonitor;
	
	private DualCfg cfg = new DualCfg();
	private Cfg currentProposedCfg = new Cfg();
	private Cfg currentSelectedPointsCfg = new Cfg();
	private ColoredCfg currentCfgDisplayed = null;
	private Cfg bboxListForRefresh = new Cfg();

	private static RGBColor colorAccepted = new RGBColor(Color.RED);
	private static RGBColor colorRejected = new RGBColor(Color.PINK);
	private static RGBColor colorSelectedPoints = new RGBColor(Color.YELLOW);
	private static RGBColor colorBlue = new RGBColor(Color.BLUE);
		
	private ConfirmReset confirmReset = new ConfirmReset();
		
	public CurrentState(SaveMonitor saveMonitor) {
		super();
		this.saveMonitor = saveMonitor;
	}
	
	public CurrentState copyForUndo() {
		CurrentState out = new CurrentState(saveMonitor);
		out.cfg = cfg.shallowCopy();
		out.currentProposedCfg = currentProposedCfg.shallowCopy();
		out.currentSelectedPointsCfg = currentSelectedPointsCfg.shallowCopy();
		out.currentCfgDisplayed = currentCfgDisplayed!=null ? currentCfgDisplayed.shallowCopy() : null;
		
		out.bboxListForRefresh =  bboxListForRefresh.shallowCopy();
		return out;
	}
	
	public void dispose() {
		confirmReset.dispose();
	}
	
	public void initAcceptedCfg( DualCfg in ) {
		// We don't alter the changedSinceLastSave variable
		cfg.addAll(in);
		bboxListForRefresh.addAll( in.getCfgAccepted() );
		bboxListForRefresh.addAll( in.getCfgRejected() );
	}

	public boolean hasCurrentProposedState() {
		return currentProposedCfg!=null && currentProposedCfg.size()>0;
	}
	
	public void addCurrentProposedCfgFromSelectedPoints(Mark mark) {
		if (currentCfgDisplayed!=null) {
			bboxListForRefresh.addAll( currentCfgDisplayed.getCfg() );
		}
		bboxListForRefresh.addAll( new Cfg(mark) );
		this.currentProposedCfg = new Cfg(mark);
		this.currentCfgDisplayed = new ColoredCfg(mark,colorBlue);
		confirmReset.triggerConfirmResetStateChangedEvent();
	}
	
	public void replaceCurrentProposedCfg(Cfg cfgCore, ColoredCfg cfgDisplayed) {
		
		if (this.currentCfgDisplayed!=null) {
			bboxListForRefresh.addAll( currentCfgDisplayed.getCfg() );
		}
		
		bboxListForRefresh.addAll( currentSelectedPointsCfg );
		this.currentSelectedPointsCfg = new Cfg();
		
		this.currentProposedCfg = cfgCore;
		this.currentCfgDisplayed = cfgDisplayed;
		bboxListForRefresh.addAll( currentCfgDisplayed.getCfg() );
		confirmReset.triggerConfirmResetStateChangedEvent();
	}
	
	
	public void removeCurrentProposedCfg() {
		
		if (this.currentCfgDisplayed!=null) {
			bboxListForRefresh.addAll( currentCfgDisplayed.getCfg() );
		}
		
		bboxListForRefresh.addAll( currentSelectedPointsCfg );
		this.currentSelectedPointsCfg = new Cfg();
		
		this.currentProposedCfg = new Cfg();
		this.currentCfgDisplayed = new ColoredCfg();
		confirmReset.triggerConfirmResetStateChangedEvent();
	}
	
	public ColoredCfg generateFullCfg() {
		ColoredCfg coloredCfg = new ColoredCfg();
		if (currentCfgDisplayed!=null) {
			coloredCfg.addAll(currentCfgDisplayed);
		}
		coloredCfg.addAll( cfg.getCfgAccepted(), colorAccepted );	
		coloredCfg.addAll( cfg.getCfgRejected(), colorRejected );
		coloredCfg.addAll(currentSelectedPointsCfg, colorSelectedPoints );
		return coloredCfg;
	}
	
	public void addSelectedPoint( Mark mark ) {
		currentSelectedPointsCfg.add(mark);
		currentProposedCfg = new Cfg();
		currentCfgDisplayed = null;
		bboxListForRefresh.add( mark );
		confirmReset.triggerConfirmResetStateChangedEvent();
	}
	
	public void removeAcceptedMarksAndSelectedPoints( Cfg cfg, List<Point3i> pnts ) {
		for (Mark m : cfg) {
			removeAcceptedMark(m);
		}
		removeSelectedPoints(pnts);
	}
	
	// NB This is not the most efficient, as we have to search a linked list each time to find the mark
	//  but as we don't call it often, it shouldn't be so bad
	private void removeAcceptedMark( Mark mark ) {
		saveMonitor.markAsChanged();
		
		cfg.removeFromEither( mark );
		
		bboxListForRefresh.add( mark );
		confirmReset.triggerConfirmResetStateChangedEvent();
	}

	
	// NB This is not the most efficient, as we have to search a linked list each time to find the mark
	//  but as we don't call it often, it shouldn't be so bad
	private void removeSelectedPoints( List<Point3i> pnts ) {
		
		Cfg toDelete = new Cfg();
		
		for( Point3i p : pnts ) {
			int index = indexOfSelectedPoints(p);
			toDelete.add( currentSelectedPointsCfg.get(index) );
			currentSelectedPointsCfg.remove(index);
		}
		
		currentProposedCfg = new Cfg();
		currentCfgDisplayed = null;
		
		bboxListForRefresh.addAll( toDelete );
		confirmReset.triggerConfirmResetStateChangedEvent();
	}
	
	public Cfg getRefreshListAndReset() {
		 Cfg refreshList = bboxListForRefresh;
		 bboxListForRefresh = new Cfg();
		 
		 if (currentCfgDisplayed!=null) {
			 bboxListForRefresh.addAll( currentCfgDisplayed.getCfg() );
		 }
		 return refreshList;
	}
	
	public int indexOfSelectedPoints( Point3i pnt ) {
		for(int i=0; i<currentSelectedPointsCfg.size(); i++) {
			Mark m = currentSelectedPointsCfg.get(i);
			
			Point3d cp = m.centerPoint();
			if(cp.getX()==pnt.getX() && cp.getY()==pnt.getY() && cp.getZ()==pnt.getZ()) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public boolean hasSelectedPoints() {
		return currentSelectedPointsCfg!=null && currentSelectedPointsCfg.size()>0;
	}
	
	@Override
	public List<Point3i> selectedPointsAsIntegers() {
		List<Point3i> listOut = new ArrayList<>();
		for( Mark m : currentSelectedPointsCfg) {
			Point3d cp = m.centerPoint();
			listOut.add( new Point3i((int) cp.getX(),(int) cp.getY(),(int) cp.getZ()) );
		}
		return listOut;
	}
	
	@Override
	public List<Point3f> selectedPointsAsFloats() {
		List<Point3f> listOut = new ArrayList<>();
		for( Mark m : currentSelectedPointsCfg) {
			Point3d cp = m.centerPoint();
			listOut.add( new Point3f((float) cp.getX(),(float) cp.getY(),(float) cp.getZ()) );
		}
		return listOut;
	}

	public void markAsChanged() {
		saveMonitor.markAsChanged();
	}
	
	public Cfg getProposedCfg() {
		return currentProposedCfg;
	}

	public IConfirmReset confirmReset() {
		return confirmReset;
	}

	public IQueryAcceptedRejected queryAcceptReject() {
		return cfg;
	}
	

	private class ConfirmReset implements IConfirmReset {
		
		private EventListenerList listeners = new EventListenerList();
		
		@Override
		public boolean canConfirm() {
			return (currentSelectedPointsCfg.size()>0 || currentProposedCfg.size()>0);
		}
		
		@Override
		public boolean canReset() {
			return ( (currentCfgDisplayed!=null&&currentCfgDisplayed.size()>0) || currentSelectedPointsCfg.size()>0 || currentProposedCfg.size()>0);
		}
		
		// Resets any unconfirmed activity
		@Override
		public void reset() {
			currentProposedCfg = new Cfg();
			if (currentCfgDisplayed!=null) {
				bboxListForRefresh.addAll( currentCfgDisplayed.getCfg() );
			}
			bboxListForRefresh.addAll( currentSelectedPointsCfg );
			currentCfgDisplayed = null;
			currentSelectedPointsCfg = new Cfg();
			triggerConfirmResetStateChangedEvent();
		}
		
		
		@Override
		public void addConfirmResetStateChangedListener(
				ConfirmResetStateChangedListener e) {
			listeners.add(ConfirmResetStateChangedListener.class, e);
		}

		
		/**
		 * Confirm the current proposed cfg
		 * 
		 * @param accepted TRUE if proposal is accepted, FALSE if rejected
		 * @return TRUE iff confirmation was successful
		 */
		@Override
		public boolean confirm(boolean accepted) {
			saveMonitor.markAsChanged();
			cfg.addAll(accepted,currentProposedCfg.deepCopy());
			
			currentProposedCfg = new Cfg();
			if (currentCfgDisplayed!=null) {
				bboxListForRefresh.addAll( currentCfgDisplayed.getCfg() );
			}
			bboxListForRefresh.addAll( currentSelectedPointsCfg );
			currentCfgDisplayed = null;
			currentSelectedPointsCfg = new Cfg();
			triggerConfirmResetStateChangedEvent();
			return true;
		}
		
		
		public void triggerConfirmResetStateChangedEvent() {
			for( ConfirmResetStateChangedListener e : listeners.getListeners(ConfirmResetStateChangedListener.class)) {
				e.confirmResetStateChanged();
			}
		}
		
		public void dispose() {
			listeners = null;
		}
	};
	

}