package og_spipes.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static Long getLastModified(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File normalizedFile(File file){

        try {
            return file.getCanonicalFile().getAbsoluteFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isOutdated(File file, Long lastCalculated){
        long lastModified = FileUtils.getLastModified(file.toPath());
        return lastCalculated == null || lastModified > lastCalculated;
    }

    public abstract static class FileCache{
        protected File file;
        //        protected CacheableField<File, Long> lastModified;
        protected Long lastCalculated;
        public FileCache(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        public void evaluateIfOutdated(){
            if(isOutdated())
                evaluateImpl();
        }

        public boolean isOutdated(){
            return FileUtils.isOutdated(file, lastCalculated);
        }

        public void evaluate(){
            Long lastCalculated = System.currentTimeMillis();
            evaluateImpl();
            this.lastCalculated = lastCalculated;
        }

        protected abstract void evaluateImpl();
    }
}
