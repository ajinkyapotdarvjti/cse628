package com.nlp.gender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class DataPreprocessor {

	private static final String BLOG_DELIMITER = "**********************************";

	public static void split(String srcPath, String destDirTrainPath,
			String destDirTestPath, int testStart, int size)
			throws IOException {

		Utility.createDirectory(destDirTrainPath);
		Utility.createDirectory(destDirTestPath);

		FileInputStream fstream = new FileInputStream(srcPath);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int fileIndex = 0;

		String fileName = null;
		String str = "";

		while ((strLine = br.readLine()) != null) {
			if (fileIndex >= testStart && fileIndex < testStart + size) {
				fileName = destDirTestPath + File.separator + fileIndex;
			} else {
				fileName = destDirTrainPath + File.separator + fileIndex;
			}

			if (strLine.equals(BLOG_DELIMITER)) {
				FileWriter fstreamW = new FileWriter(fileName);
				BufferedWriter out = new BufferedWriter(fstreamW);
				out.write(str);
				out.close();

				fileIndex++;
				str = "";
				continue;
			}

			if (destDirTestPath.contains(GenderDetector.POS_INPUT_DIR)) {
				strLine = Utility.getPOSTaggedLine(strLine);
			}
			if (destDirTestPath.contains(GenderDetector.PCFG_INPUT_DIR)) {
				strLine = Utility.getPCFGParsedLine(strLine);
			}
			str += strLine;
		}

		br.close();
		in.close();
	}
}
