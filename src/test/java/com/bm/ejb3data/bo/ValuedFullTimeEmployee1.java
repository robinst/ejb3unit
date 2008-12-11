package com.bm.ejb3data.bo;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("VFT")
public class ValuedFullTimeEmployee1 extends FullTimeEmployee1 {
    
    protected Integer pocketMoney;

    public Integer getPocketMoney() {
        return pocketMoney;
    }

    public void setPocketMoney(Integer pocketMoney) {
        this.pocketMoney = pocketMoney;
    }
    
}
