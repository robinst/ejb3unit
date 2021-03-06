package com.bm.ejb3data.bo;

import java.io.Serializable;

/**
 * Id class.
 * 
 * @author Daniel Wiese
 * 
 */
public class ExpertiseAreasPK implements Serializable {

	private static final long serialVersionUID = 1L;

	Long prodId;

	Long userId;

        public ExpertiseAreasPK() {
        }

        public ExpertiseAreasPK(Long prodId, Long userId) {
            this.prodId = prodId;
            this.userId = userId;
        }
}
