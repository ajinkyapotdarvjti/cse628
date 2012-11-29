package com.nlp.gender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class PrepareDataForNestedCV {

	public static void split(String srcPath, String destDirTrainPath,
			String destDirTestPath, int testStart, int size,
			boolean doPostTagging) throws IOException {
		(new File(destDirTrainPath)).mkdirs();
		(new File(destDirTestPath)).mkdirs();
		FileInputStream fstream = new FileInputStream(srcPath);
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int fileIndex = 0;

		String fileName = null;
		while ((strLine = br.readLine()) != null) {
			// Print the content on the console

			if (fileIndex >= testStart && fileIndex < testStart + size) {
				fileName = destDirTestPath + File.separator + fileIndex;
			} else {
				fileName = destDirTrainPath + File.separator + fileIndex;
			}
			if (doPostTagging) {
				strLine = Utility.getPOSTaggedLine(strLine);
			}
			FileWriter fstreamW = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstreamW);
			// System.out.println(strLine);
			out.write(strLine);
			// Close the output stream
			out.close();
			fileIndex++;
		}

		br.close();
		in.close();

	}
}
