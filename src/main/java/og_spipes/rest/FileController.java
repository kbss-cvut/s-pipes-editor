package og_spipes.rest;

import cz.cvut.kbss.jsonld.exception.TargetTypeException;
import og_spipes.model.view.ErrorMessage;
import og_spipes.service.exception.FileExistsException;
import og_spipes.service.exception.OntologyDuplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/file")
public class FileController {

    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

    @ResponseBody
    @ExceptionHandler({ Exception.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleException(Exception exception) {
        LOG.error("Error FileController: ", exception);
        return new ErrorMessage(exception.getMessage());
    }


    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> generateModuleForm(String file) throws Exception {
        File f = new File(file);
        if(!f.getName().endsWith(".ttl")){
            throw new Exception("Only ttl files are allowed to read.");
        }
        if(!f.exists()){
            throw new FileExistsException("File " + f.getAbsolutePath() + " does not exists.");
        }
        if(f.length() == 0){
            throw new FileExistsException("File " + f.getAbsolutePath() + " is empty.");
        }
        LOG.info("Download a file: " + f.getAbsolutePath() + ", with lenght of: " + f.length());
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + f.getName());
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        Path path = Paths.get(f.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(header)
//                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

}
