/*
 * Created on Oct 27, 2008.
 */
package com.bm.ejb3data.bo;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
@Entity
@DiscriminatorValue("BLANK")
@Table (name = "DOCUMENT_BLANK_PROPERTY")
public class DocumentBlankProperty extends AbstractDocumentProperty {

    // this class has been intentionally left blank

}
