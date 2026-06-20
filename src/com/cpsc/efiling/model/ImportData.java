package com.cpsc.efiling.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

public class ImportData {
    private static final Logger log = LogManager.getLogger(ImportData.class);
    private List<ProductData> products = new ArrayList<ProductData>();

    public List<ProductData> getProducts() { return products; }
    public void setProducts(List<ProductData> products) { this.products = products; }
}
