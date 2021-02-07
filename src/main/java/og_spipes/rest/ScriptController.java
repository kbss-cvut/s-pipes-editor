/**
 * Copyright (C) 2019 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package og_spipes.rest;

import cz.cvut.kbss.jsonld.JsonLd;
import og_spipes.model.filetree.SubTree;
import og_spipes.model.spipes.ModuleType;
import og_spipes.model.spipes.TestJSONLD;
import og_spipes.service.FileTreeService;
import og_spipes.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.lang.reflect.Array;
import java.util.List;

@RestController
@RequestMapping("/scripts")
public class ScriptController {

    private final FileTreeService fileTreeService;
    private final ScriptService scriptService;

    @Autowired
    public ScriptController(FileTreeService fileTreeService, ScriptService scriptService) {
        this.fileTreeService = fileTreeService;
        this.scriptService = scriptService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public SubTree getScripts() {
        //TODO parametrize later
        return fileTreeService.getTtlFileTree(new File("/home/chlupnoha/IdeaProjects/s-pipes-newgen/src/test/resources/ttl_files"));
    }

    @GetMapping(path = "/dummy", produces = JsonLd.MEDIA_TYPE)
    public TestJSONLD getDummy() {
        return new TestJSONLD("label");
    }

    @GetMapping(path = "/moduleTypes", produces = JsonLd.MEDIA_TYPE)
    public List<ModuleType> getModuleTypes() {
        //TODO parametrize later
        String filepath = "/home/chlupnoha/IdeaProjects/og-spipes/src/test/resources/scripts_test/sample/simple-import/script.ttl";
        List<ModuleType> moduleTypes = scriptService.getModuleTypes(filepath);
        System.out.println("sizu controller: " + moduleTypes.size());

        //WHY??? - cz/cvut/kbss/jopa/model/MultilingualString
        return moduleTypes;
    }

}
