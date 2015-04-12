package org.vaadin.gridutil.demo.data;

import java.util.Date;

import com.google.gwt.i18n.server.testing.Gender;

public class Inhabitants {

	private long id;
	private Gender gender;
	private String name;
	private double bodySize;
	private Date birthday;
	private boolean onFacebook;
	private Country country;

	public Inhabitants() {

	}

	public Inhabitants(final long id, final Gender gender) {
		super();
		this.id = id;
		this.gender = gender;
	}

	public long getId() {
		return this.id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public Gender getGender() {
		return this.gender;
	}

	public void setGender(final Gender gender) {
		this.gender = gender;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public double getBodySize() {
		return this.bodySize;
	}

	public void setBodySize(final double bodySize) {
		this.bodySize = bodySize;
	}

	public Date getBirthday() {
		return this.birthday;
	}

	public void setBirthday(final Date birthday) {
		this.birthday = birthday;
	}

	public boolean isOnFacebook() {
		return this.onFacebook;
	}

	public void setOnFacebook(final boolean onFacebook) {
		this.onFacebook = onFacebook;
	}

	public Country getCountry() {
		return this.country;
	}

	public void setCountry(final Country country) {
		this.country = country;
	}

}
