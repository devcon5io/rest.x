package rest.x;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

/**
 * Representation of Command Line arguments passed to the launcher.
 */
public class Args {

    private final List<String> args;

    public Args(String[] args){
        this.args = unmodifiableList(asList(args));
    }

    public List<String> getArgs() {
        return args;
    }

    public String[] getArgsArray(){
        return args.toArray(new String[0]);
    }
}
