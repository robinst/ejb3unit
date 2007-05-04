package com.bm.data.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Test entity bean with annotated methods. Represents a purchase order.
 * 
 * @author Daniel Wiese
 * 
 */
@Entity
@Table(name = "PURCHASE_ORDER")
public class Order implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	private double total;

	private Collection<LineItem> lineItems;

	private Date expiration;

	/**
	 * The pk.
	 * 
	 * @return - the pk
	 */
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	/**
	 * Return the id.
	 * 
	 * @param id -
	 *            the id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * The total.
	 * 
	 * @return - the total
	 */
	public double getTotal() {
		return total;
	}

	/**
	 * Set the total.
	 * 
	 * @param total -
	 *            total
	 */
	public void setTotal(double total) {
		this.total = total;
	}

	@Column(name = "expiration", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	/**
	 * Adds a line item.
	 * 
	 * @param product -
	 *            the product
	 * @param quantity -
	 *            the quantity
	 * @param price -
	 *            the price
	 */
	public void addPurchase(String product, int quantity, double price) {
		if (lineItems == null) {
			lineItems = new ArrayList<LineItem>();
		}

		LineItem item = new LineItem();
		item.setOrder(this);
		item.setProduct(product);
		item.setQuantity(quantity);
		item.setSubtotal(quantity * price);
		lineItems.add(item);
		total += quantity * price;
	}

	/**
	 * The line items.
	 * 
	 * @return - the line items
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "order")
	public Collection<LineItem> getLineItems() {
		return lineItems;
	}

	/**
	 * Sets the line items.
	 * 
	 * @param lineItems -
	 *            die libne items
	 */
	public void setLineItems(Collection<LineItem> lineItems) {
		this.lineItems = lineItems;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof Order) {
			final Order otherC = (Order) other;
			final EqualsBuilder builder = new EqualsBuilder();
			builder.append(this.total, otherC.total);
			builder.append(this.id, otherC.id);
			// line items are not ordered
			// builder.append(this.lineItems, otherC.lineItems);
			return builder.isEquals();
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.total);
		builder.append(this.id);
		// builder.append(this.lineItems);
		return builder.toHashCode();
	}
}
