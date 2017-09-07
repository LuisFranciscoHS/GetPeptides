package no.uib.utils;

import java.io.*;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Luis Francisco Hernández Sánchez <luis.sanchez@uib.no>
 * <p>
 * This class traverses the file with all the human peptides annotated in a build of PeptideAtlas and extract only the peptide sequence depending on a proteotypic score specified as argument of the program.
 * The result is an output file with a single column called PTPs_PeptideAtlas.csv, with one peptide each line.
 */

public class PeptideAtlas_PTPListExtractor {
    public static void main(String args[]) {

        FileWriter output = null;
        File file = new File("./resources/PeptideAtlas/AllPeptides");
        Set<String> peptideSet = new TreeSet<>();
        try {
            output = new FileWriter("./resources/PeptideAtlas/PTPs_PeptideAtlas.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Double proteotypicScoreThreshold = 0.0;
        if (args.length > 0) {
            proteotypicScoreThreshold = Double.valueOf(args[0]);
        }

        System.out.println("Reading file: " + file.getPath());

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = bufferedReader.readLine();    //Read the first line of the file with the column headers

            int num;
            int col = 0;
            StringBuilder peptide = new StringBuilder();
            StringBuilder proteotypicScore = new StringBuilder();

            while ((num = bufferedReader.read()) != -1) {
                char c = (char) num;
                if (c == ',') {
                    col++;          //Go to the next column
                } else if (c == '\n') {
                    col = 0;        // Go back to the beginning

                    if (Double.valueOf(proteotypicScore.toString()) >= proteotypicScoreThreshold) {
                        peptideSet.add(peptide.toString());
                        System.out.println(peptide.toString() + "\t" + proteotypicScore.toString());
                    }

                    peptide = new StringBuilder();              //Remove the contents of the previous column field
                    proteotypicScore = new StringBuilder();
                } else {
                    if (col == 1) {
                        peptide.append(c);
                    } else if (col == 4) {
                        proteotypicScore.append(c);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (String peptide : peptideSet) {
            try {
                output.write(peptide + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
