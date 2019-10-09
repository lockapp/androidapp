package com.rodrigo.lock.app.Core.Clases;

/**
 * Created by Rodrigo on 13/07/2014.
 */


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHeader implements Serializable {
    boolean cifrar=true;
    boolean caducidad=false;
    String fechaCaducidad = null;
    boolean soloAca=false;
    boolean prohibirExtraer=false;
    boolean copiaSinBloquear=false;


/*
    public boolean isDefault(){
        return !caducidad && !soloAca;
    }
*/
    public void setFechaCaducidad(int year, int month, int day) {
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

        this.fechaCaducidad = year + mes + dia;
    }

    public int getFechaCaducidad() {
        return Integer.valueOf(fechaCaducidad);
    }


    public String getFechaCaducidadFormat() throws ParseException {
        if (fechaCaducidad == null) {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
            Date now = new Date();
            fechaCaducidad = sdfDate.format(now);
        }
        String OLD_FORMAT = "yyyyMMdd";
        String NEW_FORMAT = "dd/MM/yyyy";

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = sdf.parse(fechaCaducidad);
        sdf.applyPattern(NEW_FORMAT);
        return sdf.format(d);
    }




    public boolean isCaducidad() {
        return caducidad;
    }

    public boolean isCifrar() {
        return cifrar;
    }

    public void setCifrar(boolean cifrar) {
        this.cifrar = cifrar;
    }

    public void setFechaCaducidad(String fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public boolean isProhibirExtraer() {
        return prohibirExtraer;
    }

    public void setProhibirExtraer(boolean prohibirExtraer) {
        this.prohibirExtraer = prohibirExtraer;
    }

    public boolean isCopiaSinBloquear() {
        return copiaSinBloquear;
    }

    public void setCopiaSinBloquear(boolean copiaSinBloquear) {
        this.copiaSinBloquear = copiaSinBloquear;
    }

    public void setCaducidad(boolean caducidad) {
        this.caducidad = caducidad;
    }

    public void setSoloAca(boolean soloAca) {
        this.soloAca = soloAca;
    }

    public boolean isSoloAca() {
        return soloAca;
    }

	public static String mergeIdInPassword(String pass) {
		// TODO Auto-generated method stub
		return pass;
	}






}
