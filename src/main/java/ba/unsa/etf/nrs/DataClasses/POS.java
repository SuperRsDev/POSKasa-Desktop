package ba.unsa.etf.nrs.DataClasses;

public class POS {
    private int id;
    private Order order;
    private int totalSum;
    private int fiscalNumber;

    public POS(Order order, int totalSum, int fiscalNumber) {
        this.order = order;
        this.totalSum = totalSum;
        this.fiscalNumber = fiscalNumber;
    }

    public POS(int id, Order order, int totalSum, int fiscalNumber) {
        this.id = id;
        this.order = order;
        this.totalSum = totalSum;
        this.fiscalNumber = fiscalNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public POS() {
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(int totalSum) {
        this.totalSum = totalSum;
    }

    public int getFiscalNumber() {
        return fiscalNumber;
    }

    public void setFiscalNumber(int fiscalNumber) {
        this.fiscalNumber = fiscalNumber;
    }
}
