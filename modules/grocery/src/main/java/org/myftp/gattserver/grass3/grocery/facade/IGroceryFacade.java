package org.myftp.gattserver.grass3.grocery.facade;

import java.util.List;

import org.myftp.gattserver.grass3.grocery.dto.ProductDTO;
import org.myftp.gattserver.grass3.grocery.dto.PurchaseDTO;
import org.myftp.gattserver.grass3.grocery.dto.ShopDTO;

public interface IGroceryFacade {

	// Produkty

	void deleteProduct(ProductDTO dto);

	List<ProductDTO> getAllProducts();

	boolean saveProduct(ProductDTO dto);

	// Nákupy

	void deletePurchase(PurchaseDTO dto);

	List<PurchaseDTO> getAllPurchases();

	boolean savePurchase(PurchaseDTO dto);

	// Obchody

	void deleteShop(ShopDTO dto);

	List<ShopDTO> getAllShops();

	boolean saveShop(ShopDTO dto);

}
