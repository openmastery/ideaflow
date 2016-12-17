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
package org.openmastery.time;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class LocalDateTimeService implements TimeService {

	@Override
	public LocalDateTime javaNow() {
		return nowTruncateToSeconds();
	}

	@Override
	public org.joda.time.LocalDateTime now() {
		return jodaNowTruncateToSeconds();
	}

	public static LocalDateTime nowTruncateToSeconds() {
		return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}

	public static org.joda.time.LocalDateTime jodaNowTruncateToSeconds() {
		org.joda.time.LocalDateTime localDateTime = org.joda.time.LocalDateTime.now();
		return localDateTime.minusMillis(localDateTime.getMillisOfSecond());
	}

}
