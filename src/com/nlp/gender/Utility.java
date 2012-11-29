package com.nlp.gender;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;
import weka.core.tokenizers.NGramTokenizer;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordTokenFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;

public class Utility {

	private final static String pattern = "[^\\s]*_";
	private final static POSTaggerME tagger = new POSTaggerME(
			new POSModelLoader().load(new File("en-pos-maxent.bin")));
	private final static LexicalizedParser parser = new LexicalizedParser(
			"/home/ajinkya/Downloads/NLP/Project/stanford-parser-2010-11-30/englishPCFG.ser.gz");

	public static void createDirectory(String dir) {
		(new File(dir)).mkdirs();
	}

	public static boolean checkAnddeleteDir(String dirPath) {
		File input = new File(dirPath);
		if (input.exists()) {
			return deleteDir(input);
		}
		return false;
	}

	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	public static Instances convertToArff(String dirPath) throws Exception {
		NGramTokenizer ngt = new NGramTokenizer();

		TextDirectoryLoader tdl = new TextDirectoryLoader();

		tdl.setDirectory(new File(dirPath));

		return tdl.getDataSet();
	}

	public static void prepareInputData(String classfierInputDir,
			int numOfFolds, String pathMale, String pathFemale)
			throws Exception {

		Utility.checkAnddeleteDir(classfierInputDir);
		String[] classes = { "male", "female" };

		for (int i = 1; i <= numOfFolds; i++) {
			String trainDir = classfierInputDir + File.separator + i
					+ File.separator + "Train";

			String testDir = classfierInputDir + File.separator + i
					+ File.separator + "Test";
			Utility.createDirectory(testDir);
			String srcFilePath = null;
			for (int j = 0; j < classes.length; j++) {
				if (classes[j].equals("male")) {
					srcFilePath = pathMale;
				} else {
					srcFilePath = pathFemale;
				}
				DataPreprocessor.split(srcFilePath, trainDir + File.separator
						+ classes[j], testDir + File.separator + classes[j],
						80 * (i - 1), 80);
			}
		}

	}

	public static String getPOSTaggedLine(String strLine) {
		String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
				.tokenize(strLine);
		String[] tags = tagger.tag(whitespaceTokenizerLine);

		POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
		String taggedString = sample.toString();
		String finalString = taggedString.replaceAll(pattern, "");
		return finalString;
	}

	// For POS sequence or Ngram sequence we need to set minsize to 1
	public static NGramTokenizer getNGramTokenizer(int nGram) {

		NGramTokenizer ngTokenizer = new NGramTokenizer();
		ngTokenizer.setNGramMaxSize(nGram);
		ngTokenizer.setNGramMinSize(nGram);
		return ngTokenizer;

	}

	public static String getPCFGParsedLine(String str) {
		TokenizerFactory tf = PTBTokenizer.factory(false,
				new WordTokenFactory());
		TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
	
		List tokens = tf.getTokenizer(new StringReader(str)).tokenize();
		List tokenList = tokens.size() > 120 ? tokens.subList(0, 120) : tokens;
		try {
			System.out.println("Tokensize: " + tokens.size());
			parser.parse(tokenList); // parse the tokens
		} catch (Exception e) {

		}

		Tree tree = parser.getBestParse(); // get the best parse tree
		tree.pennPrint();
		System.out.println("Tree " + tree.toString() + "\n\n");

		List<String> list = new ArrayList<String>();
		getProductionRules(tree.getChildrenAsList().get(0), 0, list);
		String str2 = "";
		for (String rule : list) {
			str2 += rule + " ";
		}
		return str2;
	}

	private static void getProductionRules(Tree tree, int level,
			List<String> list) {

		if (level < 2 && !tree.isLeaf()) {
			String str = tree.value() + "-->";
			String newStr = str;
			for (Tree child : tree.getChildrenAsList()) {
				newStr += "" + child.value();
			}
			if (!str.equals(newStr)) {
				System.out.println("\nLevel :" + level);
				for (String s : list) {
					System.out.println(s + "");
				}
				list.add(newStr);
			}
			for (Tree child : tree.getChildrenAsList()) {
				getProductionRules(child, level + 1, list);
			}
		} else
			return;
	}
}
