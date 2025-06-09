package og_spipes.model.dto;

import cz.cvut.sforms.model.AbstractEntity;

import java.util.List;

public class LinkModulesDTO extends AbstractEntity {
    private String absolutePath;
    private List<String> moduleUris;

    // геттеры и сеттеры
    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public List<String> getModuleUris() {
        return moduleUris;
    }

    public void setModuleUris(List<String> moduleUris) {
        this.moduleUris = moduleUris;
    }
}
