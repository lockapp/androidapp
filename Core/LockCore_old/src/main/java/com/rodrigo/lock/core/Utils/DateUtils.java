package com.rodrigo.lock.core.Utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static Integer convertFechaCaducidadToInt(Date fecha) {
    	String fechaCaducidad;
    	
    	Calendar cal = Calendar.getInstance();
	    cal.setTime( fecha);
	    int year = cal.get(Calendar.YEAR);
	    int month = cal.get(Calendar.MONTH);
	    int day = cal.get(Calendar.DAY_OF_MONTH);
    	
        String dia;
        if (day < 10)
            dia = "0" + day;
        else
            dia= String.valueOf(day);

        String mes;
        if (month < 10)
            mes = "0" + month;
        else
            mes= String.valueOf(month);

        fechaCaducidad = year + mes + dia;
        return Integer.valueOf(fechaCaducidad);
    }

}
