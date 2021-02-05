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

import og_spipes.model.filetree.SubTree;
import og_spipes.service.FileTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/scripts")
public class ScriptController {

    private final FileTreeService fileTreeService;

    @Autowired
    public ScriptController(FileTreeService fileTreeService) {
        this.fileTreeService= fileTreeService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public SubTree getScripts() {
        //TODO parametrize later
        return fileTreeService.getTtlFileTree(new File("/home/chlupnoha/IdeaProjects/s-pipes-newgen/src/test/resources/ttl_files"));
    }
}
