/*
 * Copyright 2017 New Iron Group, Inc.
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
package org.openmastery.time

import java.sql.Timestamp
import java.time.LocalDateTime

class TimeConverter {

	public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
		if (timestamp == null) {
			return null
		}
		timestamp.toLocalDateTime()
	}

	public static Timestamp toSqlTimestamp(LocalDateTime localDateTime) {
		new Timestamp(localDateTime.year - 1900,
				localDateTime.monthValue - 1,
				localDateTime.dayOfMonth,
				localDateTime.hour,
				localDateTime.minute,
				localDateTime.second, 0)
	}

}
