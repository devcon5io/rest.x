package rest.x;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CommandLine;

/**
 *
 */
@ApplicationScoped
public class CommandLineProducer {

    @Inject
    private Instance<Args> args;
    private CommandLine commandLine;

    @PostConstruct
    public void init(){

        List<String> argList = args.isUnsatisfied() ? Collections.emptyList() : args.get().getArgs();
        this.commandLine = CLI.create("default").parse(argList);
    }

    @Produces
    public CommandLine getCommandLine(){
        return this.commandLine;
    }

}
