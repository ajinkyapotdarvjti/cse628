package com.nlp.gender;

import java.io.File;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class GenderDetector {

	public static final String PARENT_DIR = "data";
	public static final String PATH_MALE = PARENT_DIR + File.separator
			+ "male.txt";

	private static final String PATH_FEMALE = PARENT_DIR + File.separator
			+ "female.txt";
	public static final String POS_INPUT_DIR = PARENT_DIR + "POS_DATA";
	private static final String CLASSIFIER_INPUT_DIR = PARENT_DIR + "TC_DATA";
	public static final String FSTYLISTIC_INPUT_DIR = PARENT_DIR
			+ "FSTYLISTIC_DATA";
	public static final String PCFG_INPUT_DIR = PARENT_DIR + "PCFG_DATA";

	private static final int NUM_NGRAM = 3;
	private static final int NO_OF_FOLDS = 5;

	public static void main(String[] args) throws Exception {

		GenderDetector d = new GenderDetector();
		Utility.prepareInputData(CLASSIFIER_INPUT_DIR, NO_OF_FOLDS, PATH_MALE,
				PATH_FEMALE);
		Utility.prepareInputData(POS_INPUT_DIR, NO_OF_FOLDS, PATH_MALE,
				PATH_FEMALE);
		Utility.prepareInputData(FSTYLISTIC_INPUT_DIR, NO_OF_FOLDS, PATH_MALE,
				PATH_FEMALE);
		Utility.prepareInputData(PCFG_INPUT_DIR, NO_OF_FOLDS, PATH_MALE,
				PATH_FEMALE);

		System.out.println("Start nested Cross Validation for POS.......\n");
		d.run5FoldNestedCrossValidation(POS_INPUT_DIR);
		System.out
				.println("Start nested Cross Validation Text Catagorisation.....\n");
		d.run5FoldNestedCrossValidation(CLASSIFIER_INPUT_DIR);

		System.out
				.println("Start nested Cross Validation Fstyle measure.....\n");
		d.run5FoldNestedCrossValidation(FSTYLISTIC_INPUT_DIR);

		System.out.println("Start nested Cross Validation PCFG measure.....\n");
		d.run5FoldNestedCrossValidation(PCFG_INPUT_DIR);
	}

	public ModelData createModel(String trainDir, String testDir, int nGram,
			Classifier clsfr) throws Exception {

		System.out.println("Creating model.." + nGram);
		// Create Tokenizer Object for NGram.
		NGramTokenizer ngTokenizer = Utility.getNGramTokenizer(nGram);

		// Load the instances for training data
		TextDirectoryLoader tdl = new TextDirectoryLoader();
		tdl.setDirectory(new File(trainDir));
		Instances instances = tdl.getDataSet();

		// create StringToWordVector object and set TFIDF and other parameters
		StringToWordVector stwv = new StringToWordVector();
		stwv.setTokenizer(ngTokenizer);
		stwv.setTFTransform(true);
		stwv.setIDFTransform(true);
		stwv.setLowerCaseTokens(true);
		stwv.setInputFormat(instances);
		stwv.setMinTermFreq(10);
		stwv.setStopwords(new File("stopword.txt"));

		// apply filter for instances.
		Instances filterdInstances = Filter.useFilter(instances, stwv);

		TextDirectoryLoader tdltest = new TextDirectoryLoader();
		tdltest.setDirectory(new File(testDir));

		Instances instancesTest = tdltest.getDataSet();
		Instances filterdInstancesTest = Filter.useFilter(instancesTest, stwv);

		clsfr.buildClassifier(filterdInstances);

		System.out.println("After building classifier ");
		Evaluation m_Evlution = new Evaluation(filterdInstances);
		m_Evlution.evaluateModel(clsfr, filterdInstancesTest);

		return new ModelData(clsfr, m_Evlution.correct(), stwv,
				filterdInstances.classAttribute(), m_Evlution);
	}

	private void run5FoldNestedCrossValidation(String dirPath) throws Exception {

		for (ClassifierEnum classifier : ClassifierEnum.values()) {

			for (int k = 1; k <= NUM_NGRAM; k++) {

				ModelData bestModel = null;
				for (int i = 1; i <= NO_OF_FOLDS; i++) {
					String trainDir = dirPath + File.separator + i
							+ File.separator + "Train";
					String testDir = dirPath + File.separator + i
							+ File.separator + "Test";

					ModelData curModel = new GenderDetector().createModel(
							trainDir, testDir, k, getClassifier(classifier));
					if (bestModel == null
							|| curModel.getCorrectVal() > bestModel
									.getCorrectVal()) {
						bestModel = curModel;

					}

				}
				System.out.println("NGram: " + k);
				printSummary(bestModel);
			}
		}
	}

	private void printSummary(ModelData bestModel) throws Exception {

		Evaluation eval = bestModel.getEvaluation();
		System.out.println("Summary ");
		System.out.println(eval.toSummaryString());
		System.out.println(eval.toMatrixString());
		System.out.println(eval.toClassDetailsString());
	}

	private Classifier getClassifier(ClassifierEnum classifier) {
		if (classifier.equals(ClassifierEnum.NAIVE_BAYES)) {
			NaiveBayes clsfr = new NaiveBayes();
			clsfr.setUseKernelEstimator(true);
			return clsfr;
		}

		return null;
	}
}
