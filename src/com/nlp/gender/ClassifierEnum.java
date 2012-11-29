package com.nlp.gender;

public enum ClassifierEnum {
	NAIVE_BAYES("NAIVE_BAYES");
	/**
	 * @param text
	 */
	private ClassifierEnum(final String text) {
		this.text = text;
	}

	private final String text;

	@Override
	public String toString() {
		return text;
	}
}
