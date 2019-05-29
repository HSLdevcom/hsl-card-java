/*
 * Compare.java
 *
 * Copyright (C) 2012 HSL/HRT (Helsingin seudun liikenne/ Helsinki Region Transport)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hsl.util;

public class Compare 
{
	public static boolean equals(byte[] a, byte[] b) 
	{
		if (a.length != b.length)
			return false;

		for (int i = 0; i < a.length; i++) 
		{
			if (a[i] != b[i])
				return false;
		}
		
		return true;
	}

}
