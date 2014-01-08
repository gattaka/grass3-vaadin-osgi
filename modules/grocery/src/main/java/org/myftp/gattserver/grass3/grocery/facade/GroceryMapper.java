package org.myftp.gattserver.grass3.grocery.facade;

import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.grocery.domain.Product;
import org.myftp.gattserver.grass3.grocery.domain.Purchase;
import org.myftp.gattserver.grass3.grocery.domain.Shop;
import org.myftp.gattserver.grass3.grocery.dto.ProductDTO;
import org.myftp.gattserver.grass3.grocery.dto.PurchaseDTO;
import org.myftp.gattserver.grass3.grocery.dto.ShopDTO;
import org.springframework.stereotype.Component;

@Component("groceryMapper")
public class GroceryMapper {

	public ProductDTO mapProduct(Product e) {
		if (e == null)
			return null;

		ProductDTO dto = new ProductDTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		return dto;
	}

	public List<ProductDTO> mapProducts(List<Product> e) {
		if (e == null)
			return null;

		List<ProductDTO> list = new ArrayList<ProductDTO>();
		for (Product i : e) {
			list.add(mapProduct(i));
		}

		return list;
	}

	public ShopDTO mapShop(Shop e) {
		if (e == null)
			return null;

		ShopDTO dto = new ShopDTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		return dto;
	}

	public List<ShopDTO> mapShops(List<Shop> e) {
		if (e == null)
			return null;

		List<ShopDTO> list = new ArrayList<ShopDTO>();
		for (Shop i : e) {
			list.add(mapShop(i));
		}

		return list;
	}

	public PurchaseDTO mapPurchase(Purchase e) {
		if (e == null)
			return null;

		PurchaseDTO dto = new PurchaseDTO();
		dto.setId(e.getId());
		dto.setCost(e.getCost());
		dto.setDate(e.getDate());
		dto.setProduct(mapProduct(e.getProduct()));
		dto.setQuantity(e.getQuantity());
		dto.setShop(mapShop(e.getShop()));
		return dto;
	}

	public List<PurchaseDTO> mapPurchases(List<Purchase> e) {
		if (e == null)
			return null;

		List<PurchaseDTO> list = new ArrayList<PurchaseDTO>();
		for (Purchase i : e) {
			list.add(mapPurchase(i));
		}

		return list;
	}

}
