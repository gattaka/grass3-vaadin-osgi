package org.myftp.gattserver.grass3.grocery.dto;

import java.util.Date;

import javax.persistence.ManyToOne;

import org.myftp.gattserver.grass3.model.dto.Identifiable;

public class PurchaseDTO implements Identifiable{

	private Long id;

	/**
	 * Kdy
	 */
	private Date date;

	/**
	 * Kde
	 */
	@ManyToOne
	private ShopDTO shop;

	/**
	 * Co
	 */
	@ManyToOne
	private ProductDTO product;

	/**
	 * Za kolik jednotka
	 */
	private Double cost;

	/**
	 * Množství
	 */
	private Double quantity;

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

}
