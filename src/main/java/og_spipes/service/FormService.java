package og_spipes.service;

import cz.cvut.sforms.VocabularyJena;
import cz.cvut.sforms.model.Answer;
import cz.cvut.sforms.model.Question;
import cz.cvut.sforms.util.FormUtils;
import cz.cvut.spipes.transform.AnonNodeTransformer;
import cz.cvut.spipes.transform.Transformer;
import cz.cvut.spipes.transform.TransformerImpl;
import og_spipes.model.Vocabulary;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.listeners.ChangedListener;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.util.FileUtils;
import org.apache.jena.util.ResourceUtils;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.topbraid.spin.arq.ARQ2SPIN;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static cz.cvut.spipes.transform.SPipesUtil.isSPipesTerm;

@Service
public class FormService {

    private static final Logger LOG = LoggerFactory.getLogger(FormService.class);

    private final OntologyHelper helper;

    private final Transformer transformer = new TransformerImpl();
//    private final Transformer transformer = new MujTransformer();

    @Autowired
    public FormService(OntologyHelper helper) {
        this.helper = helper;
    }

    public Question generateModuleForm(String scriptPath, String moduleUri, String moduleTypeUri){
        System.out.println("Generating form for script " + scriptPath + ", module " + moduleUri + ", moduleType " + moduleTypeUri);
        LOG.info("Generating form for script " + scriptPath + ", module " + moduleUri + ", moduleType " + moduleTypeUri);
        OntModel ontModel = helper.createOntModel(new File(scriptPath));
        Optional<Statement> moduleType = ontModel.listStatements(
                ontModel.getResource(moduleUri),
                RDF.type,
                ""
        ).filterDrop(
                x -> x.getObject().asResource().getURI().equals(Vocabulary.s_c_Modules)
        ).nextOptional();
        return transformer.script2Form(
                ontModel.getResource(moduleUri),
                moduleType.map(x -> x.getObject().asResource()).orElse(ontModel.getResource(moduleTypeUri))
      );
    }

