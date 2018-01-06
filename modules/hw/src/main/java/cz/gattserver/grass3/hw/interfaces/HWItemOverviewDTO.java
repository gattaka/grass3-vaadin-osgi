package cz.gattserver.grass3.hw.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

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
	private LocalDate purchaseDate;

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
	private String usedInName;

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

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDate purchaseDate) {
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

	public String getUsedInName() {
		return usedInName;
	}

	public void setUsedInName(String usedInName) {
		this.usedInName = usedInName;
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

}
