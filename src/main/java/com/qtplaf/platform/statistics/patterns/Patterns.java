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

package com.qtplaf.platform.statistics.patterns;

import com.qtplaf.library.ai.data.Pattern;
import com.qtplaf.library.ai.data.DefaultPattern;
import com.qtplaf.library.ai.data.info.InputInfo;
import com.qtplaf.library.ai.data.info.PatternInfo;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.statistics.patterns.input.CandleBodyCenter;
import com.qtplaf.platform.statistics.patterns.input.CandleBodyFactor;
import com.qtplaf.platform.statistics.patterns.input.CandleBullishBearish;
import com.qtplaf.platform.statistics.patterns.input.CandleCenterDiff;
import com.qtplaf.platform.statistics.patterns.input.CandleRangeFactor;

/**
 * Definition of patterns.
 *
 * @author Miquel Sas
 */
public class Patterns {

	/** Data info property. */
	private static final int DATA_INFO = 0;
	/** Look backward property. */
	private static final int LOOK_BACKWARD = 1;
	/** Pattern intput function property. */
	private static final int INPUT_FUNCTION = 2;
	/** Pattern record property. */
	private static final int PATTERN_RECORD = 3;

	/**
	 * Returns the data info stored in the pattern info.
	 * 
	 * @param patternInfo Tha pattern info.
	 * @return The data info.
	 */
	public static DataInfo getDataInfo(PatternInfo patternInfo) {
		return (DataInfo) patternInfo.getProperties().getObject(DATA_INFO);
	}

	/**
	 * Set the data info.
	 * 
	 * @param patternInfo The pattern info.
	 * @param dataInfo The data info.
	 */
	public static void setDataInfo(PatternInfo patternInfo, DataInfo dataInfo) {
		patternInfo.getProperties().setObject(DATA_INFO, dataInfo);
	}

	/**
	 * Returns the look backward property.
	 * 
	 * @param patternInfo The pattern info.
	 * @return The look backward property.
	 */
	public static int getLookBackward(PatternInfo patternInfo) {
		return patternInfo.getProperties().getInteger(LOOK_BACKWARD);
	}

	/**
	 * Set the look backward property.
	 * 
	 * @param patternInfo The pattern info.
	 * @param lookBackward The look backward property.
	 */
	public static void setLookBackward(PatternInfo patternInfo, int lookBackward) {
		patternInfo.getProperties().setInteger(LOOK_BACKWARD, lookBackward);
	}

	/**
	 * Returns the pattern input function.
	 * 
	 * @param input The input info.
	 * @return The pattern input function.
	 */
	public static PatternInput getPatternInput(InputInfo input) {
		return (PatternInput) input.getProperties().getObject(INPUT_FUNCTION);
	}

	/**
	 * Set the input function.
	 * 
	 * @param input The input info.
	 * @param function The input function.
	 */
	public static void setPatternInput(InputInfo input, PatternInput function) {
		input.getProperties().setObject(INPUT_FUNCTION, function);
	}

	/**
	 * Returns the record stored in the pattern.
	 * 
	 * @param pattern The pattern.
	 * @return The source record.
	 */
	public static Record getPatternRecord(Pattern pattern) {
		return (Record) pattern.getProperties().getObject(PATTERN_RECORD);
	}

	/**
	 * Set the pattern record property.
	 * 
	 * @param pattern The pattern.
	 * @param record The record.
	 */
	public static void setPatternRecord(Pattern pattern, Record record) {
		pattern.getProperties().setObject(PATTERN_RECORD, record);
	}

