import model.Machine;
import org.apache.commons.io.FileUtils;
import tasks.CreateCoffeeMachine;

import java.io.File;

public class CoffeeMachineApplication {
    public static void main(String[] args) throws Exception {
        File file = new File(Machine.class.getClassLoader().getResource(args[0]).getFile());
        String jsonInput = FileUtils.readFileToString(file, "UTF-8");
        CreateCoffeeMachine coffeeMachine = CreateCoffeeMachine.getInstance(jsonInput);
        coffeeMachine.process();
        coffeeMachine.reset();
    }
}
