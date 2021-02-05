package og_spipes.persistence.dao.filetree;

import java.util.List;

public class SubTree implements FileTree {
    private List<FileTree> children;
    private String name;

    public SubTree(List<FileTree> children, String name) {
        this.children = children;
        this.name = name;
    }

    public List<FileTree> getChildren() {
        return children;
    }

    public void setChildren(List<FileTree> children) {
        this.children = children;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
