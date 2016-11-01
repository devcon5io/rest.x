package rest.x.cdi;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.List;

import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CommandLine;
import rest.x.Restx;

/**
 * Provides access to the Vert.x CommandLine.
 */
@ApplicationScoped
public class CommandLineProducer {

    @Inject
    private Instance<CLI> cliModel;

    private CommandLine commandLine;

    @PostConstruct
    public void init(){

        final CLI cli = CDIUtils.getInstanceOrDefault(cliModel, () -> CLI.create("default"));
        final List<String> argList = Restx.args().asList();
        this.commandLine = cli.parse(argList);
    }

    @Produces
    public CommandLine getCommandLine(){
        return this.commandLine;
    }

}
