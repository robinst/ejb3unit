/*
 * Created on Nov 24, 2008.
 * (C) 2008 ICZ, a.s.
 */
package com.bm.ejb3data.bo;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
@Entity
public class BlobItem {

    @Id
    private Long id;

    @Column (name = "DESC")
    private String desc;

    @Lob
    @Column (name = "BLOB_DATA", nullable = false)
    private byte[] blobData;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column (name = "BLOB_DATA2")
    private byte[] blobData2;

    public byte[] getBlobData() {
        return blobData;
    }

    public void setBlobData(byte[] blobData) {
        this.blobData = blobData;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getBlobData2() {
        return blobData2;
    }

    public void setBlobData2(byte[] blobData2) {
        this.blobData2 = blobData2;
    }
}
