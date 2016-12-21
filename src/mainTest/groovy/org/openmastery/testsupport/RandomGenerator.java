package org.openmastery.testsupport;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import lombok.extern.slf4j.Slf4j;
import org.fluttercode.datafactory.impl.DataFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Originally sourced from https://github.com/BancVue/common-spring-boot/blob/master/src/mainTest/groovy/com/bancvue/boot/testsupport/RandomGenerator.groovy
 */
@Slf4j
public class RandomGenerator {

    private static Random seedRandom = new Random();
    private Random random;
    private DataFactory df;
    private Lorem lorem = LoremIpsum.getInstance();

    private static long getSeed() {
        return seedRandom.nextLong();
    }

    public RandomGenerator() {
        this(getSeed());
    }

    public RandomGenerator(long seed) {
        log.info("Creating RandomGenerator with seed value of " + seed);
        random = new Random(seed);
        df = new DataFactory();
        df.randomize(random.nextInt());
    }

    public long id() {
        return toId(random.nextLong());
    }

    public int intId() {
        return (int) toId(random.nextInt());
    }

    private long toId(long number) {
        if (number < 0) {
            number *= -1;
        } else if (number == 0) {
            number = 1;
        }
        return number;
    }

    public int tinyInt() {
        return intBetween(0, 50);
    }

    public int negativeInt() { return intBetween(Integer.MIN_VALUE, -1); }

    public int intBetween(int min, int max) {
        return df.getNumberBetween(min, max);
    }

    public int nextInt() {
        return random.nextInt();
    }

    public long nextLong() {
        return random.nextLong();
    }

    public double nextDouble() {
        return random.nextDouble();
    }

    public LocalDate dateInPastDays(int numOfDays) {
        return LocalDate.now().minusDays(intBetween(1, numOfDays));
    }

    public LocalDate dateInFuture(int numOfDays) {
        return LocalDate.now().plusDays(intBetween(1, numOfDays));
    }

    /**
     * Returns a string of random characters.
     */
    public String text(int maxLength) {
        // NOTE: we're using getRandomChars instead of getRandomText b/c the random text isn't so random.
        // they basically use a dictionary of words of specific lengths and the number of choices can be
        // very small (e.g. size 10 equates to 2 distinct words)
        int length = intBetween(1, maxLength);
        return df.getRandomChars(length);
    }

    public String optionalText(int length) {
        return shouldBeNull() ? null : text(length);
    }

    public String words(int length) {
        return lorem.getWords(length).substring(0, length).trim();
    }

    public String optionalWords(int length) {
        return shouldBeNull() ? null : words(length);
    }

    private boolean shouldBeNull() {
        return df.chance(5);
    }

    public String phoneNumber() {
        return df.getNumberText(3) + "-" + df.getNumberText(3) + "-" + df.getNumberText(4);
    }

    public String numberText(int length) {
        return df.getNumberText(length);
    }

    public String optionalPhoneNumber() {
        return shouldBeNull() ? null : phoneNumber();
    }

    public String address() {
        return df.getAddress();
    }

    public String optionalAddress() {
        return shouldBeNull() ? null : address();
    }

    public String city() {
        return df.getCity();
    }

    public String email() {
        return df.getEmailAddress().replaceAll("\\s+", "_");
    }

    public String optionalEmail() {
        return shouldBeNull() ? null : email();
    }

    public <T> T item(List<T> items) {
        return df.getItem(items);
    }

    public <T> T item(T... items) {
        return df.getItem(Arrays.asList(items));
    }

    public String name() {
        return df.getName();
    }

    public String firstName() {
        return df.getFirstName();
    }

    public String lastName() {
        return df.getLastName();
    }

    public boolean coinFlip() {
        return df.chance(50);
    }

    public boolean weightedCoinFlip(int probability) {
        return df.chance(probability);
    }

    public LocalDateTime dayOfYear() {
        Date beginningOfYear = new Date(LocalDateTime.now().getYear(), 0, 1);
        Date randomDayOfYear = df.getDate(beginningOfYear, 0, 365);
        Instant instant = Instant.ofEpochMilli(randomDayOfYear.getTime());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public Duration duration() {
        int hours = intBetween(0, 2);
        int minutes = intBetween(0, 59);
        int seconds = intBetween(1, 59);
        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }

    public Duration smallDuration() {
        return Duration.ofMinutes(intBetween(1, 30)).plusSeconds(intBetween(0, 59));
    }

    public String filePath() {
        return "/tmp/" + df.getRandomChars(3, 10);
    }

}
