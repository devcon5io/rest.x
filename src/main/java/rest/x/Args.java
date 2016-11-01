package rest.x;

import static java.util.Collections.unmodifiableList;

import java.util.Arrays;
import java.util.List;

/**
 * Representation of Command Line arguments passed to the launcher.
 */
public class Args {

    private final List<String> args;

    public Args(String[] args){
        this.args = unmodifiableList(Arrays.asList(args));
    }

    public List<String> asList() {
        return args;
    }

    public String[] asArray(){
        return args.toArray(new String[0]);
    }
}
