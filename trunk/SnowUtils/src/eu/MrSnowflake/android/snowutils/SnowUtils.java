/*
 * SnowUtils - Small Utils lib for Android
 * Copyright (C) 2008 Maarten Krijn 
 * 
 * Contact:  mrsnowflake@gmail.com
 *
 * This file is part of SnowUtils.
 * 
 * SnowUtils is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package eu.MrSnowflake.android.snowutils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class SnowUtils {
	public static final String EMULATOR_IMEI = "000000000000000";
	
	/**
	 * Returns whether the context is running on the android emulator. 
	 * @param ctx The calling context.
	 * @return True: Running on emulator. False: Running on a real device
	 */
	public static boolean isEmulator(Context ctx) {
		TelephonyManager telephonyMgr = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);        
		//to be deleted in final
		// always use simulator when in emulator 
		return !telephonyMgr.getDeviceId().equals(EMULATOR_IMEI);
	}
}