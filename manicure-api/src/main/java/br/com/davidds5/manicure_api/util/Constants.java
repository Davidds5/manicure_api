package br.com.davidds5.manicure_api.util;

public class Constants {

    public static final String API_VERSION = "v1";
    public static final String BASE_PATH = "/api/" + API_VERSION;

    // Validações
    public static final long CANCEL_HOURS_AHEAD = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MIN_NAME_LENGTH = 2;

    // Mensagens
    public static final String EMAIL_EXISTS = "Email já cadastrado";
    public static final String TIME_CONFLICT = "Horário ocupado";
    public static final String FUTURE_DATE = "Data deve ser futura";
}