package no.uib;

import com.martiansoftware.jsap.*;
import no.uib.utils.PeptideMapping;
import org.neo4j.driver.v1.*;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Luis Francisco Hernández Sánchez <luis.sanchez@uib.no>
 * <p>
 * This class takes as input a list of peptides and maps to proteins represented
 * by their UniProt accessions. Then it reads the list of human proteins
 * annotated in Reactome to filter the peptides. Then it filters the peptides to
 * be only proteotypic, mapping to only one protein.
 */
public class PeptideToProteinMapper {

    private static Set<String> reactomeProteinsSet;
    private static Map<String, Set<String>> mapping;
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(PeptideToProteinMapper.class);

    private enum Arguments {
        fasta,
        peptidelist,
        simple
    }

    public static void main(String args[]) throws JSAPException {

        // Parse Program Arguments
        SimpleJSAP jsap = new SimpleJSAP(PeptideToProteinMapper.class.getName(), "Maps a list of peptides to UniProt accessions and filters to only proteotypic peptides and proteins in Reactome.",
                new Parameter[]{
                        new FlaggedOption(Arguments.fasta.toString(), JSAP.STRING_PARSER, "./resources/uniprot-all.fasta", JSAP.REQUIRED, 'f', Arguments.fasta.toString(), "Contains the reference protein sequences"),
                        new FlaggedOption(Arguments.peptidelist.toString(), JSAP.STRING_PARSER, "./resources/PTPs_GPMDB.csv", JSAP.REQUIRED, 'l', Arguments.peptidelist.toString(), "Peptide list"),
                        new QualifiedSwitch(Arguments.simple.toString(), JSAP.BOOLEAN_PARSER, null, JSAP.NOT_REQUIRED, 's', Arguments.simple.toString(), "Simple Peptide list")
                }
        );

        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) {
            System.exit(1);
        }

        // Get list of proteins annotated in Reactome
        System.out.println("Getting proteins annotated in Reactome...");
        reactomeProteinsSet = new TreeSet<String>();
        Driver driver = GraphDatabase.driver("bolt://127.0.0.1:7687", AuthTokens.basic("neo4j", "neo4j2"));

        try {
            Session session = driver.session();

            String query = "MATCH (re:ReferenceEntity)<-[:referenceEntity]-(ewas:EntityWithAccessionedSequence)\n"
                    + "WHERE re.databaseName = 'UniProt' AND ewas.speciesName = \"Homo sapiens\"\n"
                    + "RETURN DISTINCT re.identifier as UniProtAccession\n"
                    + "ORDER BY UniProtAccession";
            StatementResult queryResult = session.run(query, Values.parameters());

            while (queryResult.hasNext()) {
                Record record = queryResult.next();
                //System.out.println(record.get("UniProtAccession").asString());
                reactomeProteinsSet.add(record.get("UniProtAccession").asString());
            }

            System.out.println("Found " + reactomeProteinsSet.size() + " proteins.");

            session.close();
        } catch (org.neo4j.driver.v1.exceptions.ClientException e) {
            System.out.println(e.toString());
            System.out.println(" Unable to connect to \"" + "bolt://127.0.0.1:7687" + "\", ensure the database is running and that there is a working network connection to it.");
            System.exit(1);
        }

        System.out.println("Initializing PeptideMapper...");
        PeptideMapping.initializePeptideMapper(config.getString(Arguments.fasta.toString()));

        System.out.println("Filtering peptide list...");
        mapping = new HashMap<>();
        try {
            FileWriter simple = null;
            FileWriter output = new FileWriter("./resources/PTPs.csv");
            if (config.getBoolean(Arguments.simple.toString())) {
                simple = new FileWriter("./resources/simple_PTPs.csv");
            }
            FileWriter stats = new FileWriter("./resources/Stats.txt");

            System.out.println("Reading peptide list from " + config.getString(Arguments.peptidelist.toString()));
            BufferedReader reader = new BufferedReader(new FileReader(config.getString(Arguments.peptidelist.toString())));
            String peptide;
            while ((peptide = reader.readLine()) != null) {

                ArrayList<String> mappedProtens = PeptideMapping.getPeptideMapping(peptide);
                if (mappedProtens.size() == 1) {
                    if (reactomeProteinsSet.contains(mappedProtens.get(0))) {
                        mapping.putIfAbsent(mappedProtens.get(0), new HashSet<String>());
                        mapping.get(mappedProtens.get(0)).add(peptide);
                    }
                }
            }

            stats.write("Proteins in Reactome: " + reactomeProteinsSet.size() + "\n");
            int proteinsCovered = 0;
            int peptidesFound = 0;
            for (String protein : reactomeProteinsSet) {
                if (mapping.containsKey(protein)) {
                    proteinsCovered++;
                    output.write(protein + "\t");
                    for (String p : mapping.get(protein)) {
                        output.write(p + "\t");
                        peptidesFound++;
                        if (config.getBoolean(Arguments.simple.toString())) {
                            simple.write(p + "\n");
                        }
                    }
                    output.write("\n");
                }
            }

            stats.write("Proteins covered: " + proteinsCovered + "\n");
            stats.write("Proteotypic peptides found: " + peptidesFound);


            simple.close();
            stats.close();
            reader.close();
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(PeptideToProteinMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
