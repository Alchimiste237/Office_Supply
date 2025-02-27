package application.model;

public class OrderItem {
	
	    private int orderItemsId;
	    private int orderId;
	    private double unitPrice;
	    private int supplyId;
	    private String supplyName;
	    private int quantity;


	    // Constructors, getters, and setters

	    public OrderItem() {
	    }

	    public OrderItem(int orderId, int orderItemsId, double unitPrice, String orderStatus, int quantity) {
	        this.orderId = orderId;
	        this.orderItemsId = orderItemsId;
	        this.unitPrice = unitPrice;
	        this.quantity = quantity;
	    
	    }
	    
	    public int getSupplyId() {
	    	return supplyId;
	    }
	    
	    public void setSupplyId(int supplyId) {
	    	this.supplyId = supplyId;
	    }
	    
	    public void setSupplyName(String supplyName) {
	    	this.supplyName = supplyName;
	    }
	    
	    public String getSupplyName() {
	    	return supplyName;
	    }
	    
	    public int getOrderId() {
	        return orderId;
	    }

	    public void setOrderId(int orderId) {
	        this.orderId = orderId;
	    }

	    public int getOrderItemsId() {
	        return orderItemsId;
	    }

	    public void setOrderItemsId(int orderItemId) {
	        this.orderItemsId = orderItemId;
	    }

	     public double getUnitPrice() {
	        return unitPrice;
	    }

	    public void setUnitPrice(double unitPrice) {
	        this.unitPrice = unitPrice;
	    }

	   public int getQuantity() {
	        return quantity;
	    }

	    public void setQuantity(int quantity) {
	        this.quantity = quantity;
	    }

	    @Override
	    public String toString() {
	        return "Order{" +
	                "orderId=" + orderId +
	                ", supplierId=" + orderItemsId +
	                '\'' +
	                '}';
	    }
	}


