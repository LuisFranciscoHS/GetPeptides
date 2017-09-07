package no.uib.utils;

import sun.java2d.pipe.BufferedRenderPipe;

import java.io.*;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Luis Francisco Hernández Sánchez <luis.sanchez@uib.no>
 *
 *  This class takes all the fasta files corresponding to the human proteotypic peptides at GPMDB. Then gathers the content from each peptide file.
 *  The result is an output file with a single column called PTPs_GPMDB.csv, with one peptide each line.
 */

public class GPMDB_PTPListExtractor {
    public static void main(String args[]) {

        FileWriter output = null;
        Set<String> peptideSet = new TreeSet<>();
        try {
            output = new FileWriter("./resources/GPMDB/PTPs_GPMDB.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        File directory = new File("./resources/GPMDB");
        if (args.length > 0) {
            directory = new File(args[0]);
        }

        System.out.println("Reading repository files at: " + directory.getPath());

        //System.out.println("The contents are:");

        for (final File file : directory.listFiles()) {                         // Get each file in the directory
            if (file.getName().endsWith(".fasta")) {
                System.out.println(" --- " + file.getName() + " --- ");

                try {
                    String line;
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    while((line = bufferedReader.readLine()) != null){
                        if(!line.startsWith(">")){
                            peptideSet.add(line);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
