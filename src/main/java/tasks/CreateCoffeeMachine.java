package tasks;
import com.fasterxml.jackson.databind.ObjectMapper;
import inventory.InventoryManager;
import model.Beverage;
import model.Input;
import model.Machine;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class CreateCoffeeMachine {
    private static CreateCoffeeMachine coffeeMachine;
    public Input coffeeMachineDetails;
    public InventoryManager inventoryManager;
    private static final int MAX_QUEUED_REQUEST = 1;
    private ThreadPoolExecutor executor;


    public static CreateCoffeeMachine getInstance(final String jsonInput) throws IOException {
        if (coffeeMachine == null) {
            coffeeMachine = new CreateCoffeeMachine(jsonInput);
        }
        return coffeeMachine;
    }

    private CreateCoffeeMachine(String jsonInput) throws IOException {
        System.out.println("New Machine");
        this.coffeeMachineDetails = new ObjectMapper().readValue(jsonInput, Input.class);
        int outlet = coffeeMachineDetails.getMachine().getOutlets().getCount();
        executor = new ThreadPoolExecutor(outlet, outlet, 5000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(MAX_QUEUED_REQUEST));
        executor.setRejectedExecutionHandler(new RejectedTaskHandler());
    }

    public void process() {
        this.inventoryManager = InventoryManager.getInstance();

        Map<String, Integer> ingredients = coffeeMachineDetails.getMachine().getIngredientQuantityMap();

        for (String key : ingredients.keySet()) {
            inventoryManager.addInventory(key, ingredients.get(key));
        }

        HashMap<String, HashMap<String, Integer>> beverages = coffeeMachineDetails.getMachine().getBeverages();
        for (String key : beverages.keySet()) {
            Beverage beverage = new Beverage(key, beverages.get(key));
            coffeeMachine.addBeverageRequest(beverage);
        }
    }

    public void addBeverageRequest(Beverage beverage) {
        BeverageMakerTask task = new BeverageMakerTask(beverage);
        executor.execute(task);
    }

    public void stopMachine() {
        executor.shutdown();
    }

    /**used for testing. */
    public void reset() {
        this.stopMachine();
        this.inventoryManager.resetInventory();
    }
}
