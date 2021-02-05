package og_spipes.model.filetree;

public class Leaf implements FileTree {
    private String fileAbsolutePath;
    private String name;

    public Leaf(String fileAbsolutePath, String name) {
        this.fileAbsolutePath = fileAbsolutePath;
        this.name = name;
    }

    public String getFileAbsolutePath() {
        return fileAbsolutePath;
    }

    public void setFileAbsolutePath(String fileAbsolutePath) {
        this.fileAbsolutePath = fileAbsolutePath;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