    public void mergeFrom(String scriptPath, Question rootQuestion, String moduleType) {
        OntModel ontModel = helper.createOntModel(new File(scriptPath));
        ChangedListener changedListener = new ChangedListener();
        ontModel.register(changedListener);
        Map<String, Model> modelMap = transformer.form2Script(ontModel, rootQuestion, moduleType);
        System.out.println("changed: " + changedListener.hasChanged());
        modelMap.forEach((file, model) -> {
            try {
                System.out.println("file: " + file);
                FileOutputStream os = new FileOutputStream(scriptPath);
                model.write(os, FileUtils.langTurtle);
                System.out.println("changed: " + changedListener.hasChanged());
            } catch (FileNotFoundException e) {
                LOG.error(e.getMessage());
            }
        });
    }

}
//
//class MujTransformer implements Transformer{
//
////    Lister l = null;
////
////    void register(Lister lister){
////        l = lister;
////    }
////
////    @Override
////    public Question script2Form(Resource module, Resource moduleType) {
////        return null;
////    }
////
////    @Override
////    public Model form2Script(MujModel inputScript, Question form, String moduleType){
////        Model m = inputScript.copy();
////        if(l.exist()) {
////            m.register(l);
////        }
////
////    }
////
////    //preferable way urcite vyzkouset!
////    @Override
////    public void form2Script(Model scripts, Question form, String moduleType) {
//////        if(l.exist()) {
//////            m.register(l);
//////        }
////
////    }
//
////TODO look at the implementation and try to modify do pristiho tydne!
//
//    @Override
//    public Question script2Form(Resource module, Resource moduleType) {
//        return null;
//    }
//
//    /**
//     * public Model form2Script(MujModel inputScript, Question form, String moduleType)
//     * public void form2Script(Model scripts, Question form, String moduleType)
//     */
//
//    public Map<String, Model> form2Script(Model inputScript, Question form, String moduleType) {
//        System.out.println("JSEM TADY");
//
//        Map<String, Model> changed = new HashMap<>();
//
//        Resource module = inputScript.getResource(form.getOrigin().toString());
//
//        Question uriQ = findUriQ(form);
//        URI newUri = new ArrayList<>(uriQ.getAnswers()).get(0).getCodeValue();
//
//        Optional<Model> ttlModel = getTTLModel(form);
//
//        boolean ttlChanged = form.getSubQuestions().stream()
//                .flatMap(q -> q.getSubQuestions().stream())
//                .filter(q -> q.getLayoutClass().stream().anyMatch(s -> s.equals("ttl")))
//                .findFirst()
//                .map(q -> q.getAnswers().stream().anyMatch(a -> !DigestUtils.sha1Hex(a.getTextValue()).equals(a.getHash())))
//                .orElse(false);
//
//        System.out.println(RDF.type + " " + OWL.Ontology);
//        ((OntModel) inputScript).getBaseModel().listStatements().forEachRemaining(x -> {
//            System.out.println(x);
//        });
//
//        if (module.listProperties().hasNext()) {
//            Map<TransformerImpl.OriginPair<URI, URI>, Statement> questionStatements = getOrigin2StatementMap(module); // Created answer origin is different from the actual one
//            findRegularQ(form).forEach((q) -> {
//                System.out.println("questione: " + q + ", properties: " + q.getProperties());
//                TransformerImpl.OriginPair<URI, URI> originPair = new TransformerImpl.OriginPair<>(q.getOrigin(), getAnswer(q).map(Answer::getOrigin).orElse(null));
//                Statement s = questionStatements.get(originPair);
//                if (s != null) {
//                    final Model m = extractModel(s);
//
//                    final String uri = ((OntModel) inputScript).getBaseModel().listStatements(null, RDF.type, OWL.Ontology).next().getSubject().getURI();
//                    System.out.println("URI DURI: " + uri);
//                    if (!changed.containsKey(uri)) {
//                        changed.put(uri, ModelFactory.createDefaultModel().add(m instanceof OntModel ? ((OntModel) m).getBaseModel() : m));
//                    }
//                    final Model changingModel = changed.get(uri);
//
//                    changingModel.remove(s);
//                    if (isSupportedAnon(q)) {
//                        if (q.getAnswers().stream()
//                                .anyMatch(a -> !DigestUtils.sha1Hex(a.getTextValue()).equals(a.getHash()) && !DigestUtils.sha1Hex(a.getCodeValue().toString()).equals(a.getHash()))) {
//                            throw new ConcurrentModificationException("TTL and form can not be edited at the same time");
//                        }
//                        Query query = AnonNodeTransformer.parse(q, inputScript);
//                        org.topbraid.spin.model.Query spinQuery = ARQ2SPIN.parseQuery(query.serialize(), inputScript);
//                        changingModel.add(spinQuery.getModel());
//                        changingModel.add(
//                                ResourceFactory.createResource(uri),
//                                ResourceFactory.createProperty(cz.cvut.sforms.Vocabulary.s_p_text),
//                                ResourceFactory.createStringLiteral(q.getAnswers().iterator().next().getTextValue().replaceAll("\\n", "\n"))
//                        );
//                    } else {
//                        if (q.getAnswers().stream()
//                                .anyMatch(a -> !DigestUtils.sha1Hex(a.getTextValue()).equals(a.getHash()) && (a.getCodeValue() == null || !DigestUtils.sha1Hex(a.getCodeValue().toString()).equals(a.getHash()))) &&
//                                ttlChanged) {
//                            throw new ConcurrentModificationException("TTL and form can not be edited at the same time");
//                        }
//                        RDFNode answerNode = getAnswerNode(getAnswer(q).orElse(null));
//                        if (answerNode != null) {
//                            changingModel.add(s.getSubject(), s.getPredicate(), answerNode);
//                        }
//                    }
//                }
//            });
//        } else {
//            System.out.println("nema next");
//            Model m = ModelFactory.createDefaultModel().add(inputScript);
//            m.add(m.getResource(newUri.toString()), RDF.type, m.getResource(moduleType));
//            m.add(m.getResource(newUri.toString()), RDF.type, m.getResource(cz.cvut.sforms.Vocabulary.s_c_Modules_A));
//            findRegularQ(form).forEach((q) -> {
//                RDFNode answerNode = getAnswerNode(getAnswer(q).orElse(null));
//                if (answerNode != null) {
//                    m.add(m.getResource(newUri.toString()), new PropertyImpl(q.getOrigin().toString()), answerNode);
//                }
//            });
//            changed.put(((OntModel) inputScript).getBaseModel().listStatements(null, RDF.type, OWL.Ontology).next().getSubject().getURI(), m);
//        }
//
//        if (ttlChanged) {
//            ttlModel.map(m ->
//                    changed.put(
//                            m.listStatements(null, RDF.type, OWL.Ontology).next().getSubject().getURI(),
//                            m
//                    )
//            );
//        }
//
//        ResourceUtils.renameResource(module, newUri.toString());
//
//        return changed;
//    }
//
//    @Override
//    public Question functionToForm(Model script, Resource function) {
//        return null;
//    }
//
//
//    private Question findUriQ(Question root) {
//        Optional<Question> uriQ =
//                FormUtils.flatten(root).stream()
//                        .filter(q -> q.getOrigin() != null)
//                        .filter((q) -> RDFS.Resource.getURI().equals(q.getOrigin().toString())).findFirst();
//        if (uriQ.isPresent())
//            return uriQ.get();
//        throw new IllegalArgumentException("Root question has no subquestion that maps to URI");
//    }
//
//    private Set<Question> findRegularQ(Question root) {
//        return FormUtils.flatten(root).stream()
//                .filter((q) -> q.getSubQuestions() == null || q.getSubQuestions().isEmpty())
//                .filter(q -> q.getOrigin() != null)
//                .filter((q) -> !RDFS.Resource.getURI().equals(q.getOrigin().toString()))
//                .collect(Collectors.toSet());
//    }
//
//    private Optional<Model> getTTLModel(Question root) {
//        Optional<Question> ttl = root.getSubQuestions().stream()
//                .filter(q -> q.getLayoutClass().contains("TTL"))
//                .map(q -> q.getSubQuestions().iterator().next())
//                .findFirst();
//        return ttl.map(q -> {
//            Model m = ModelFactory.createDefaultModel();
//            m.read(q.getAnswers().iterator().next().getTextValue());
//            return m;
//        });
//    }
//
//    private Optional<Answer> getAnswer(Question q) {
//        if (q.getAnswers() == null || q.getAnswers().isEmpty()) {
//            return Optional.empty();
//        }
//        return Optional.of(q.getAnswers().iterator().next());
//    }
//
//    private RDFNode getAnswerNode(Answer a) {
//        if (a == null) {
//            return null;
//        }
//        if (a.getCodeValue() != null) {
//            return ResourceFactory.createResource(a.getCodeValue().toString());
//        }
//        if (a.getTextValue() != null && !a.getTextValue().isEmpty()) {
//            return ResourceFactory.createStringLiteral(a.getTextValue());
//        }
//        return null;
//    }
//
//    private Answer getAnswer(RDFNode node) {
//        Answer a = new Answer();
//        if (node.isURIResource()) {
//            a.setCodeValue(URI.create(node.asResource().getURI()));
//            a.setHash(DigestUtils.sha1Hex(node.asResource().getURI()));
//        } else if (node.isLiteral()) {
//            a.setTextValue(node.asLiteral().getString());
//            a.setHash(DigestUtils.sha1Hex(node.asLiteral().getLexicalForm()));
//        } else if (node.isAnon()) {
//            a.setTextValue(AnonNodeTransformer.serialize(node));
//            a.setHash(DigestUtils.sha1Hex(AnonNodeTransformer.serialize(node)));
//        } else {
//            throw new IllegalArgumentException("RDFNode " + node + " should be a literal, a URI resource or an anonymous node of a known type");
//        }
//        return a;
//    }
//
//    private Map<TransformerImpl.OriginPair<URI, URI>, Statement> getOrigin2StatementMap(Resource module) {
//        return module.listProperties()
//                .filterDrop(st -> isSPipesTerm(st.getPredicate()))
//                .toList().stream()
//                .collect(Collectors.toMap(st -> new TransformerImpl.OriginPair<>(createQuestionOrigin(st), createAnswerOrigin(st)), st -> st));
//    }
//
//    private URI toUri(Resource resource) {
//        return URI.create(resource.toString());
//    }
//
//    private URI createQuestionOrigin(Statement statement) {
//        return URI.create(statement.getPredicate().toString());
//    }
//
//    private URI createAnswerOrigin(Statement statement) {
//        if (!statement.getObject().isAnon())
//            return URI.create(VocabularyJena.s_c_answer_origin.toString() +
//                    "/" + createMd5Hash(statement.getObject().toString()));
//        return URI.create(VocabularyJena.s_c_answer_origin.toString() +
//                "/" + createMd5Hash(AnonNodeTransformer.serialize(statement.getObject())));
//    }
//
//    private String createMd5Hash(String text) {
//        return DigestUtils.md5Hex(text);
//    }
//
//    private void initializeQuestionUri(Question q) {
//        q.setUri(URI.create(VocabularyJena.s_c_question + "-" + UUID.randomUUID().toString()));
//    }
//
//    private Question createQuestion(Resource resource) {
//        Question q = new Question();
//        initializeQuestionUri(q);
//        q.setLabel(resource.getURI());
//
//        StringBuilder descriptionBuilder = new StringBuilder();
//        StmtIterator labelIt = resource.listProperties(RDFS.label);
//        if (labelIt.hasNext()) {
//            descriptionBuilder.append(labelIt.nextStatement().getObject().asLiteral().getString());
//            descriptionBuilder.append("\n\n");
//        }
//        StmtIterator descriptionIt = resource.listProperties(RDFS.comment);
//        if (descriptionIt.hasNext())
//            descriptionBuilder.append(descriptionIt.nextStatement().getObject().asLiteral().getString());
//
//        q.setDescription(descriptionBuilder.toString());
//        return q;
//    }
//
//    private Map<String, Set<String>> extractQuestionMetadata(Statement st) {
//        Map<String, Set<String>> p = new HashMap<>();
//        if (st.getPredicate().hasProperty(RDFS.range))
//            p.put(cz.cvut.sforms.Vocabulary.s_p_has_answer_value_type, Collections.singleton(st.getPredicate().getProperty(RDFS.range).getObject().asResource().getURI()));
//        Model m = extractModel(st);
//        p.put(cz.cvut.sforms.Vocabulary.s_p_has_origin_context, Collections.singleton(m.listStatements(null, RDF.type, OWL.Ontology).next().getSubject().getURI()));
//        return p;
//    }
//
//    public static class OriginPair<Q, A> {
//        public final Q q;
//        public final A a;
//
//        public OriginPair(Q q, A a) {
//            this.q = q;
//            this.a = a;
//        }
//
//        @Override
//        public int hashCode() {
//            if (q == null)
//                return a.hashCode();
//            if (a == null)
//                return q.hashCode();
//            return (q.hashCode() + a.hashCode()) % 21;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (!(o instanceof TransformerImpl.OriginPair))
//                return false;
//            TransformerImpl.OriginPair p = (TransformerImpl.OriginPair) o;
//            return Objects.equals(q, p.q) && Objects.equals(a, p.a);
//        }
//
//        @Override
//        public String toString() {
//            return "<" + q.toString() + ", " + a.toString() + ">";
//        }
//    }
//
//    private boolean isSupportedAnon(Question q) {
//        System.out.println("quest: " + q.getProperties());
//        if (q.getProperties().containsKey(cz.cvut.sforms.Vocabulary.s_p_has_answer_value_type)) {
//            Set<String> types = q.getProperties().get(cz.cvut.sforms.Vocabulary.s_p_has_answer_value_type);
//            return types.contains(cz.cvut.sforms.Vocabulary.s_c_Ask) ||
//                    types.contains(cz.cvut.sforms.Vocabulary.s_c_Construct) ||
//                    types.contains(cz.cvut.sforms.Vocabulary.s_c_Describe) ||
//                    types.contains(cz.cvut.sforms.Vocabulary.s_c_Select);
//        }
//        return false;
//    }
//
//    Model extractModel(Statement st) {
//        Model model = st.getModel(); // Iterate through subgraphs and find model defining st and return it (or IRI)
//        return find(st, model, Optional.empty()).orElse(null);
//    }
//
//    private Optional<Model> find(Statement st, Model m, Optional<Model> res) {
//        if (res.isPresent())
//            return res;
//        if (!m.contains(st))
//            return Optional.empty();
//        if (m instanceof OntModel && ((OntModel) m).listSubModels().toList().stream().anyMatch(sm -> sm.contains(st))) {
//            Optional<Optional<Model>> o = ((OntModel) (m)).listSubModels().toList().stream().map(sm -> find(st, sm, res)).filter(Optional::isPresent).findFirst();
//            if (o.isPresent())
//                return o.get();
//        } else
//            return Optional.of(m);
//        return Optional.empty();
//    }
//}


//class MyListener implements ModelChangedListener
//{
//    public void addedStatement( Statement s )
//    { System.out.println( ">> added statement " + s ); }
//    public void addedStatements( Statement [] statements )
//    { System.out.println( ">> added statement "  ); }
//    public void addedStatements( List statements )
//    { System.out.println( ">> added statement "  ); }
//    public void addedStatements( StmtIterator statements )
//    { System.out.println( ">> added statement "  ); }
//    public void addedStatements( Model m )
//    { System.out.println( ">> added statement "  ); }
//    public void removedStatement( Statement s )
//    { System.out.println( ">> added statement "  ); }
//    public void removedStatements( Statement [] statements )
//    { System.out.println( ">> added statement "  ); }
//    public void removedStatements( List statements )
//    { System.out.println( ">> added statement "  ); }
//    public void removedStatements( StmtIterator statements )
//    { System.out.println( ">> added statement "  ); }
//    public void removedStatements( Model m )
//    { System.out.println( ">> added statement "  ); }
//
//    @Override
//    public void notifyEvent(Model model, Object o) {
//
//    }
//}