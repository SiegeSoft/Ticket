package com.studiostorquato.pareepague.model;

/**
 * Created by rafaelmagalhaes on 21/04/17.
 */

public class Ticket {

    String date;
    String ticket;
    String address;
    String value;
    String phone;
    String sign;

    public Ticket(String date, String ticket, String address, String value, String phone, String sign) {
        this.date = date;
        this.ticket = ticket;
        this.address = address;
        this.value = value;
        this.phone = phone;
        this.sign = sign;
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

    public String getPhone() {
        return phone;
    }

    public String getSign() {
        return sign;
    }
}
