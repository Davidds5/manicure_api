package br.com.davidds5.manicure_api.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {
    public static final DateTimeFormatter API_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static LocalDateTime parse(String dateTime){
        return LocalDateTime.parse(dateTime, API_FORMAT);

    }

    public static String format(LocalDateTime dateTime){
        return dateTime.format(API_FORMAT);

    }

    public static Boolean isFuture(LocalDateTime dateTime){
        return dateTime.isAfter(LocalDateTime.now());
    }

    public static long hoursUntil(LocalDateTime future){
        return ChronoUnit.HOURS.between(LocalDateTime.now(), future);
    }

    public static boolean canCancel(LocalDateTime appointmentTime) {
        return hoursUntil(appointmentTime) >= 2;
    }
}