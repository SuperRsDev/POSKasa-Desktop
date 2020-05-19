package ba.unsa.etf.nrs.DataClasses;

public class Product {
    private int productId;
    private String name;
    private int stockQuantity;
    private String status;
    private String description;
    private int unitPrice;
    private int sellingPrice;
    private Category category;

    public Product(int productId, String name, int stockQuantity, String status, String description, int unitPrice, int sellingPrice, Category category) {
        this.productId = productId;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.description = description;
        this.unitPrice = unitPrice;
        this.sellingPrice = sellingPrice;
        this.category = category;
    }

    public Product() {}

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(int sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return name;
    }
}
