package com.cpsc.efiling.model;

import java.util.ArrayList;
import java.util.List;

public class ImportData {
    private List<ProductData> products = new ArrayList<ProductData>();

    public List<ProductData> getProducts() { return products; }
    public void setProducts(List<ProductData> products) { this.products = products; }
}
