package no.uib.utils;

import com.martiansoftware.jsap.*;
import no.uib.PeptideToProteinMapper;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class ListCompare {
    public static List<String> getDiff(List<String> list1, List<String> list2) {
        list1.removeAll(list2);
        return list1;
    }

    public static void main(String args[]) throws JSAPException {
        // Parse Program Arguments
        SimpleJSAP jsap = new SimpleJSAP(PeptideToProteinMapper.class.getName(), "Maps a list of peptides to UniProt accessions and filters to only proteotypic peptides and proteins in Reactome.",
                new Parameter[]{
                        new FlaggedOption("list1", JSAP.STRING_PARSER, "./resources/GPMDB_PTPs_simple.csv", JSAP.REQUIRED, 'o', "list1", "File with the first list"),
                        new FlaggedOption("list2", JSAP.STRING_PARSER, "./resources/PeptideAtlas_PTPs_simple.csv", JSAP.REQUIRED, 'n', "list2", "File with the second list")
                }
        );

        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) {
            System.exit(1);
        }

        try {
            FileWriter output = new FileWriter("./resources/intersection.csv");

            BufferedReader reader1 = new BufferedReader(new FileReader(config.getString("list1")));
            BufferedReader reader2 = new BufferedReader(new FileReader(config.getString("list2")));
            List<String> list1 = new ArrayList<>();
            List<String> list2 = new ArrayList<>();

            String line;

            while ((line = reader1.readLine()) != null) {
                list1.add(line);
            }
            while ((line = reader2.readLine()) != null) {
                list2.add(line);
            }

            Set<String> intersection = getIntersection(list1, list2);

            for (String str : intersection) {
                output.write(str + "\n");
            }

            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Set<String> getIntersection(Collection<String> list1, Collection<String> list2) {
        Set<String> set1 = new TreeSet<>();
        Set<String> result = new TreeSet<>();

        set1.addAll(list1);
        for (String str : list2) {
            if (set1.contains(str)) {
                result.add(str);
            }
        }

        return result;
    }
}
