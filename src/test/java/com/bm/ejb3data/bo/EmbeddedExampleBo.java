package com.bm.ejb3data.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Test embeded pk.
 * @author Daniel Wiese
 *
 */
@Entity
@Table(name = "testembedded")
public class EmbeddedExampleBo implements Serializable{

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TestEmbededPK id;

	@Column(name = "price", nullable = false)
	private float price;

	@Column(name = "transactions", nullable = false)
	private short transactions;

	@Column(name = "volume", nullable = false)
	private int volume;

	/**
	 * Parameterless constructor for JSR 220.
	 */
	public EmbeddedExampleBo() {
		id = new TestEmbededPK();
		this.transactions = (short) 1;
	}

	/**
	 * Construktor ohne systemZeit.
	 * 
	 * @param wkn -
	 *            die wkn
	 * @param day -
	 *            det tag (absolut)
	 * @param framenr
	 *            framenr
	 * @param volume -
	 *            die anzehl
	 * @param price -
	 *            der price.
	 * @param transactions
	 *            anzahl Transactionen, die zusammengefasst wurden.
	 */
	public EmbeddedExampleBo(final int wkn, final short day, final short framenr,
			final int volume, final float price, final short transactions) {
		this.id = new TestEmbededPK(wkn, day, framenr);
		this.volume = volume;
		this.price = price;
		this.transactions = transactions;
	}

	/**
	 * Gibt die Eigenschaft wert zurück.
	 * 
	 * @return gibt wert zurück.
	 */
	public float getPrice() {
		return this.price;
	}


	/**
	 * Gibt die Eigenschaft anz zurück.
	 * 
	 * @return gibt anz zurück.
	 */
	public int getVolume() {
		return this.volume;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getWkn() {
		return this.id.wkn;
	}

	/**
	 * Standard equals-Methode.
	 * 
	 * @param obj
	 *            das andere Objekt
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @return .
	 */
	@Override
	public boolean equals(final Object obj) {
		boolean back = false;
		if (obj instanceof EmbeddedExampleBo) {
			final EmbeddedExampleBo otherC = (EmbeddedExampleBo) obj;
			final EqualsBuilder eq = new EqualsBuilder();
			eq.append(this.id, otherC.id);
			back = eq.isEquals();
		}

		return back;
	}

	/**
	 * HshCode.
	 * 
	 * @return hash code
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final HashCodeBuilder builder = new HashCodeBuilder(17, 21);
		builder.append(this.id);
		return builder.toHashCode();
	}

	/**
	 * Setzt die Eigenschaft day.
	 * 
	 * @param day
	 *            ersetzt day.
	 */
	public void setDay(final short day) {
		this.id.day = day;
	}


	/**
	 * Setzt die Eigenschaft volume.
	 * 
	 * @param volume
	 *            ersetzt volume.
	 */
	public void setVolume(final int volume) {
		this.volume = volume;
	}

	/**
	 * Setzt die Eigenschaft wkn.
	 * 
	 * @param wkn
	 *            ersetzt wkn.
	 */
	public void setWkn(final int wkn) {
		this.id.wkn = wkn;
	}

	public TestEmbededPK getId() {
		return id;
	}

	public void setId(TestEmbededPK id) {
		this.id = id;
	}

	public short getTransactions() {
		return transactions;
	}

	public void setTransactions(short transactions) {
		this.transactions = transactions;
	}

	public short getFrameNr() {
		return id.framenr;
	}

	public short getDay() {
		return id.getDay();
	}


	/**
	 * {@inheritDoc}
	 */
	public float getWert() {
		return this.price;
	}



	/**
	 * PK-Klasse.
	 * 
	 * @author Fabian
	 * 
	 */
	@Embeddable
	public static class TestEmbededPK implements Serializable {

		private static final long serialVersionUID = 1L;

		@Column(name = "wkn", nullable = false)
		private int wkn;
		@Column(name = "day", nullable = false)
		private short day;
		@Column(name = "framenr", nullable = false)
		private short framenr;

		public int getWkn() {
			return wkn;
		}

		/**
		 * Standardkonstruktor.
		 */
		public TestEmbededPK() {

		}

		/**
		 * Voller Konstruktor mit allen Parametern.
		 * 
		 * @param wkn .
		 * @param day .
		 * @param framenr .
		 */
		public TestEmbededPK(int wkn, short day, short framenr) {
			super();
			this.wkn = wkn;
			this.day = day;
			this.framenr = framenr;
		}

		public void setWkn(int wkn) {
			this.wkn = wkn;
		}

		public short getDay() {
			return day;
		}

		/**
		 * .
		 * 
		 * @param day
		 *            der neue Tag
		 */
		public void setDay(short day) {
			this.day = day;
		}

		public short getFramenr() {
			return framenr;
		}

		/**
		 * .
		 * 
		 * @param framenr
		 *            neue Framenr
		 */
		public void setFramenr(short framenr) {
			this.framenr = framenr;
		}

		/**
		 * Standard equals-Methode.
		 * 
		 * @param obj
		 *            das andere Objekt
		 * @see java.lang.Object#equals(java.lang.Object)
		 * @return .
		 */
		@Override
		public boolean equals(final Object obj) {
			boolean back = false;
			if (obj instanceof TestEmbededPK) {
				final TestEmbededPK otherC = (TestEmbededPK) obj;
				final EqualsBuilder eq = new EqualsBuilder();
				eq.append(this.getWkn(), otherC.getWkn());
				eq.append(this.getDay(), otherC.getDay());
				eq.append(this.getFramenr(), otherC.getFramenr());

				back = eq.isEquals();
			}

			return back;
		}

		/**
		 * HshCode.
		 * 
		 * @return hash code
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final HashCodeBuilder builder = new HashCodeBuilder(17, 21);
			builder.append(this.getWkn());
			builder.append(this.getDay());
			builder.append(this.getFramenr());
			return builder.toHashCode();
		}


	}
}
