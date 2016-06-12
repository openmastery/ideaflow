package org.openmastery.mapper

import org.openmastery.testsupport.BeanCompare
import org.openmastery.time.LocalDateTimeService
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

class EntityMapperSpec extends Specification {

	private EntityMapper entityMapper = new EntityMapper()
	private BeanCompare beanCompare = new BeanCompare().excludeFields("sourceField", "targetField")

	def "mapIfNotNull should return null on null input"() {
		expect:
		entityMapper.mapIfNotNull(null, Object.class) == null
	}

	def "mapIfNotNull should copy variables"() {
		given:
		Source source = new Source(field: "value")

		expect:
		beanCompare.assertEquals(source, entityMapper.mapIfNotNull(source, Target))
	}

	def "mapList should map list of objects"() {
		given:
		List sources = [new Source(field: "value-1"), new Source(field: "value-2")]

		expect:
		beanCompare.assertEquals(sources, entityMapper.mapList(sources, Target))
	}

	def "should copy types defined in defaultDozerConfig.xml by referenced"() {
		given:
		Source source = new Source(
				field: "value",
				localDateTime: LocalDateTime.now(),
				duration: Duration.ofSeconds(10)
		)

		when:
		Target target = entityMapper.mapIfNotNull(source, Target)

		then:
		assert source.localDateTime.is(target.localDateTime)
		assert source.duration.is(target.duration)
		beanCompare.assertEquals(source, target)
	}

	def "should convert between joda and java8 time"() {
		given:
		LocalDateTime javaNow = LocalDateTimeService.nowTruncateToSeconds()
		org.joda.time.LocalDateTime jodaNow = LocalDateTimeService.jodaNowTruncateToSeconds()
		Source source = new Source(
				convertJavaLocalDateTime: javaNow,
				convertJodaLocalDateTime: jodaNow,
		)

		when:
		Target target = entityMapper.mapIfNotNull(source, Target)

		then:
		assert javaNow.toEpochSecond(ZoneOffset.UTC) == (target.convertJavaLocalDateTime.toDate(TimeZone.getTimeZone("UTC")).time / 1000)
		assert (jodaNow.toDate(TimeZone.getTimeZone("UTC")).time / 1000) == target.convertJodaLocalDateTime.toEpochSecond(ZoneOffset.UTC)
	}

}
