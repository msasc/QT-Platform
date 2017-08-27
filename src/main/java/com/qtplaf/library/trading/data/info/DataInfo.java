/*
 * Copyright (C) 2015 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.qtplaf.library.trading.data.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.util.FormatUtils;
import com.qtplaf.library.util.StringUtils;

/**
 * Base information that describes data in a data list.
 * 
 * @author Miquel Sas
 */
public class DataInfo {

	/** Name. */
	private String name;
	/** A title to use in list or tool tips. */
	private String title;
	/** An optional long description that completely describes the data. */
	private String description;
	
	/** Instrument of data if applicable. */
	private Instrument instrument;
	/** Period. */
	private Period period;
	
	/** The pip scale used for the data in this data list. If -1, take it from the instrument. */
	private int pipScale = -1;
	/** The tick scale used for the data in this data list. If -1, take it from the instrument. */
	private int tickScale = -1;
	
	/** The list of informations about outputs. */
	private List<OutputInfo> outputs = new ArrayList<>();
	/** Map of output indexes. */
	private Map<String, Integer> mapIndexes = new HashMap<>();
	
	/** The working session. */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public DataInfo(Session session) {
		super();
		this.session = session;
	}

	/**
	 * Check whether this data info is equal to the argument object.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataInfo) {
			DataInfo info = (DataInfo) obj;
			if (!getName().equals(info.getName())) {
				return false;
			}
			if (!getInstrument().equals(info.getInstrument())) {
				return false;
			}
			if (!getPeriod().equals(info.getPeriod())) {
				return false;
			}
			if (getPipScale() != info.getPipScale()) {
				return false;
			}
			if (getTickScale() != info.getTickScale()) {
				return false;
			}
			int count = getOutputCount();
			if (count != info.getOutputCount()) {
				return false;
			}
			for (int i = 0; i < count; i++) {
				if (!getOutput(i).equals(info.getOutput(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Adds an output data information to the list of outputs.
	 * 
	 * @param name The output name.
	 * @param shortName The output short name.
	 * @param index The index in the data.
	 */
	public void addOutput(String name, String shortName, int index) {
		addOutput(new OutputInfo(name, shortName, index));
	}

	/**
	 * Add the output and map the index to the name.
	 * 
	 * @param output The output.
	 */
	private void addOutput(OutputInfo output) {
		outputs.add(output);
		mapIndexes.put(output.getName(), output.getIndex());
	}

	/**
	 * Returns the output index given the name of the output.
	 * 
	 * @param name The name of the output.
	 * @return The output index or -1 if the name is not valid.
	 */
	public int getOutputIndex(String name) {
		Integer index = mapIndexes.get(name);
		if (index == null) {
			return -1;
		}
		return index.intValue();
	}

	/**
	 * Returns the output info by data index or null if not found.
	 * 
	 * @param index The data index.
	 * @return Teh output info.
	 */
	public OutputInfo getOutputByDataIndex(int index) {
		for (OutputInfo output : outputs) {
			if (output.getIndex() == index) {
				return output;
			}
		}
		return null;
	}

	/**
	 * Adds an output to the list of outputs.
	 * 
	 * @param name The output name.
	 * @param shortName The output short name.
	 * @param index The index in the data.
	 * @param description The output description.
	 */
	public void addOutput(String name, String shortName, int index, String description) {
		outputs.add(new OutputInfo(name, shortName, index, description));
	}

	/**
	 * Returns the number of outputs.
	 * 
	 * @return The number of outputs.
	 */
	public int getOutputCount() {
		return outputs.size();
	}

	/**
	 * Returns the output at the given index.
	 * 
	 * @param index The output index.
	 * @return The output.
	 */
	public OutputInfo getOutput(int index) {
		if (index < outputs.size()) {
			return outputs.get(index);
		}
		return null;
	}

