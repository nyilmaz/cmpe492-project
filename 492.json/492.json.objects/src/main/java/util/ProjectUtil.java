package util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: nyilmaz
 */
public class ProjectUtil {

    public static final ThreadLocal<DateFormat> TWITTER_DATE_FORMAT = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        }

        @Override
        public DateFormat get() {
            return super.get();
        }

        @Override
        public void set(DateFormat value) {
            super.set(value);
        }

        @Override
        public void remove() {
            super.remove();
        }
    };

    public static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");



}
