package com.studiostorquato.pareepague;

/**
 * Created by rafae on 24/05/2016.
 */


/**
 * P25ConnectionException.
 *
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */

public class P25ConnectionException extends Exception  {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    String error = "";

    public P25ConnectionException(String msg) {
        super(msg);

        error = msg;
    }

    public String getError() {
        return error;
    }


}
