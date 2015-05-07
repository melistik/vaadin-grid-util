package org.vaadin.gridutil.demo.data;

public class Country {

	public enum Continent {
		AS("Asia"), EU("Europe"), AF("Africa"), OC("Oceania"), NA("North america"), SA("South america"), AN("Antarctica");

		private String display;

		Continent(final String display) {
			this.display = display;
		}

		public String getDisplay() {
			return this.display;
		}
	}

	private String isoCode;
	private String name;
	private Continent continent;
	private long population;

	public Country(final String isoCode, final String name, final Continent continent, final long population) {
		super();
		this.isoCode = isoCode;
		this.name = name;
		this.continent = continent;
		this.population = population;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.isoCode == null) ? 0 : this.isoCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Country other = (Country) obj;
		if (this.isoCode == null) {
			if (other.isoCode != null) {
				return false;
			}
		} else if (!this.isoCode.equals(other.isoCode)) {
			return false;
		}
		return true;
	}

	public String getIsoCode() {
		return this.isoCode;
	}

	public void setIsoCode(final String isoCode) {
		this.isoCode = isoCode;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Continent getContinent() {
		return this.continent;
	}

	public void setContinent(final Continent continent) {
		this.continent = continent;
	}

	public long getPopulation() {
		return this.population;
	}

	public void setPopulation(final long population) {
		this.population = population;
	}

}
