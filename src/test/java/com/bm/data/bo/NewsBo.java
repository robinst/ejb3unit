package com.bm.data.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Repreasentitert eine Boersen-Nachricht die zu einer Aktie gehoert.
 * 
 * @author Daniel Wiese
 * @since 18.09.2005
 */
@Entity
@Table(name = "sysnews")
@NamedQueries(value = {
		@NamedQuery(name = "NewsBo.allNewsallStocks", query = "from com.bm.data.bo.NewsBo"),
		@NamedQuery(name = "NewsBo.allNews", query = "SELECT c FROM com.bm.data.bo.NewsBo c WHERE c.primaryKey.wkn = :wknParam and datum >= :beginParam and datum<=:endParam order by datum") })
public class NewsBo implements Serializable {
	@Transient
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private NewsId primaryKey;

	// fields
	@Column(name = "agentur", nullable = false, length = 50)
	private String agentur;

	@Column(name = "text", nullable = false)
	private String text;

	@Column(name = "tag", nullable = true)
	private Integer tag;

	@Column(name = "zusatzinfo", nullable = true)
	private String zusatzinfo;

	@Column(name = "aktienliste", nullable = true, length = 250)
	private String aktienliste;

	@Column(name = "quellenID", nullable = true)
	private Integer quellenID;

	/**
	 * Default constructor Constructor.
	 */
	public NewsBo() {

	}

	/**
	 * Constructor for primary key.
	 * 
	 * @param datumInMillis -
	 *            zeitpunkt der nachricht
	 * @param wkn -
	 *            die wkn zu der die nachricht gehoert
	 * @param ueberschrift -
	 *            die uebershrift
	 */
	public NewsBo(final Long datumInMillis, final int wkn,
			final String ueberschrift) {
		this.primaryKey = new NewsId();
		this.setDatumInMillis(datumInMillis);
		this.setWkn(wkn);
		this.setUeberschrift(ueberschrift);
	}

	/**
	 * Constructor for required fields.
	 * 
	 * @param datumInMillis -
	 *            zeitpunkt der nachricht
	 * @param wkn -
	 *            die wkn zu der die nachricht gehoert
	 * @param ueberschrift -
	 *            die uebershrift
	 * @param agentur -
	 *            die agentur
	 * @param text -
	 *            der text der nachricht (ev. sehr lang)
	 * 
	 */
	public NewsBo(final Long datumInMillis, final int wkn,
			final String ueberschrift, final String agentur, final String text) {
		this.primaryKey = new NewsId();
		this.setDatumInMillis(datumInMillis);
		this.setWkn(wkn);
		this.setUeberschrift(ueberschrift);
		this.setAgentur(agentur);
		this.setText(text);
	}

	/**
	 * Setzt die wkn als integer.
	 * 
	 * @author Daniel Wiese
	 * @since 18.09.2005
	 * @param wkn -
	 *            die wkn als integer
	 */
	public final void setWknInteger(final int wkn) {
		this.setWkn(wkn);
	}


	/**
	 * Die toString methode.
	 * 
	 * @author Daniel Wiese
	 * @since 18.09.2005
	 * @see java.lang.Object#toString()
	 */
	public final String toString() {
		StringBuilder buf = new StringBuilder(200);
		String laengeText = ((this.getText() != null) ? String.valueOf(this
				.getText().length()) : "null");
		buf.append(this.getDatumInMillis()).append(": ").append(
				this.getUeberschrift());
		buf.append(" (WKN=").append(this.getWkn()).append(
				", Laenge des Textes=");
		buf.append(laengeText).append(")");
		return buf.toString();
	}

	/**
	 * Liefert eine detailiertere toString Methode.
	 * 
	 * @return - eine detailierte toString methode
	 */
	public final String toStringDetail() {
		StringBuilder buf = new StringBuilder(((this.getText() != null) ? this
				.getText().length() : 200));
		buf.append(this.getDatumInMillis()).append(": ").append(
				this.getUeberschrift()).append("\n");
		buf.append("Agentur: ").append(this.getAgentur()).append(", Quelle: ")
				.append(this.getQuellenID()).append(", Zusatzinformation: ")
				.append(this.getZusatzinfo()).append("\n");
		buf.append(this.getText());

		return buf.toString();

	}

	/**
	 * Liefert true wenn die Nachrichten identisch sind.
	 * 
	 * @author Daniel Wiese
	 * @since 18.09.2005
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other instanceof NewsBo) {
			final NewsBo otherCast = (NewsBo) other;
			final EqualsBuilder builder = new EqualsBuilder();
			builder.append(this.getAgentur(), otherCast.getAgentur());
			builder.append(this.getWkn(), otherCast.getWkn());
			builder.append(this.getDatumInMillis(), otherCast
					.getDatumInMillis());
			builder.append(this.getText(), otherCast.getText());
			builder.append(this.getUeberschrift(), otherCast.getUeberschrift());
			return builder.isEquals();

		} else {
			return false;
		}
	}

	/**
	 * Liefer den hash code - gleich be gleichen nachrichten.
	 * 
	 * @author Daniel Wiese
	 * @since 18.09.2005
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder(17, 37);
		builder.append(this.getAgentur());
		builder.append(this.getWkn());
		builder.append(this.getDatumInMillis());
		builder.append(this.getText());
		builder.append(this.getUeberschrift());
		return builder.toHashCode();
	}

	/**
	 * The agency.
	 * 
	 * @return Returns the agentur.
	 */
	public String getAgentur() {
		return this.agentur;
	}

	/**
	 * The agency.
	 * 
	 * @param agentur
	 *            The agentur to set.
	 */
	public void setAgentur(java.lang.String agentur) {
		this.agentur = agentur;
	}