	/**
	 * Returns the data instrument.
	 * 
	 * @return The data instrument.
	 */
	public Instrument getInstrument() {
		return instrument;
	}

	/**
	 * Sets the data instrument.
	 * 
	 * @param instrument The data instrument.
	 */
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	/**
	 * Returns the data period.
	 * 
	 * @return The data period.
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * Sets the data period.
	 * 
	 * @param period The data period.
	 */
	public void setPeriod(Period period) {
		this.period = period;
	}

	/**
	 * Returns the identifier or name.
	 * 
	 * @return The unique identifier or name, like for instance <b>SMA</b>.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the unique identifier or name, like for instance <b>SMA</b>.
	 * 
	 * @param id The unique identifier or name, like for instance <b>SMA</b>.
	 */
	public void setName(String id) {
		this.name = id;
	}

	/**
	 * Returns the title to use in list or tool tips.
	 * 
	 * @return The title to use in list or tool tips.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title to use in list or tool tips.
	 * 
	 * @param title The title to use in list or tool tips.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the long description.
	 * 
	 * @return The long description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the long description.
	 * 
	 * @param description The long description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the pip scale to use.
	 * 
	 * @return The pip scale.
	 * @throws UnsupportedOperationException If the pip scale can not be resolved.
	 */
	public int getPipScale() {
		if (pipScale >= 0) {
			return pipScale;
		}
		if (instrument != null) {
			return instrument.getPipScale();
		}
		throw new UnsupportedOperationException("Pip scale can not be resolved");
	}

	/**
	 * Sets the pip scale for the data in this data list. If -1, retrieve it from the instrument.
	 * 
	 * @param pipScale The pip scale.
	 */
	public void setPipScale(int pipScale) {
		this.pipScale = pipScale;
	}

	/**
	 * Returns the tick scale to use.
	 * 
	 * @return The tick scale.
	 * @throws UnsupportedOperationException If the tick scale can not be resolved.
	 */
	public int getTickScale() {
		if (tickScale >= 0) {
			return tickScale;
		}
		if (instrument != null) {
			return instrument.getTickScale();
		}
		throw new UnsupportedOperationException("Tick scale can not be resolved");
	}

	/**
	 * Returns the volume scale or -1 if it can not be resolved.
	 * 
	 * @return The volume scale.
	 */
	public int getVolumeScale() {
		if (instrument != null) {
			return instrument.getVolumeScale();
		}
		return -1;
	}

	/**
	 * Sets the tick or minimum value scale.
	 * 
	 * @param tickScale The tick or minimum value scale.
	 */
	public void setTickScale(int tickScale) {
		this.tickScale = tickScale;
	}

	/**
	 * Returns a display information of the given data.
	 * 
	 * @param data The data.
	 * @return The display info.
	 */
	public String getInfoData(Data data) {
		boolean priceInfo = (this instanceof PriceInfo);
		boolean addVolumeValue = (priceInfo && Data.getVolume(data) != 0);
		StringBuilder b = new StringBuilder();
		int count = getOutputCount();
		for (int i = 0; i < count; i++) {
			OutputInfo output = getOutput(i);
			int index = output.getIndex();
			boolean addValue = true;
			if (index == Data.VOLUME) {
				addValue = addVolumeValue;
			}
			if (addValue) {
				if (i > 0) {
					b.append(", ");
				}
				String shortName = output.getShortName();
				if (shortName != null) {
					b.append(shortName);
					b.append(": ");
				}
				b.append(
					FormatUtils.formattedFromDouble(data.getValue(index), getTickScale(), getSession().getLocale()));
			}
		}
		return b.toString();
	}

	/**
	 * Returns a string representation of this data info.
	 * 
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		StringBuilder info = new StringBuilder();
		StringUtils.append(info, getInstrument());
		StringUtils.appendSep(info, getPeriod(), ", ");
		StringUtils.appendSep(info, getName(), ", ");
		return info.toString();
	}
}
