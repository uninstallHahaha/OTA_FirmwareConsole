package com.jinju.FirmwareServiceTransfer.beans;

import java.util.ArrayList;
import java.util.List;

public class CompanyBean {

    String name;
    List<ProjBean> projBeans=new ArrayList<>();

    public CompanyBean() {
    }

    public CompanyBean(String name, List<ProjBean> projBeans) {
        this.name = name;
        this.projBeans = projBeans;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProjBean> getProjBeans() {
        return projBeans;
    }

    public void setProjBeans(List<ProjBean> projBeans) {
        this.projBeans = projBeans;
    }
}