	/**
	 * The stock list.
	 * 
	 * @return Returns the aktienliste.
	 */
	public java.lang.String getAktienliste() {
		return this.aktienliste;
	}

	/**
	 * The stck list.
	 * 
	 * @param aktienliste
	 *            The aktienliste to set.
	 */
	public void setAktienliste(java.lang.String aktienliste) {
		this.aktienliste = aktienliste;
	}

	/**
	 * The millis.
	 * 
	 * @return Returns the datumInMillis.
	 */
	public Long getDatumInMillis() {
		return this.primaryKey.datumInMillis;
	}

	/**
	 * The millis.
	 * 
	 * @param datumInMillis
	 *            The datumInMillis to set.
	 */
	public void setDatumInMillis(java.lang.Long datumInMillis) {
		this.primaryKey.datumInMillis = datumInMillis;
	}

	/**
	 * The id.
	 * 
	 * @return Returns the quellenID.
	 */
	public java.lang.Integer getQuellenID() {
		return this.quellenID;
	}

	/**
	 * The id.
	 * 
	 * @param quellenID
	 *            The quellenID to set.
	 */
	public void setQuellenID(java.lang.Integer quellenID) {
		this.quellenID = quellenID;
	}

	/**
	 * The day.
	 * 
	 * @return Returns the tag.
	 */
	public java.lang.Integer getTag() {
		return this.tag;
	}

	/**
	 * The tag.
	 * 
	 * @param tag
	 *            The tag to set.
	 */
	public void setTag(java.lang.Integer tag) {
		this.tag = tag;
	}

	/**
	 * The text.
	 * 
	 * @return Returns the text.
	 */
	public java.lang.String getText() {
		return this.text;
	}

	/**
	 * The text.
	 * 
	 * @param text
	 *            The text to set.
	 */
	public void setText(java.lang.String text) {
		this.text = text;
	}

	/**
	 * The title.
	 * 
	 * @return Returns the ueberschrift.
	 */
	public java.lang.String getUeberschrift() {
		return this.primaryKey.ueberschrift;
	}

	/**
	 * The title.
	 * 
	 * @param ueberschrift
	 *            The ueberschrift to set.
	 */
	public void setUeberschrift(java.lang.String ueberschrift) {
		this.primaryKey.ueberschrift = ueberschrift;
	}

	/**
	 * The wkn.
	 * 
	 * @return Returns the wkn.
	 */
	public int getWkn() {
		return this.primaryKey.wkn;
	}

	/**
	 * The wkn.
	 * 
	 * @param wkn
	 *            The wkn to set.
	 */
	public void setWkn(int wkn) {
		this.primaryKey.wkn = wkn;
	}

	/**
	 * The info.
	 * 
	 * @return Returns the zusatzinfo.
	 */
	public String getZusatzinfo() {
		return this.zusatzinfo;
	}

	/**
	 * The inf.
	 * 
	 * @param zusatzinfo
	 *            The zusatzinfo to set.
	 */
	public void setZusatzinfo(String zusatzinfo) {
		this.zusatzinfo = zusatzinfo;
	}

	/**
	 * The PK Class from News - wird benutzt, da es sich um einen
	 * zusammengesetzten Key handelt.
	 * 
	 * @author Daniel
	 * 
	 */
	@Embeddable
	public static class NewsId implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * PK Comonent 1: Uberschrift der Meldung.
		 */
		@Column(name = "ueberschrift", nullable = false, length = 250)
		private String ueberschrift;

		/**
		 * PK Comonent 2: WKN der Meldung.
		 */
		@Column(name = "wkn", nullable = false)
		private int wkn;

		/**
		 * PK Comonent 3: Datum der Meldung.
		 */
		@Column(name = "datum", nullable = false)
		private long datumInMillis;

		/**
		 * Returns the datumInMillis.
		 * 
		 * @return Returns the datumInMillis.
		 */
		public long getDatumInMillis() {
			return this.datumInMillis;
		}

		/**
		 * Sets the datumInMillis.
		 * 
		 * @param datumInMillis
		 *            The datumInMillis to set.
		 */
		public void setDatumInMillis(long datumInMillis) {
			this.datumInMillis = datumInMillis;
		}

		/**
		 * Returns the ueberschrift.
		 * 
		 * @return Returns the ueberschrift.
		 */
		public String getUeberschrift() {
			return this.ueberschrift;
		}

		/**
		 * Sets the ueberschrift.
		 * 
		 * @param ueberschrift
		 *            The ueberschrift to set.
		 */
		public void setUeberschrift(String ueberschrift) {
			this.ueberschrift = ueberschrift;
		}

		/**
		 * Returns the wkn.
		 * 
		 * @return Returns the wkn.
		 */
		public int getWkn() {
			return this.wkn;
		}

		/**
		 * Sets the wkn.
		 * 
		 * @param wkn
		 *            The wkn to set.
		 */
		public void setWkn(int wkn) {
			this.wkn = wkn;
		}

		/**
		 * Equals.
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object other) {
			if (other != null && other instanceof NewsId) {
				final NewsId otherC = (NewsId) other;
				final EqualsBuilder eq = new EqualsBuilder();
				eq.append(otherC.datumInMillis, this.datumInMillis);
				eq.append(otherC.ueberschrift, this.ueberschrift);
				eq.append(otherC.wkn, this.wkn);
				return eq.isEquals();
			} else {
				return false;
			}
		}

		/**
		 * HashCode.
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final HashCodeBuilder hb = new HashCodeBuilder(17, 21);
			hb.append(ueberschrift);
			hb.append(wkn);
			hb.append(datumInMillis);
			return hb.toHashCode();
		}

	}

}
