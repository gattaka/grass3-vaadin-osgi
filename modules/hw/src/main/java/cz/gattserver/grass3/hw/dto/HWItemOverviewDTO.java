package cz.gattserver.grass3.hw.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * HW Objekt
 */
public class HWItemOverviewDTO {

	/**
	 * Identifikátor hw
	 */
	private Long id;

	/**
	 * Název
	 */
	private String name;

	/**
	 * Datum zakoupení (získání)
	 */
	private Date purchaseDate;

	/**
	 * Cena
	 */
	private BigDecimal price;

	/**
	 * Stav hw - funkční, rozbitý, poruchový, bližší popis
	 */
	private HWItemState state;

	/**
	 * Součást celku
	 */
	private HWItemOverviewDTO usedIn;

	/**
	 * Spravováno pro (spravuju tohle zařízení někomu?)
	 */
	private String supervizedFor;

	public String getSupervizedFor() {
		return supervizedFor;
	}

	public void setSupervizedFor(String supervizedFor) {
		this.supervizedFor = supervizedFor;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public HWItemState getState() {
		return state;
	}

	public void setState(HWItemState state) {
		this.state = state;
	}

	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HWItemOverviewDTO) {
			return (((HWItemOverviewDTO) obj).getId().equals(id));
		} else
			return false;
	}

	public String getUsedInName() {
		return usedIn == null ? "" : usedIn.getName();
	}

	public HWItemOverviewDTO getUsedIn() {
		return usedIn;
	}

	public void setUsedIn(HWItemOverviewDTO usedIn) { 
		this.usedIn = usedIn;
	}

}
