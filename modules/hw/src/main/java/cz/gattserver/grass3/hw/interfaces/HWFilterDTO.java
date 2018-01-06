package cz.gattserver.grass3.hw.interfaces;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

public class HWFilterDTO {

	private String name;
	private HWItemState state;
	private String usedIn;
	private String supervizedFor;
	private BigDecimal price;
	private Date purchaseDateFrom;
	private Date purchaseDateTo;
	private Collection<String> types;

	public String getSupervizedFor() {
		return supervizedFor;
	}

	public void setSupervizedFor(String supervizedFor) {
		this.supervizedFor = supervizedFor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getPurchaseDateFrom() {
		return purchaseDateFrom;
	}

	public void setPurchaseDateFrom(Date purchaseDateFrom) {
		this.purchaseDateFrom = purchaseDateFrom;
	}

	public Date getPurchaseDateTo() {
		return purchaseDateTo;
	}

	public void setPurchaseDateTo(Date purchaseDateTo) {
		this.purchaseDateTo = purchaseDateTo;
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

	public String getUsedIn() {
		return usedIn;
	}

	public void setUsedIn(String usedIn) {
		this.usedIn = usedIn;
	}

	public Collection<String> getTypes() {
		return types;
	}

	public void setTypes(Collection<String> types) {
		this.types = types;
	}

}
