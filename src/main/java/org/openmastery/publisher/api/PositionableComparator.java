/**
 * Copyright 2016 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.publisher.api;

import java.util.Comparator;

public class PositionableComparator implements Comparator<Positionable> {

	public static final PositionableComparator INSTANCE = new PositionableComparator();

	@Override
	public int compare(Positionable o1, Positionable o2) {
		if (o1.getPosition() == null) {
			throw new IllegalArgumentException("Position is not allowed to be null!"+ o1);
		}
		if (o2.getPosition() == null) {
			throw new IllegalArgumentException("Position is not allowed to be null!"+ o2);
		}
		return o1.getPosition().compareTo(o2.getPosition());
	}

}
