package entities;

public class SaveGame implements java.io.Serializable {
	private static final long serialVersionUID = 5337482461381953681L;
	private Long id;
	private String gameName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String nom) {
		this.gameName = nom;
	}

	public String getName() {
		return this.gameName;
	}
}
