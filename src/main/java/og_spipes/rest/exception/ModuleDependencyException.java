package og_spipes.rest.exception;

public class ModuleDependencyException extends RuntimeException {
    public ModuleDependencyException(String script, String subscript) {
        super(formatMessage(script, subscript));
    }

    private static String formatMessage(String script, String subscript) {
        StringBuilder message = new StringBuilder("Error: BAD_REQUEST\n");

        if (script != null) {
            message.append("Dependency was not found in Open Script\n ");
        }
        if (subscript != null) {
            message.append("But was found in Subscript: ").append(subscript)
                    .append("\nYou should modify the subscript instead");
        }

        return message.toString();
    }
}
