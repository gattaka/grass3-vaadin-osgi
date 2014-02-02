package org.myftp.gattserver.grass3.grocery.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.myftp.gattserver.grass3.model.dto.Identifiable;

public class PurchaseDTO implements Identifiable {

	private Long id;

	/**
	 * Kdy
	 */
	@NotNull
	private Date date;

	/**
	 * Kde
	 */
	@NotNull
	private ShopDTO shop;

	/**
	 * Co
	 */
	@NotNull
	private ProductDTO product;

	/**
	 * Za kolik jednotka
	 */
	@NotNull
	private Double cost = 0.0;

	/**
	 * Množství
	 */
	@NotNull
	private Double quantity = 0.0;

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ShopDTO getShop() {
		return shop;
	}

	public void setShop(ShopDTO shop) {
		this.shop = shop;
	}

	public ProductDTO getProduct() {
		return product;
	}

	public void setProduct(ProductDTO product) {
		this.product = product;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Double getCostSum() {
		return getCost() * getQuantity();
	}
}
