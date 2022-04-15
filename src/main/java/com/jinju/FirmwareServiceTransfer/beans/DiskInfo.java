package com.jinju.FirmwareServiceTransfer.beans;

public class DiskInfo {

    Long used;
    Long total;

    Double usedRate;

    public DiskInfo() {
    }

    public DiskInfo(Long used, Long total, Double usedRate) {
        this.used = used;
        this.total = total;
        this.usedRate = usedRate;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Double getUsedRate() {
        return usedRate;
    }

    public void setUsedRate(Double usedRate) {
        this.usedRate = usedRate;
    }
}
