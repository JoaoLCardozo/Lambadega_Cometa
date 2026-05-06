package br.com.gw.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {
    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DateUtils() {}

    public static String formatarData(LocalDate data) {
        return data != null ? data.format(DATA_BR) : "";
    }

    public static String formatarDataHora(LocalDateTime dataHora) {
        return dataHora != null ? dataHora.format(DATA_HORA_BR) : "";
    }
}
