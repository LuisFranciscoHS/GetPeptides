
package no.uib;

import com.martiansoftware.jsap.*;
import org.reactome.server.graph.domain.model.*;
import org.reactome.server.graph.service.DatabaseObjectService;
import org.reactome.server.graph.service.GeneralService;
import org.reactome.server.graph.service.SchemaService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import no.uib.ReactomeNeo4jConfig;

import java.util.*;

public class Main {

    public static void main(String[] args) throws JSAPException {

        // Program Arguments -h, -p, -u, -k
        SimpleJSAP jsap = new SimpleJSAP(Main.class.getName(), "Connect to Reactome Graph Database",
                new Parameter[]{
                        new FlaggedOption("host",     JSAP.STRING_PARSER, "localhost", JSAP.NOT_REQUIRED, 'h', "host",     "The neo4j host"),
                        new FlaggedOption("port",     JSAP.STRING_PARSER, "7474",      JSAP.NOT_REQUIRED, 'p', "port",     "The neo4j port"),
                        new FlaggedOption("user",     JSAP.STRING_PARSER, "neo4j",     JSAP.NOT_REQUIRED, 'u', "user",     "The neo4j user"),
                        new FlaggedOption("password", JSAP.STRING_PARSER, "neo4j2",     JSAP.REQUIRED,     'k', "password", "The neo4j password")
                }
        );

        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);

        //Initialising ReactomeCore Neo4j configuration
        ReactomeGraphCore.initialise(config.getString("host"),
                                     config.getString("port"),
                                     config.getString("user"),
                                     config.getString("password"),
                                     ReactomeNeo4jConfig.class);

        // Get the list of protein identifiers from Reactome
        Set<String> proteinSet = new TreeSet<String>();
        GeneralService genericService = ReactomeGraphCore.getService(GeneralService.class);

        String query = "MATCH (re:ReferenceEntity)<-[:referenceEntity]-(ewas:EntityWithAccessionedSequence)\n"
                + "WHERE re.databaseName = 'UniProt' AND ewas.speciesName = \"Homo sapiens\"\n"
                + "RETURN DISTINCT re.identifier as UniProtAccession\n"
                + "ORDER BY UniProtAccession";
        for(Map<String, Object> entry : genericService.query(query, new HashMap<>())){
            proteinSet.add(entry.get("UniProtAccession").toString());
        }
    }
}