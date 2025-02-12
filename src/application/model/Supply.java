package application.model;

public class Supply {
	private int id;
    private String name;
    private int quantity;
    private int supplierId;
	private String supplierName;

    //Constructors, Getters, Setters, toString()
    public Supply() {
    	
    }
    public Supply(int id, String name, int quantity, int supplierId, String supplierName) {
    	this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
    }
    
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    

    public int getId() {
        return id;
    }

    public void setId(int supplyId) {
        this.id = supplyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getPrice() {
        return quantity;
    }

    public void setPrice(int quantity) {
        this.quantity = quantity;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
    
   

    
    @Override
    public String toString() {
        return "Supply{" +
                "supplyId=" + id +
                ", name='" + name + '\'' +
                ", supplierId=" + supplierId +
                '}';
    }
}
