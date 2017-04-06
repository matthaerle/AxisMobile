package com.acusportrtg.axismobile.JSON_Classes;

/**
 * Created by mhaerle on 4/6/2017.
 */

public class SendProductView {
    private Long ProductID;
    private String ProductUPC;
    private String ShortDescription;
    private int MinLevel;
    private int MaxLevel;
    private String ItemNmbr;
    private Double Price;
    private int PhysicalQoH;
    private int QtyOnOrder;
    private int QtyCommitted;

    public Long getProductID() {
        return ProductID;
    }

    public void setProductID(Long productID) {
        ProductID = productID;
    }

    public String getProductUPC() {
        return ProductUPC;
    }

    public void setProductUPC(String productUPC) {
        ProductUPC = productUPC;
    }

    public String getShortDescription() {
        return ShortDescription;
    }

    public void setShortDescription(String shortDescription) {
        ShortDescription = shortDescription;
    }

    public int getMinLevel() {
        return MinLevel;
    }

    public void setMinLevel(int minLevel) {
        MinLevel = minLevel;
    }

    public int getMaxLevel() {
        return MaxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        MaxLevel = maxLevel;
    }

    public String getItemNmbr() {
        return ItemNmbr;
    }

    public void setItemNmbr(String itemNmbr) {
        ItemNmbr = itemNmbr;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public int getPhysicalQoH() {
        return PhysicalQoH;
    }

    public void setPhysicalQoH(int physicalQoH) {
        PhysicalQoH = physicalQoH;
    }

    public int getQtyOnOrder() {
        return QtyOnOrder;
    }

    public void setQtyOnOrder(int qtyOnOrder) {
        QtyOnOrder = qtyOnOrder;
    }

    public int getQtyCommitted() {
        return QtyCommitted;
    }

    public void setQtyCommitted(int qtyCommitted) {
        QtyCommitted = qtyCommitted;
    }
}
