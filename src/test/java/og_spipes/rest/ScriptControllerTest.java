package og_spipes.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import cz.cvut.kbss.jsonld.jackson.JsonLdModule;
import og_spipes.model.Vocabulary;
import og_spipes.model.dto.SHACLValidationResultDTO;
import og_spipes.model.spipes.FunctionDTO;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:application.properties")
public class ScriptControllerTest {

    @Value("${scriptPaths}")
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
        mapper.registerModule(new JsonLdModule());
    }

    @Test
    @DisplayName("List script folder")
    public void testScriptsEndpoint() throws Exception {
        this.mockMvc.perform(get("/scripts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"children\":[" +
                                    "{\"file\":\""+ scriptPaths +"/hello-world.sms.ttl\",\"name\":\"hello-world.sms.ttl\"}," +
                                    "{\"file\":\""+ scriptPaths +"/data-without-label.ttl\",\"name\":\"data-without-label.ttl\"}," +
                                    "{\"file\":\""+ scriptPaths +"/hello-world2.sms.ttl\",\"name\":\"hello-world2.sms.ttl\"}" +
                                "],\"name\":\"og_spipes\"}"
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
        Assert.assertEquals("Before execution count is 2 of next property", 2, beforeExecutionCount);
        Assert.assertEquals("After execution count is 3 of next property", 3, afterExecutionCount);
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
        Assert.assertEquals("Bind-firstname appear in hello-world.sms.ttl", 1, beforeCount);
        Assert.assertEquals("Bind-firstname is deleted", 0, afterCount);
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
        Assert.assertEquals("Before deletion is count 2 of next property", 2, beforeExecutionCount);
        Assert.assertEquals("After deletion is count 1 of next property", 1, afterExecutionCount);
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

        Assert.assertEquals(1, res.size());
        SHACLValidationResultDTO resultDTO = res.get(0);
        Assertions.assertEquals("http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.6/construct-greeding", resultDTO.getModuleURI());
        Assertions.assertEquals("Violation", resultDTO.getSeverityMessage());
        Assertions.assertEquals("Property needs to have at least 1 values, but found 0", resultDTO.getErrorMessage());
        Assertions.assertEquals("file:/home/jordan/IdeaProjects/s-pipes-newgen/src/main/resources/rules/SHACL/module-requires-rdfs_label.ttl", resultDTO.getRuleURI());
        Assertions.assertEquals("Every modul must have rdfs:label.@en", resultDTO.getRuleComment());
    }

    @AfterEach
    public void after() {
        FileSystemUtils.deleteRecursively(new File(scriptPaths));
    }

}