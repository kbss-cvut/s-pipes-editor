package og_spipes.persistence.dao;

import og_spipes.utils.FileUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class OntologyDao {

    protected static Map<File, CachedStatements> statementCache = new HashMap<>();
    protected static CachedStatements getCachedStatements(File file){
        File nf = FileUtils.normalizedFile(file);
        return statementCache.computeIfAbsent(nf, f -> new CachedStatements(f));
    }
    public static String getOntologyUri(File f) {
        return getCachedStatements(f).getOntologyIRI();
    }

    public static List<String> getOntologyImports(File f) {
        return getCachedStatements(f).getImportedIRIs();
    }

    public static Set<String> getSubjects(File f) {
        return getCachedStatements(f).getSubjects();
    }


    static class CachedStatements extends FileUtils.FileCache{
        protected String ontologyIRI;
        protected List<String> importedIRIs;
        protected Set<String> subjects;

        public CachedStatements(File file) {
            super(file);
        }

        public File getFile() {
            return file;
        }


        public String getOntologyIRI() {
            evaluateIfOutdated();
            return ontologyIRI;
        }

        public List<String> getImportedIRIs() {
            evaluateIfOutdated();
            return importedIRIs;
        }

        public Set<String> getSubjects() {
            evaluateIfOutdated();
            return subjects;
        }

        @Override
        public void evaluateImpl(){
            lastCalculated = System.currentTimeMillis();
            OntologyIRIAndImportsStreamer streamer = new OntologyIRIAndImportsStreamer();
            RDFParser.source(file.getPath()).parse(streamer);
            ontologyIRI = streamer.getOntologyIris().stream().findFirst().orElse("");
            importedIRIs = streamer.getImports().values().stream().flatMap(l -> l.stream())
                    .distinct().collect(Collectors.toList());
            subjects = streamer.getSubjects();
        }
    }

    public static class OntologyIRIAndImportsStreamer extends StreamRDFBase {

        protected List<String> ontologyIris = new ArrayList<>();
        protected Set<String> subjects = new HashSet<>();
        protected Map<String, List<String>> imports = new HashMap<>();

        public List<String> getOntologyIris() {
            return ontologyIris;
        }

        public Map<String, List<String>> getImports() {
            return imports;
        }

        public Set<String> getSubjects() {
            return subjects;
        }

        protected List<StatementSelector> selectors = Arrays.asList(
                new StatementSelector(Node.ANY, RDF.type.asNode(), OWL.Ontology.asNode()) {
                    @Override
                    public void accept(Triple t) {
                        toResourceString(t.getSubject()).ifPresent(ontologyIris::add);
                    }

                    @Override
                    public void accept(Quad q) {
                        toResourceString(q.getSubject()).ifPresent(ontologyIris::add);
                    }
                },

                new StatementSelector(Node.ANY, OWL.imports.asNode(), Node.ANY) {
                    @Override
                    public void accept(Triple t) {
                        accept(t.getSubject(), t.getObject());
                    }

                    @Override
                    public void accept(Quad q) {
                        accept(q.getSubject(), q.getObject());
                    }

                    protected void accept(Node sn, Node on){
                        Optional<String> subj = toResourceString(sn);
                        Optional<String> obj = toResourceString(on);
                        if(subj.isPresent())
                            ontologyIris.add(subj.get());

                        if(subj.isPresent() && obj.isPresent())
                            imports.computeIfAbsent(subj.get(), s -> new ArrayList<>())
                                    .add(obj.get());
                    }
                }
        );

        protected Optional<String> toResourceString(Node n){
            return Optional.ofNullable(
                    n.isURI()
                            ? n.getURI()
                            : n.isBlank()
                                ? n.getBlankNodeLabel()
                                : null
            );
        }

        @Override
        public void quad(Quad quad) {
            if(quad.getSubject()!=null && quad.getSubject().isURI())
                subjects.add(quad.getSubject().getURI());

            for(StatementSelector s : selectors){
                if(s.check(quad))
                    break;
            }
        }

        @Override
        public void triple(Triple triple) {
            if(triple.getSubject()!=null && triple.getSubject().isURI())
                subjects.add(triple.getSubject().getURI());

            for(StatementSelector s : selectors){
                if(s.check(triple))
                    break;
            }
        }
    }

    public abstract static class StatementSelector{
        protected Triple tt;
        protected Quad qt;
        public StatementSelector(Node subject, Node predicate, Node object) {
            this(Node.ANY, subject, predicate, object);
        }

        public StatementSelector(Node graph, Node subject, Node predicate, Node object) {
            qt = new Quad(
                    Optional.ofNullable(graph).orElse(Node.ANY),
                    Optional.ofNullable(subject).orElse(Node.ANY),
                    Optional.ofNullable(predicate).orElse(Node.ANY),
                    Optional.ofNullable(object).orElse(Node.ANY)
            );

            tt = qt.asTriple();
        }

        public boolean check(Triple triple){
            if(tt.matches(triple)) {
                accept(triple);
                return true;
            }
            return false;
        }
        public boolean check(Quad q){
            if (qt.matches(q.getGraph(), q.getSubject(), q.getPredicate(), q.getObject())) {
                accept(q);
                return true;
            }
            return false;
        }

        public abstract void accept(Triple t);
        public abstract void accept(Quad q);
    }


}
