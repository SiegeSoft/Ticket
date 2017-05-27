package com.studiostorquato.troiapark.model;

/**
 * Created by rafaelmagalhaes on 21/04/17.
 */

public class Ticket {

    String date;
    String ticket;
    String address;
    String value;
    String credits;
    String sign;
    String total;

    public Ticket(String date, String ticket, String address, String value, String credits, String sign, String total) {
        this.date = date;
        this.ticket = ticket;
        this.address = address;
        this.value = value;
        this.credits = credits;
        this.sign = sign;
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public String getTicket() {
        return ticket;
    }

    public String getAddress() {
        return address;
    }

    public String getValue() {
        return value;
    }

    public String getCredits() {
        return credits;
    }

    public String getSign() {
        return sign;
    }

    public String getTotal() {
        return total;
    }
}