	/**
	 * Pattern informaction for a single/multiple candle patterns.
	 * <p>
	 * Inputs:
	 * <ul>
	 * <li>bullish_bearish: A value of 1.0 indicates bullish, a value of -1.0 beartish, and a value of 0 open ==
	 * close.</li>
	 * <li>range_factor: Gives the measure of how big the candle is. A value of 1.0 is a very big candle, a value of
	 * -1.0 indicates open = high = low = close, zero range.</li>
	 * <li>body_factor: Gives the measure of how big the body is. A value of 1.0 is a marubozu, a value of -1.0
	 * indicates open = close, zero body.</li>
	 * <li>body_center: Gives the measure of the position of the body. A value of 1.0, can only be dragon fly doji,
	 * while a value of -1.0 can only be a gravestone doji.</li>
	 * <li>center_diff: Percentual center difference vs the previous candle. This attribute should make no difference in
	 * a single candle pattern, while it should play an important role in multiple candle patterns.</li>
	 * </ul>
	 * Other measures like upper_shadow_factor or lower_shadow_factor are redundant.
	 * <p>
	 * Outputs:
	 * <ul>
	 * <li>high_wave: Relatively big candle, with small body and big shadows, no matter bullish or bearish.</li>
	 * <li>spinning: Small or very small candle, with small body and shadows, no matter bullish or bearish.</li>
	 * <li>big_white: Big white candle.</li>
	 * <li>big_black: Big black candle.</li>
	 * <li>long_lower_shadow: Relatively big candle with long lower shadow, no matter bullish or bullish.</li>
	 * <li>long_upper_shadow: Relatively big candle with long upper shadow, no matter bullish or bullish.</li>
	 * <li></li>
	 * </ul>
	 * A multiple candle pattern has N times the inputs of a single candle pattern.
	 * 
	 * @return The pattern info.
	 */
	public static PatternInfo getInfoCandle() {

		PatternInfo info = new PatternInfo();
		// Id candle 1.
		info.setId("cd1");
		// Description.
		info.setDescription("Single candle pattern");

		// Input for one candle.
		setInfoCandleInput(info, 1);

		// Single candle outputs.
		info.addOutput("high_wave", "Hig wave, big candle with small body big shadows");
		info.addOutput("spinning", "Spinning, small candle with small body and shadows");
		info.addOutput("big_white", "Big white, big white candle");
		info.addOutput("big_black", "Big blach, big black candle");
		info.addOutput("long_lower", "Relatively big, small body and long lower shadow");
		info.addOutput("long_upper", "Relatively big, small body and long upper shadow");
		info.addOutput("other", "Other, not classified");

		return info;
	}

	/**
	 * Set the input info given the number of candles.
	 * 
	 * @param info The pattern info to fill.
	 * @param candles The number of candles.
	 */
	private static void setInfoCandleInput(PatternInfo info, int candles) {
		setLookBackward(info, candles);
		for (int candle = 0; candle < candles; candle++) {

			// Bullish/bearish indicator.
			InputInfo bullishBearish = new InputInfo("bullish_bearish_" + candle, "Bullish/bearish indicator");
			bullishBearish.getProperties().setObject(INPUT_FUNCTION, new CandleBullishBearish(candle, info));
			info.addInput(bullishBearish);

			InputInfo rangeFactor = new InputInfo("range_factor_" + candle, "Range factor: range vs maximum range");
			rangeFactor.getProperties().setObject(INPUT_FUNCTION, new CandleRangeFactor(candle, info));
			info.addInput(rangeFactor);

			InputInfo bodyFactor = new InputInfo("body_factor_" + candle, "Body factor: body vs range");
			bodyFactor.getProperties().setObject(INPUT_FUNCTION, new CandleBodyFactor(candle, info));
			info.addInput(bodyFactor);

			InputInfo bodyCenter =
				new InputInfo("body_center_" + candle, "Body center factor: body center position vs range");
			bodyCenter.getProperties().setObject(INPUT_FUNCTION, new CandleBodyCenter(candle, info));
			info.addInput(bodyCenter);

			InputInfo centerDiff =
				new InputInfo("center_diff_" + candle, "Center difference current vs previous candle");
			centerDiff.getProperties().setObject(INPUT_FUNCTION, new CandleCenterDiff(candle, info));
			info.addInput(centerDiff);
		}
	}

	/**
	 * Returns the pattern.
	 * 
	 * @param patternInfo The pattern info.
	 * @param record The record that contains the pattern data.
	 * @return The pattern.
	 */
	public static Pattern getPattern(PatternInfo patternInfo, Record record) {

		double[] inputs = new double[patternInfo.getInputCount()];
		for (int i = 0; i < patternInfo.getInputCount(); i++) {
			String alias = patternInfo.getInput(i).getId();
			inputs[i] = record.getValue(alias).getDouble();
		}

		double[] outputs = new double[patternInfo.getOutputCount()];
		for (int i = 0; i < patternInfo.getOutputCount(); i++) {
			String alias = patternInfo.getOutput(i).getId();
			outputs[i] = record.getValue(alias).getDouble();
		}

		String label = record.getValue(Fields.LABEL).getString();

		DefaultPattern pattern = new DefaultPattern();
		pattern.setInputs(inputs);
		pattern.setOutputs(outputs);
		pattern.setLabel(label);
		
		setPatternRecord(pattern, record);

		return pattern;
	}
}
