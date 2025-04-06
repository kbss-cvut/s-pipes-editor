package og_spipes.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import og_spipes.config.Constants;
import og_spipes.config.RestConfig;
import og_spipes.model.Vocabulary;
import og_spipes.model.dto.SHACLValidationResultDTO;
import og_spipes.model.dto.ScriptCreateDTO;
import og_spipes.model.spipes.ScriptOntologyDTO;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ScriptControllerTest {

    @Value(Constants.SCRIPTPATH_SPEL)
    private String scriptPaths;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(scriptPaths);
        if(scriptsHomeTmp.exists()){
            FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/hello-world"), scriptsHomeTmp);
        FileUtils.copyFileToDirectory(new File("src/test/resources/SHACL/rule-test-cases/data-without-label.ttl"), scriptsHomeTmp);
        RestConfig.configureObjectMapper(mapper);
    }

        @Test
        @DisplayName("Create script")
        public void testCreateFile() throws Exception {
                ScriptCreateDTO scriptCreateDTO = new ScriptCreateDTO(
                                scriptPaths,
                                "new-ontology",
                                ".ttl",
                                "http://onto.fel.cvut.cz/ontologies/s-pipes/new-ontology",
                                "test-return",
                                "test-create-file");
                String json = mapper.writeValueAsString(scriptCreateDTO);

        this.mockMvc.perform(post("/scripts/create")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        File file = new File(scriptPaths + "/new-ontology.ttl");
        Assertions.assertTrue(file.exists());
    }

    @Test
    @DisplayName("Create script exception")
    public void testCreateFileDuplicateOntologyException() throws Exception {
        this.mockMvc.perform(post("/scripts/create")
                .content(
                        "{" +
                                "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/script-create-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-script-path\": \"" + scriptPaths +"\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-name\": \"" + "hello-world.sms.ttl\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-ontology-uri\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/new-ontology\"" +
                                "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"hello-world.sms.ttl already exists\"}"));
    }

    @Test
    @DisplayName("Delete script or script's directory")
    public void testDeleteScript() throws Exception {
        File file = new File(scriptPaths);
        int initLength = (Objects.requireNonNull(file.list())).length;

        this.mockMvc.perform(post("/scripts/delete")
                .content(
                        "{" +
                                "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-absolute-path\": \""+ scriptPaths +"/hello-world.sms.ttl\"" +
                                "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        int resLength = (Objects.requireNonNull(file.list())).length;
        Assertions.assertEquals(initLength-1, resLength);
    }

    @Test
    @DisplayName("Get script ontologies")
    public void testScriptOntologies() throws Exception {
        File scriptsHomeTmp = new File(scriptPaths);
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/skosify"), scriptsHomeTmp);

        String tmpScripts = scriptPaths + "/skosify.sms.ttl";
        MvcResult mvcResult = this.mockMvc.perform(post("/scripts/ontologies")
                .content(
                        "{" +
                                "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-absolute-path\": \"" + tmpScripts + "\"" +
                                "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        JsonNode rootNode = mapper.readTree(content);
        JsonNode listNode = rootNode.get("@list");

        List<ScriptOntologyDTO> res = new ArrayList<>(
                mapper.readValue(listNode.toString(), new TypeReference<Set<ScriptOntologyDTO>>(){})
        );

        Assertions.assertEquals(4, res.size());
    }

    @Test
    @DisplayName("List script folder")
    public void testScriptsEndpoint() throws Exception {
        this.mockMvc.perform(get("/scripts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\n" +
                                "  \"children\": [\n" +
                                "    {\n" +
                                "      \"children\": [\n" +
                                "        {\n" +
                                "          \"id\": \"" + new File("/tmp/og_spipes/hello-world.sms.ttl").toURI().getPath() + "\",\n" +
                                "          \"name\": \"hello-world.sms.ttl\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"id\": \"" + new File("/tmp/og_spipes/hello-world2.sms.ttl").toURI().getPath() + "\",\n" +
                                "          \"name\": \"hello-world2.sms.ttl\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"id\": \"" + new File("/tmp/og_spipes/hello-world3.sms.ttl").toURI().getPath() + "\",\n" +
                                "          \"name\": \"hello-world3.sms.ttl\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"id\": \"" + new File("/tmp/og_spipes/data-without-label.ttl").toURI().getPath() + "\",\n" +
                                "          \"name\": \"data-without-label.ttl\"\n" +
                                "        }\n" +
                                "      ],\n" +
                                "      \"name\": \"og_spipes\",\n" +
                                "      \"id\": \"" + new File("/tmp/og_spipes").toURI().getPath() + "\" \n" +
                                "    }\n" +
                                "  ],\n" +
                                "  \"name\": \"[SCRIPTS ROOT]\",\n" +
                                "  \"id\": \"\"\n" +
                                "}"
                ));
    }

    @Test
    @DisplayName("Get file moduleTypes")
    public void testGetScriptModuleTypes() throws Exception {
        this.mockMvc.perform(post("/scripts/moduleTypes")
                .content(
                        "{" +
                            "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
                            "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-absolute-path\": \""+ scriptPaths +"/hello-world.sms.ttl\"" +
                        "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        //TODO think about assertion approach
    }

    @Test
    @DisplayName("Add dependency between two modules in script")
    public void testAddModuleDependency() throws Exception {
        //TODO try inline JsonLD from object
        String tmpScripts = scriptPaths + "/hello-world.sms.ttl";
        this.mockMvc.perform(post("/scripts/modules/dependency")
                .content(
                        "{\n" +
                                "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/dependency-dto\",\n" +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-absolute-path\": \"" + tmpScripts + "\",\n" +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-module-uri\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/express-greeding_Return\",\n" +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-target-module-uri\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/bind-firstname\"\n" +
                        "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(204));

        Model modelProject = ModelFactory.createDefaultModel().read("src/test/resources/scripts_test/sample/hello-world/hello-world.sms.ttl");
        Model afterModel = ModelFactory.createDefaultModel().read(tmpScripts);
        long beforeExecutionCount = modelProject.listSubjectsWithProperty(new PropertyImpl(Vocabulary.s_p_next)).toList().stream()
                .map(x -> x.getProperty(new PropertyImpl(Vocabulary.s_p_next))).count();
        long afterExecutionCount = afterModel.listSubjectsWithProperty(new PropertyImpl(Vocabulary.s_p_next)).toList().stream()
                .map(x -> x.getProperty(new PropertyImpl(Vocabulary.s_p_next))).count();
        Assertions.assertEquals(2, beforeExecutionCount, "Before execution count is 2 of next property");
        Assertions.assertEquals(3, afterExecutionCount, "After execution count is 3 of next property");
    }

    @Test
    @DisplayName("Delete module in script")
    public void testDeleteModule() throws Exception {
        String tmpScripts = scriptPaths + "/hello-world.sms.ttl";
        this.mockMvc.perform(post("/scripts/modules/delete")
                .content("{\n" +
                            "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/module-dto\",\n" +
                            "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-absolute-path\": \"" + tmpScripts + "\",\n" +
                            "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-module-uri\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/bind-firstname\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(204));

        Model modelProject = ModelFactory.createDefaultModel().read("src/test/resources/scripts_test/sample/hello-world/hello-world.sms.ttl");
        long beforeCount = modelProject.listSubjects().toList().stream()
                .filter(x -> x.toString().equals("http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/bind-firstname")).count();
        Model afterModel = ModelFactory.createDefaultModel().read(tmpScripts);
        long afterCount = afterModel.listSubjects().toList().stream()
                .filter(x -> x.toString().equals("http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/bind-firstname")).count();
        Assertions.assertEquals(1, beforeCount, "Bind-firstname appear in hello-world.sms.ttl");
        Assertions.assertEquals(0, afterCount, "Bind-firstname is deleted");
    }

    @Test
    @DisplayName("Delete dependency of module in script")
    public void testDeleteDependecyOfModule() throws Exception {
        String tmpScripts = scriptPaths + "/hello-world.sms.ttl";
        this.mockMvc.perform(post("/scripts/modules/dependencies/delete")
                .content(
                        "{\n" +
                                "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/dependency-dto\",\n" +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-absolute-path\": \"" + tmpScripts + "\",\n" +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-module-uri\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/bind-firstname\",\n" +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-target-module-uri\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/construct-greeding\"\n" +
                        "}"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(204));

        Model modelProject = ModelFactory.createDefaultModel().read("src/test/resources/scripts_test/sample/hello-world/hello-world.sms.ttl");
        Model afterModel = ModelFactory.createDefaultModel().read(tmpScripts);
        long beforeExecutionCount = modelProject.listSubjectsWithProperty(new PropertyImpl(Vocabulary.s_p_next)).toList().stream()
                .map(x -> x.getProperty(new PropertyImpl(Vocabulary.s_p_next))).count();
        long afterExecutionCount = afterModel.listSubjectsWithProperty(new PropertyImpl(Vocabulary.s_p_next)).toList().stream()
                .map(x -> x.getProperty(new PropertyImpl(Vocabulary.s_p_next))).count();
        Assertions.assertEquals(2, beforeExecutionCount, "Before deletion is count 2 of next property");
        Assertions.assertEquals(1, afterExecutionCount, "After deletion is count 1 of next property");
    }

    @Test
    @DisplayName("Enforce script SHACL rules on valid script")
    public void restSHACLRulesForValidScript() throws Exception {
        String tmpScripts = scriptPaths + "/hello-world.sms.ttl";
        this.mockMvc.perform(post("/scripts/validate")
                .content(
                        "{" +
                                "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-absolute-path\": \""+tmpScripts+"\"" +
                                "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("Enforce script SHACL rules on valid script")
    public void restSHACLRulesForInvalidScript() throws Exception {
        String tmpScripts = scriptPaths + "/data-without-label.ttl";
        MvcResult mvcResult = this.mockMvc.perform(post("/scripts/validate")
                .content(
                        "{" +
                                "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-absolute-path\": \"" + tmpScripts + "\"" +
                                "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        List<SHACLValidationResultDTO> res = new ArrayList<>(
                mapper.readValue(content, new TypeReference<Set<SHACLValidationResultDTO>>(){})
        );

        Assertions.assertEquals(1, res.size());
        SHACLValidationResultDTO resultDTO = res.get(0);
        Assertions.assertEquals("http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.6/construct-greeding", resultDTO.getModuleURI());
        Assertions.assertEquals("Violation", resultDTO.getSeverityMessage());
        Assertions.assertEquals("Property needs to have at least 1 values, but found 0", resultDTO.getErrorMessage());
//        Assertions.assertEquals("file:/home/jordan/IdeaProjects/s-pipes-newgen/src/main/resources/rules/SHACL/module-requires-rdfs_label.ttl", resultDTO.getRuleURI());
        Assertions.assertEquals("Every modul must have rdfs:label.@en", resultDTO.getRuleComment());
    }

    //TODO test create, remove list ontologies

//    @Test
//    @DisplayName("Remove ontology from script")
//    public void testOntologyRemove() throws Exception {
//        File f = new File("/tmp/og_spipes/hello-world.sms.ttl");
//        ScriptOntologyCreateDTO scriptCreateDTO = new ScriptOntologyCreateDTO(
//                f.getAbsolutePath(),
//                "http://onto.fel.cvut.cz/ontologies/s-pipes-lib"
//        );
//        String json = mapper.writeValueAsString(scriptCreateDTO);
//
//        this.mockMvc.perform(post("/ontology/remove")
//                .content(json)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }

    @AfterEach
    public void after() {
        FileSystemUtils.deleteRecursively(new File(scriptPaths));
    }

}