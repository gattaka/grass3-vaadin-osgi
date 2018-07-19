package cz.gattserver.grass3.drinks.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "DRINK")
public class Drink {

	/**
	 * DB id
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	/**
	 * Název
	 */
	private String name;

	/**
	 * Typ
	 */
	private DrinkType type;

	/**
	 * Hodnocení
	 */
	private Double rating;

	/**
	 * Obrázek
	 */
	@Lob
	private byte[] image;

	/**
	 * Text
	 */
	@Column(columnDefinition = "TEXT")
	private String description;

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

	public DrinkType getType() {
		return type;
	}

	public void setTyp(DrinkType type) {
		this.type = type;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setType(DrinkType type) {
		this.type = type;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Drink))
			return false;
		return ((Drink) obj).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
