package com.example.crimexplorer;

import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.io.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;


@RestController
public class MyController {

    @Value("${fuseki.server.url}")
    private String fusekiServerUrl;

    @Value("${fuseki.server.username}")
    private String fusekiUsername;

    @Value("${fuseki.server.password}")
    private String fusekiPassword;

    @GetMapping("/getCrimes/{state}")
    public String getCrimesByState(@PathVariable String state) {
        String sparqlQuery = buildSparqlQuery(state);
        return executeSparqlQuery(sparqlQuery);
    }

    private String buildSparqlQuery(String state) {
        // SPARQL query with the state variable incorporated
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX ex: <http://www.semanticweb.org/karthik003/ontologies/2023/10/Project_Team_8#> SELECT (REPLACE(str(?crimeId), \"^http://www.semanticweb.org/karthik003/ontologies/2023/10/Project_Team_8#\", \"\") AS ?crimeIdStripped) (REPLACE(str(?county), \"^http://www.semanticweb.org/karthik003/ontologies/2023/10/Project_Team_8#\", \"\") AS ?countyStripped) (REPLACE(str(?state), \"^http://www.semanticweb.org/karthik003/ontologies/2023/10/Project_Team_8#\", \"\") AS ?stateStripped) (REPLACE(str(?latitude), \"^http://www.semanticweb.org/karthik003/ontologies/2023/10/Project_Team_8#\", \"\") AS ?latitudeStripped) (REPLACE(str(?longitude), \"^http://www.semanticweb.org/karthik003/ontologies/2023/10/Project_Team_8#\", \"\") AS ?longitudeStripped) ?date (REPLACE(str(?type), \"^http://www.semanticweb.org/karthik003/ontologies/2023/10/Project_Team_8#\", \"\") AS ?typeStripped) ?weapon WHERE { ?crimeId rdf:type owl:NamedIndividual. ?crimeId ex:hasCounty ?county . ?crimeId ex:hasState ?state . ?crimeId ex:hasLatitude ?latitude . ?crimeId ex:hasLongitude ?longitude . ?crimeId ex:hasDate ?date . ?crimeId ex:hasType ?type . ?crimeId ex:hasWeaponInvolved ?weapon . FILTER (regex(str(?state), \"NewYork\")) } LIMIT 100";

        return query;
    }

    private String executeSparqlQuery(String query) {
        try (RDFConnection conn = RDFConnectionFactory.connect(fusekiServerUrl, fusekiUsername, fusekiPassword)) {
            try (QueryExecution qExec = conn.query(query)) {
                ResultSet results = qExec.execSelect();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ResultSetFormatter.outputAsJSON(outputStream, results);

                return outputStream.toString("UTF-8");
            }
        } catch (Exception e) {
            return "Error executing SPARQL query: " + e.getMessage();
        }
    }

}
