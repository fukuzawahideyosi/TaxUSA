package com.panda.servlet.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class MyRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String, String[]> customParams = new HashMap<>();

    public MyRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void setParameter(String name, String value) {
        customParams.put(name, new String[]{value});
    }

    @Override
    public String getParameter(String name) {
        String[] values = customParams.get(name);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new HashMap<>(super.getParameterMap());
        map.putAll(customParams);
        return map;
    }

    @Override
    public String[] getParameterValues(String name) {
        if (customParams.containsKey(name)) {
            return customParams.get(name);
        }
        return super.getParameterValues(name);
    }
}
