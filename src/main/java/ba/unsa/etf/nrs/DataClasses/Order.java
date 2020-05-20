package ba.unsa.etf.nrs.DataClasses;

import java.time.LocalDate;

public class Order {
    private int id;
    private User user;
    private PaymentType paymentType;
    private LocalDate date;
    private String status;
    private String orderType;

    public Order() {
    }

    public Order(int id, User user, PaymentType paymentType, LocalDate date, String status, String orderType) {
        this.id = id;
        this.user = user;
        this.paymentType = paymentType;
        this.date = date;
        this.status = status;
        this.orderType = orderType;
    }

    public Order(User user, PaymentType paymentType, LocalDate date, String status, String orderType) {
        this.user = user;
        this.paymentType = paymentType;
        this.date = date;
        this.status = status;
        this.orderType = orderType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setEmployee(User user) {
        this.user = user;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
