package com.bm.ejb3metadata.annotations.metadata.interfaces;

import javax.ejb.TransactionAttributeType;


/**
 * This interface represents methods which can be call on ClassAnnotationMetadata and MethodAnnotationMetadata.
 * @author Daniel Wiese
 */
public interface ITransactionAttribute {

    /**
     * @return transaction Attribute type
     * @see javax.ejb.TransactionAttributeType
     */
    TransactionAttributeType getTransactionAttributeType();

    /**
     * Set Transaction Attribute Type.
     * @see javax.ejb.TransactionAttributeType
     * @param transactionAttributeType the type of the attribute.
     */
    void setTransactionAttributeType(TransactionAttributeType transactionAttributeType);

}
