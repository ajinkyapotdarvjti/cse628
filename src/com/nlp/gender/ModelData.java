package com.nlp.gender;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.filters.Filter;

public class ModelData {
	int correctVal;
	Classifier clsfr;
	Filter filter;
	Attribute classAttribute;
	Evaluation evaluation;

	public Attribute getClassAttribute() {
		return classAttribute;
	}

	public void setClassAttribute(Attribute classAttribute) {
		this.classAttribute = classAttribute;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public ModelData(Classifier clsfr, double correct, Filter stwv,
			Attribute attribute, Evaluation m_Evlution) {
		correctVal = (int) correct;
		this.clsfr = clsfr;
		this.filter = stwv;
		this.classAttribute = attribute;
		this.evaluation = m_Evlution;

	}

	public Evaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}

	public int getCorrectVal() {
		return correctVal;
	}

	public void setCorrectVal(int correctVal) {
		this.correctVal = correctVal;
	}

	public Classifier getClsfr() {
		return clsfr;
	}

	public void setClsfr(Classifier clsfr) {
		this.clsfr = clsfr;
	}

}