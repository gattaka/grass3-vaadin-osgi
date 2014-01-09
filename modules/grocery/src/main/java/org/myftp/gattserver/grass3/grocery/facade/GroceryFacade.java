package org.myftp.gattserver.grass3.grocery.facade;

import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.grocery.dao.ProductRepository;
import org.myftp.gattserver.grass3.grocery.dao.PurchaseRepository;
import org.myftp.gattserver.grass3.grocery.dao.ShopRepository;
import org.myftp.gattserver.grass3.grocery.domain.Product;
import org.myftp.gattserver.grass3.grocery.domain.Purchase;
import org.myftp.gattserver.grass3.grocery.domain.Shop;
import org.myftp.gattserver.grass3.grocery.dto.ProductDTO;
import org.myftp.gattserver.grass3.grocery.dto.PurchaseDTO;
import org.myftp.gattserver.grass3.grocery.dto.ShopDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("groceryFacade")
public class GroceryFacade implements IGroceryFacade {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private PurchaseRepository purchaseRepository;

	@Autowired
	private ShopRepository shopRepository;

	@Resource(name = "groceryMapper")
	private GroceryMapper mapper;

	// Produkty

	@Override
	public void deleteProduct(ProductDTO dto) {
		productRepository.delete(dto.getId());
	}

	@Override
	public List<ProductDTO> getAllProducts() {
		return mapper.mapProducts(productRepository.findAll());
	}

	@Override
	public boolean saveProduct(ProductDTO dto) {
		Product product = new Product();
		product.setId(dto.getId());
		product.setName(dto.getName());
		return productRepository.save(product) != null;
	}

	// Nákupy

	@Override
	public void deletePurchase(PurchaseDTO dto) {
		purchaseRepository.delete(dto.getId());
	}

	@Override
	public List<PurchaseDTO> getAllPurchases() {
		return mapper.mapPurchases(purchaseRepository.findAll());
	}

	@Override
	public boolean savePurchase(PurchaseDTO dto) {
		Purchase purchase = new Purchase();
		purchase.setId(dto.getId());
		purchase.setCost(dto.getCost());
		purchase.setDate(dto.getDate());
		purchase.setQuantity(dto.getQuantity());
		purchase.setProduct(productRepository.findOne(dto.getProduct().getId()));
		purchase.setShop(shopRepository.findOne(dto.getShop().getId()));
		return purchaseRepository.save(purchase) != null;
	}

	// Obchody

	@Override
	public void deleteShop(ShopDTO dto) {
		shopRepository.delete(dto.getId());
	}

	@Override
	public List<ShopDTO> getAllShops() {
		return mapper.mapShops(shopRepository.findAll());
	}

	@Override
	public boolean saveShop(ShopDTO dto) {
		Shop shop = new Shop();
		shop.setId(dto.getId());
		shop.setName(dto.getName());
		return shopRepository.save(shop) != null;
	}

}
