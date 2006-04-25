package com.bm.data.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Repraesentiert eine Aktie wie ADIDAS.
 * 
 * @author Daniel Wiese since 11.09.2005
 */
@Entity
@Table(name = "allstatdata")
@NamedQuery(name = "StockWKNBo.alleAktien", query = "from com.bm.data.bo.StockWKNBo")
public class StockWKNBo implements Comparable, Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	private boolean istGeholt = false;

	@Transient
	private boolean istGeparsed = false;

	@Transient
	private Set<Integer> usedParser = null;

	@Transient
	private double precission = -1;

	@Transient
	private double f1 = -1;

	@Transient
	private double recall = -1;

	@Transient
	private String resultText = null;

	/** primary key * */
	@Id
	@Column(name = "aid", nullable = false)
	private int wkn;

	// fields
	@Column(name = "name", nullable = false, length = 255)
	private String aktienName;

	@Column(name = "isin", nullable = true, length = 50)
	private String isin;

	@Column(name = "symbol", nullable = true, length = 50)
	private String symbol;

	@Column(name = "isKaufModus", nullable = true)
	private Boolean kaufModus = true;

	@Column(name = "brancheCode", nullable = true, length = 4)
	private Integer branchenCode;

	@Column(name = "brancheName", nullable = true, length = 16)
	private String branche;

	@Column(name = "transaktionenTag", nullable = true)
	private Double transaktionenProTag;

	@Column(name = "zumHandelZugelassen", nullable = true)
	private Boolean zumHandelZugelassen;

	@Column(name = "volatilitaet", nullable = true)
	private Double volatilitaet;

	@Column(name = "durschnittskaufkurs", nullable = true)
	private Double durchschnittskaufkurs;

	// OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy =
	// "wkn")
	@Transient
	private Collection<NewsBo> news = new ArrayList<NewsBo>();

	/**
	 * Parameterless Constructor.
	 */
	public StockWKNBo() {
	}

	/**
	 * Constructor for primary key.
	 * 
	 * @param wkn -
	 *            the wkn
	 */
	public StockWKNBo(final int wkn) {
		this.setWkn(wkn);
	}

	/**
	 * Constructor for required fields.
	 * 
	 * @param wkn -
	 *            the wkn
	 * @param kaufModus -
	 *            ob die aktie kekauft werden soll
	 * @param zumHandelZugelassen -
	 *            ob die aktie zum handel zugelassen ist
	 */
	public StockWKNBo(int wkn, boolean kaufModus, boolean zumHandelZugelassen) {

		this.setWkn(wkn);
		this.setKaufModus(kaufModus);
		this.setZumHandelZugelassen(zumHandelZugelassen);
	}

	/**
	 * Constructor for primary key, name.
	 * 
	 * @param wkn -
	 *            the wkn
	 * @param name -
	 *            der aktiename wie ADIDAS
	 */
	public StockWKNBo(int wkn, String name) {
		this(wkn);
		this.setAktienName(name);
	}

	/**
	 * Compare to.
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(Object o) {
		if (o instanceof StockWKNBo) {
			return (this.getWkn() == ((StockWKNBo) o).getWkn()) ? 0 : (-1);
		} else {
			return -1;
		}
	}

	/**
	 * Returnes a external name.
	 * 
	 * @author Daniel Wiese since 11.09.2005
	 * @return - an external name
	 */
	public String getExtName() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getAktienName());
		sb.append(" - ");
		sb.append(this.getBranche());
		sb.append(" (");
		sb.append(this.getWkn());
		sb.append(")");
		return sb.toString();
	}

	/**
	 * To String.
	 * @author Daniel Wiese since 11.09.2005
	 * @see java.lang.Object#toString()
	 */
	public String toString() { 
		StringBuffer sb = new StringBuffer();
		sb.append(this.getAktienName());
		sb.append("  [WKN:");
		sb.append(this.getWkn());
		sb.append("]  Geholt:[");
		sb.append((this.istGeholt) ? "Ja" : "Nein");
		sb.append("]  Geparsed:[");
		sb.append((this.istGeparsed) ? "Ja" : "Nein");
		sb.append("]");

		return sb.toString();
	}

	/**
	 * Registriert einen Parser mit dem diese Aktie an diesem Tag bereits
	 * geparsed wurde.
	 * 
	 * @param parserId -
	 *            de unique id of the used parser
	 */
	public void setUsedParser(Integer parserId) {
		if (this.usedParser == null) {
			this.usedParser = new HashSet<Integer>();
		}
		if (!this.usedParser.contains(parserId)) {
			this.usedParser.add(parserId);
		}
	}

	/**
	 * Ueberpruft ob ein bestimmter Parser fuer diese Aktie schon benutzt wurde.
	 * 
	 * @param maxFactoryNumber -
	 *            the maximum factories available
	 * @return true wenn benutzt
	 */
	public boolean areAllParsersUsed(int maxFactoryNumber) {
		if (this.usedParser == null) {
			return false;
		} else {
			final int usedParsersCount = this.usedParser.size();

			return (usedParsersCount >= maxFactoryNumber) ? true : false;
		}
	}

	/**
	 * Flags fuer den Grab-Process.
	 * 
	 * @return -- true wenn die Aktie geholt ist
	 */
	public boolean getIsGeholt() {
		return this.istGeholt;
	}

	/**
	 * Flags fuer den Grab-Process.
	 * 
	 * @return -- true wenn die Aktie geparst ist
	 */
	public boolean getIsGeparsed() {
		return this.istGeparsed;
	}

	/**
	 * Flag ob schon der grabber Daten zu dieser Aktie geholt hat.
	 * 
	 * @author Daniel Wiese since 11.09.2005
	 * @param istGeholt - true wenn geholt
	 */
	public void setIstGeholt(boolean istGeholt) {
		this.istGeholt = istGeholt;
	}

	/**
	 * Flag ob schon der parser Daten zu dieser Aktie geparst hat.
	 * 
	 * @author Daniel Wiese
	 * @since 11.09.2005
	 * @param istGeparsed - true wenn geparsed
	 */
	public void setIstGeparsedr(boolean istGeparsed) {
		this.istGeparsed = istGeparsed;
	}

	/**
	 * Liefert den f1 wert.
	 * @return Returns the f1.
	 */
	public double getF1() {
		return f1;
	}

	/**
	 * Setzt den f1 wert.
	 * @param f1
	 *            The f1 to set.
	 */
	public void setF1(double f1) {
		this.f1 = f1;
	}

	/**
	 * Liefer die Precision.
	 * @return Returns the precission.
	 */
	public double getPrecission() {
		return precission;
	}

	/**
	 * Setzt die Precission.
	 * @param precission
	 *            The precission to set.
	 */
	public void setPrecission(double precission) {
		this.precission = precission;
	}

	/**
	 * Liefer den recall.
	 * @return Returns the recall.
	 */
	public double getRecall() {
		return recall;
	}

	/**
	 * Setzt den recall.
	 * @param recall
	 *            The recall to set.
	 */
	public void setRecall(double recall) {
		this.recall = recall;
	}

	/**
	 * Liefert den result text.
	 * @return Returns the resultText.
	 */
	public String getResultText() {
		return resultText;
	}

	/**
	 * Setzt den result text.
	 * @param resultText
	 *            The resultText to set.
	 */
	public void setResultText(String resultText) {
		this.resultText = resultText;
	}

	/**
	 * Equals.
	 * @author Daniel Wiese
	 * @since 11.09.2005
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other instanceof StockWKNBo) {
			final StockWKNBo otherCast = (StockWKNBo) other;
			final EqualsBuilder builder = new EqualsBuilder();
			builder.append(this.getWkn(), otherCast.getWkn());
			return builder.isEquals();

		} else {
			return false;
		}
	}

	/**
	 * HahCode.
	 * @author Daniel Wiese
	 * @since 11.09.2005
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.getWkn());
		return builder.toHashCode();
	}

	/**
	 * Return the unique identifier of this class.
	 * 
	 * @return - the wkn (id)
	 */
	public int getWkn() {
		return wkn;
	}

	/**
	 * Set the unique identifier of this class.
	 * 
	 * @param wkn
	 *            the new ID
	 */
	public void setWkn(int wkn) {
		this.wkn = wkn;
	}

	/**
	 * Return the value associated with the column: name.
	 * 
	 * @return - den aktienNamen
	 */
	public String getAktienName() {
		return aktienName;
	}

	/**
	 * Set the value related to the column: name.
	 * 
	 * @param aktienName
	 *            the name value
	 */
	public void setAktienName(java.lang.String aktienName) {
		this.aktienName = aktienName;
	}

	/**
	 * Return the value associated with the column: isin.
	 * 
	 * @return - die isin (wie wkn) der aktie
	 */
	public String getIsin() {
		return isin;
	}

	/**
	 * Set the value related to the column: isin.
	 * 
	 * @param isin
	 *            the isin value
	 */
	public void setIsin(java.lang.String isin) {
		this.isin = isin;
	}

	/**
	 * Return the value associated with the column: symbol.
	 * 
	 * @return - liewert das Tickesymbol (XETRA) der aktie
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Set the value related to the column: symbol.
	 * 
	 * @param symbol
	 *            the symbol value
	 */
	public void setSymbol(java.lang.String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Return the value associated with the column: isKaufModus.
	 * 
	 * @return true -wenn die Aktie gekauft werden darf
	 */
	public boolean isKaufModus() {
		return kaufModus;
	}

	/**
	 * Set the value related to the column: isKaufModus.
	 * 
	 * @param kaufModus
	 *            the isKaufModus value
	 */
	public void setKaufModus(boolean kaufModus) {
		this.kaufModus = kaufModus;
	}

	/**
	 * Return the value associated with the column: brancheCode.
	 * 
	 * @return den (onvista) code der branche
	 */
	public int getBranchenCode() {
		return branchenCode;
	}

	/**
	 * Set the value related to the column: brancheCode.
	 * 
	 * @param branchenCode
	 *            the brancheCode value
	 */
	public void setBranchenCode(int branchenCode) {
		this.branchenCode = branchenCode;
	}

	/**
	 * Return the value associated with the column: brancheName.
	 * 
	 * @return den Namen der Branche
	 */
	public String getBranche() {
		return branche;
	}

	/**
	 * Set the value related to the column: brancheName.
	 * 
	 * @param branche
	 *            the brancheName value
	 */
	public void setBranche(java.lang.String branche) {
		this.branche = branche;
	}

	/**
	 * Return the value associated with the column: transaktionenTag.
	 * 
	 * @return - die durchschnittlichen Transaktionen am Tag
	 */
	public double getTransaktionenProTag() {
		return transaktionenProTag;
	}

	/**
	 * Set the value related to the column: transaktionenTag.
	 * 
	 * @param transaktionenProTag
	 *            the transaktionenTag value
	 */
	public void setTransaktionenProTag(double transaktionenProTag) {
		this.transaktionenProTag = transaktionenProTag;
	}

	/**
	 * Return the value associated with the column: zumHandelZugelassen.
	 * 
	 * @return - tue wenn die Aktie zum Handel zugelassen ist
	 */
	public boolean isZumHandelZugelassen() {
		return zumHandelZugelassen;
	}

	/**
	 * Set the value related to the column: zumHandelZugelassen.
	 * 
	 * @param zumHandelZugelassen
	 *            the zumHandelZugelassen value
	 */
	public void setZumHandelZugelassen(boolean zumHandelZugelassen) {
		this.zumHandelZugelassen = zumHandelZugelassen;
	}

	/**
	 * Return the value associated with the column: volatilitaet.
	 * 
	 * @return die durchschnittliche volatilitaet
	 */
	public double getVolatilitaet() {
		return volatilitaet;
	}

	/**
	 * Set the value related to the column: volatilitaet.
	 * 
	 * @param volatilitaet
	 *            the volatilitaet value
	 */
	public void setVolatilitaet(double volatilitaet) {
		this.volatilitaet = volatilitaet;
	}

	/**
	 * Return the value associated with the column: durschnittskaufkurs.
	 * 
	 * @return - der duchnittlichen preis der aktie
	 */
	public double getDurchschnittskaufkurs() {
		return durchschnittskaufkurs;
	}

	/**
	 * Set the value related to the column: durschnittskaufkurs.
	 * 
	 * @param durchschnittskaufkurs
	 *            the durschnittskaufkurs value
	 */
	public void setDurchschnittskaufkurs(double durchschnittskaufkurs) {
		this.durchschnittskaufkurs = durchschnittskaufkurs;
	}

	/**
	 * Liefert die news.
	 * @return Returns the news.
	 */
	public Collection<NewsBo> getNews() {
		return this.news;
	}

	/**
	 * Setzt die news.
	 * @param news
	 *            The news to set.
	 */
	public void setNews(Collection<NewsBo> news) {
		this.news = news;
	}
}
